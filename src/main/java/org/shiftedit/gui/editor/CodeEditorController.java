package org.shiftedit.gui.editor;

/*
 * #%L
 * CodeEditorController.java - shift - 2013
 * %%
 * Copyright (C) 2013 Gilles Grousset
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
import org.shiftedit.ApplicationContext;
import org.shiftedit.gui.AbstractController;
import org.shiftedit.gui.control.CodeEditor;
import org.shiftedit.task.TaskManagerListener;
import org.shiftedit.workspace.artifact.Document;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXML;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class CodeEditorController extends AbstractController implements EditorController, TaskManagerListener {

    public enum Mode {

        NONE,
        HTML,
        JAVASCRIPT,
        CSS,
        MARKDOWN,
        GROOVY,
        XML
    };

    private Document document;
    private Mode mode;
    @FXML
    private CodeEditor codeEditor;
    private EventHandler<CursorChangedEvent> onCursorChanged;

    private EventHandler<CodeEditor.ContentChangedEvent> codeEditorContentChangedEventHandler;
    private EventHandler<CodeEditor.CursorChangedEvent> codeEditorCursorChangedEventHandler;

    private Task documentOpeningTask;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);

        ApplicationContext.getTaskManager().addListener(this);

        if (document != null) {
            this.setEditorContent();
        }

        // Content changed event
        codeEditorContentChangedEventHandler = (CodeEditor.ContentChangedEvent event) -> {
            String newContent = codeEditor.getContent();
            if (newContent != null) {
                document.setContentAsString(newContent);
            }
        };
        codeEditor.setOnContentChanged(new WeakEventHandler<>(codeEditorContentChangedEventHandler));

        // Cursor changed event (forward event)
        codeEditorCursorChangedEventHandler = (CodeEditor.CursorChangedEvent t) -> {
            if (getOnCursorChanged() != null) {
                getOnCursorChanged().handle(new CursorChangedEvent(EventType.ROOT));
            }
        };
        codeEditor.setOnCursorChanged(new WeakEventHandler<>(codeEditorCursorChangedEventHandler));

        // Code editor i18n
        codeEditor.setSearchPromptLabel(getResourceBundle().getString("editor.search.prompt"));
        codeEditor.setSearchTipLabel(getResourceBundle().getString("editor.search.tip"));
        codeEditor.setReplacePromptLabel(getResourceBundle().getString("editor.replace.prompt"));
        codeEditor.setReplaceTipLabel(getResourceBundle().getString("editor.replace.tip"));
        codeEditor.setReplaceWithPromptLabel(getResourceBundle().getString("editor.replace.with_prompt"));
        codeEditor.setReplaceConfirmYesLabel(getResourceBundle().getString("yes"));
        codeEditor.setReplaceConfirmNoLabel(getResourceBundle().getString("no"));
        codeEditor.setReplaceConfirmStopLabel(getResourceBundle().getString("stop"));
    }

    @Override
    public void onTaskStarted(Task task) {

    }

    @Override
    public void onTaskFailed(Task task) {

    }

    @Override
    public void onTaskSucceeded(Task task) {

        // Document opening
        if (task == documentOpeningTask) {
            if (codeEditor != null) {
                Platform.runLater(() -> {
                    codeEditor.setContent(document.getContentAsString());
                    codeEditor.setDisable(false);
                });
                
            }
        }

    }

    /**
     * @return the document
     */
    @Override
    public Document getDocument() {
        return document;
    }

    /**
     * Set editor content from the document content. If the document is not
     * opened yet, operation is asynchronous.
     */
    private void setEditorContent() {

        if (document.isOpened()) {
            if (codeEditor != null) {
                codeEditor.setContent(document.getContentAsString());
            }
        } else {

            codeEditor.setDisable(true);
            documentOpeningTask = new Task() {
                @Override
                protected Object call() throws Exception {

                    updateTitle(String.format(getResourceBundle().getString("task.opening_file"), document.getName()));
                    document.open();
                    updateProgress(1, 1);

                    return document;
                }
            };

            ApplicationContext.getTaskManager().addTask(documentOpeningTask);
        }

    }

    /**
     * @param document the document to set
     */
    public void setDocument(Document document) {

        this.document = document;

        this.setEditorContent();
    }

    /**
     * @return the mode
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(Mode mode) {
        this.mode = mode;

        switch (mode) {
            case HTML:
                codeEditor.setMode(CodeEditor.Mode.HTML_MIXED);
                break;
            case JAVASCRIPT:
                codeEditor.setMode(CodeEditor.Mode.JAVASCRIPT);
                break;
            case CSS:
                codeEditor.setMode(CodeEditor.Mode.CSS);
                break;
            case MARKDOWN:
                codeEditor.setMode(CodeEditor.Mode.MARKDOWN);
                break;
            case GROOVY:
                codeEditor.setMode(CodeEditor.Mode.GROOVY);
                break;
            case XML:
                codeEditor.setMode(CodeEditor.Mode.XML);
                break;
            default:
                codeEditor.setMode(CodeEditor.Mode.NONE);
        }
    }

    @Override
    public void setOnCursorChanged(EventHandler<CursorChangedEvent> onCursorChanged) {
        this.onCursorChanged = onCursorChanged;
    }

    @Override
    public EventHandler<CursorChangedEvent> getOnCursorChanged() {
        return onCursorChanged;
    }

    @Override
    public void close() {

        document.close();
    }

    @Override
    public boolean canUndo() {
        return codeEditor.getHistorySize().getUndo() > 0;
    }

    @Override
    public boolean canRedo() {
        return codeEditor.getHistorySize().getRedo() > 0;
    }

    @Override
    public void undo() {
        codeEditor.undo();
    }

    @Override
    public void redo() {
        codeEditor.redo();
    }

    @Override
    public void cut() {
        codeEditor.cut();
    }

    @Override
    public void copy() {
        codeEditor.copy();
    }

    @Override
    public void paste() {
        codeEditor.paste();
    }

    @Override
    public void selectAll() {
        codeEditor.selectAll();
    }

    @Override
    public void contentAssist() {
        codeEditor.contentAssist();
    }

    @Override
    public boolean canContentAssist() {
        return codeEditor.canContentAssist();
    }

    @Override
    public boolean canSearch() {
        return true;
    }

    @Override
    public void find() {
        codeEditor.find();
    }

    @Override
    public void findNext() {
        codeEditor.findNext();
    }

    @Override
    public void findPrevious() {
        codeEditor.findPrevious();
    }

    @Override
    public void replace() {
        codeEditor.replace();
    }

    @Override
    public void replaceAll() {
        codeEditor.replace();
    }

    @Override
    public void clearHistory() {
        codeEditor.clearHistory();
    }

    @Override
    public CursorPosition getCursorPosition() {
        CodeEditor.CursorPosition position = codeEditor.getCursorPosition();
        return new CursorPosition(position.getLine(), position.getCh());
    }

}
