package com.backelite.shift.gui.editor;

/*
 * #%L
 * CodeEditorController.java - shift - 2013
 * %%
 * Copyright (C) 2013 Gilles Grousset
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
import com.backelite.shift.gui.AbstractController;
import com.backelite.shift.gui.control.CodeEditor;
import com.backelite.shift.workspace.artifact.Document;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class CodeEditorController extends AbstractController implements EditorController {

    public enum Mode {
        NONE,
        HTML,
        JAVASCRIPT,
        CSS,
        MARKDOWN,
        GROOVY
    };
    
    private Document document;
    private Mode mode;
    @FXML
    private CodeEditor codeEditor;
    private EventHandler<CursorChangedEvent> onCursorChanged;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);

        if (document != null) {
            codeEditor.setContent(document.getContentAsString());
        }
        
        // Content changed event
        codeEditor.setOnContentChanged(new EventHandler<CodeEditor.ContentChangedEvent>() {

                public void handle(CodeEditor.ContentChangedEvent event) {
                    
                    String newContent = codeEditor.getContent();
                    if (newContent != null) {
                        document.setContentAsString(newContent);
                    }
                    
                }
            
        });
        
        // Cursor changed event (forward event)
        codeEditor.setOnCursorChanged(new EventHandler<CodeEditor.CursorChangedEvent>() {

            public void handle(CodeEditor.CursorChangedEvent t) {
                if (getOnCursorChanged() != null) {
                    getOnCursorChanged().handle(new CursorChangedEvent(new EventType<CursorChangedEvent>()));
                }
            }
        });
        
    }

    /**
     * @return the document
     */
    @Override
    public Document getDocument() {
        return document;
    }

    /**
     * @param document the document to set
     */
    public void setDocument(Document document) {
        this.document = document;

        if (codeEditor != null) {
            codeEditor.setContent(document.getContentAsString());
        }
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
            default:
                codeEditor.setMode(CodeEditor.Mode.NONE);
        }
    }

    public void setOnCursorChanged(EventHandler<CursorChangedEvent> onCursorChanged) {
        this.onCursorChanged = onCursorChanged;
    }
    
    public EventHandler<CursorChangedEvent> getOnCursorChanged() {
        return onCursorChanged;
    }

    public void close() {
        document.close();
    }

    public boolean canUndo() {
        return codeEditor.getHistorySize().getUndo() > 0;
    }

    public boolean canRedo() {
        return codeEditor.getHistorySize().getRedo() > 0;
    }

    public void undo() {
        codeEditor.undo();
    }

    public void redo() {
        codeEditor.redo();
    }

    public void clearHistory() {
        codeEditor.clearHistory();
    }

    public CursorPosition getCursorPosition() {
        CodeEditor.CursorPosition position = codeEditor.getCursorPosition();
        return new CursorPosition(position.getLine(), position.getCh());
    }
    
    
    
}
