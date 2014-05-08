package com.backelite.shift.gui.control;

/*
 * #%L
 * CodeEditor.java - shift - 2013
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.concurrent.Worker.State;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.event.WeakEventHandler;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class CodeEditor extends AnchorPane {

    /**
     * @return the searchPromptLabel
     */
    public String getSearchPromptLabel() {
        return searchPromptLabel;
    }

    /**
     * @param searchPromptLabel the searchPromptLabel to set
     */
    public void setSearchPromptLabel(String searchPromptLabel) {
        this.searchPromptLabel = searchPromptLabel;
    }

    /**
     * @return the searchTipLabel
     */
    public String getSearchTipLabel() {
        return searchTipLabel;
    }

    /**
     * @param searchTipLabel the searchTipLabel to set
     */
    public void setSearchTipLabel(String searchTipLabel) {
        this.searchTipLabel = searchTipLabel;
    }

    /**
     * @return the replacePromptLabel
     */
    public String getReplacePromptLabel() {
        return replacePromptLabel;
    }

    /**
     * @param replacePromptLabel the replacePromptLabel to set
     */
    public void setReplacePromptLabel(String replacePromptLabel) {
        this.replacePromptLabel = replacePromptLabel;
    }

    /**
     * @return the replaceWithPromptLabel
     */
    public String getReplaceWithPromptLabel() {
        return replaceWithPromptLabel;
    }

    /**
     * @param replaceWithPromptLabel the replaceWithPromptLabel to set
     */
    public void setReplaceWithPromptLabel(String replaceWithPromptLabel) {
        this.replaceWithPromptLabel = replaceWithPromptLabel;
    }

    /**
     * @return the replaceTipLabel
     */
    public String getReplaceTipLabel() {
        return replaceTipLabel;
    }

    /**
     * @param replaceTipLabel the replaceTipLabel to set
     */
    public void setReplaceTipLabel(String replaceTipLabel) {
        this.replaceTipLabel = replaceTipLabel;
    }

    /**
     * @return the replaceConfirmPromptLabel
     */
    public String getReplaceConfirmPromptLabel() {
        return replaceConfirmPromptLabel;
    }

    /**
     * @param replaceConfirmPromptLabel the replaceConfirmPromptLabel to set
     */
    public void setReplaceConfirmPromptLabel(String replaceConfirmPromptLabel) {
        this.replaceConfirmPromptLabel = replaceConfirmPromptLabel;
    }

    /**
     * @return the replaceConfirmYesLabel
     */
    public String getReplaceConfirmYesLabel() {
        return replaceConfirmYesLabel;
    }

    /**
     * @param replaceConfirmYesLabel the replaceConfirmYesLabel to set
     */
    public void setReplaceConfirmYesLabel(String replaceConfirmYesLabel) {
        this.replaceConfirmYesLabel = replaceConfirmYesLabel;
    }

    /**
     * @return the replaceConfirmNoLabel
     */
    public String getReplaceConfirmNoLabel() {
        return replaceConfirmNoLabel;
    }

    /**
     * @param replaceConfirmNoLabel the replaceConfirmNoLabel to set
     */
    public void setReplaceConfirmNoLabel(String replaceConfirmNoLabel) {
        this.replaceConfirmNoLabel = replaceConfirmNoLabel;
    }

    /**
     * @return the replaceConfirmStopLabel
     */
    public String getReplaceConfirmStopLabel() {
        return replaceConfirmStopLabel;
    }

    /**
     * @param replaceConfirmStopLabel the replaceConfirmStopLabel to set
     */
    public void setReplaceConfirmStopLabel(String replaceConfirmStopLabel) {
        this.replaceConfirmStopLabel = replaceConfirmStopLabel;
    }

    public enum Mode {

        NONE,
        HTML_MIXED,
        JAVASCRIPT,
        CSS,
        MARKDOWN,
        GROOVY,
        XML
    };
    private static final String HTML_TEMPLATE_NAME = "/editor-template.html";
    private static final String WEB_RESOURCES_PATH = "/codemirror";
    private Mode mode = Mode.NONE;
    private WebView webView;
    private String initialContent;
    private EventHandler<ContentChangedEvent> onContentChanged;
    private EventHandler<CursorChangedEvent> onCursorChanged;
    private String contentAssistFunction = null;
    
    private EventHandler<KeyEvent> webViewKeyEventHandler;
    private ChangeListener<State> webViewStateChangeListener;
    
    private String searchPromptLabel = "Search:";
    private String searchTipLabel = "(Use /re/ syntax for regexp search)";
    private String replacePromptLabel = "Replace:";
    private String replaceWithPromptLabel = "With:";
    private String replaceTipLabel = "(Use /re/ syntax for regexp search)";
    private String replaceConfirmPromptLabel = "Replace?";
    private String replaceConfirmYesLabel = "Yes";
    private String replaceConfirmNoLabel = "No";
    private String replaceConfirmStopLabel = "Stop";
    

    public CodeEditor() {
        super();

        webView = new WebView();
        this.getChildren().add(webView);
        AnchorPane.setTopAnchor(webView, 0.0);
        AnchorPane.setBottomAnchor(webView, 0.0);
        AnchorPane.setLeftAnchor(webView, 0.0);
        AnchorPane.setRightAnchor(webView, 0.0);
        webViewStateChangeListener = (ObservableValue<? extends State> ov, State oldState, State newState) -> {
            if (newState == State.SUCCEEDED) {
                
                // Inject bridge object
                JSObject jsobj = (JSObject) webView.getEngine().executeScript("window");
                jsobj.setMember("bridge", new JSBridge());
                
                // If content was set before page is ready : apply it now
                if (initialContent != null) {
                    setContent(initialContent);
                }
            }
        };
        webView.getEngine().getLoadWorker().stateProperty().addListener(new WeakChangeListener<>(webViewStateChangeListener));

        // Consume default clipboard shortcuts to prevent double call
        webViewKeyEventHandler = (KeyEvent keyEvent) -> {
            if ((keyEvent.getCode() == KeyCode.V || keyEvent.getCode() == KeyCode.C || keyEvent.getCode() == KeyCode.X) && (keyEvent.isMetaDown() || keyEvent.isControlDown())) {
                keyEvent.consume();
            }
        };
        this.webView.addEventFilter(KeyEvent.ANY, new WeakEventHandler<>(webViewKeyEventHandler));
        
        this.refresh();

    }
   
    private String buildInlineScriptsForMode() {

        StringBuilder inlineScripts = new StringBuilder();
        
        contentAssistFunction = null;
        
        switch (mode) {
            case HTML_MIXED:
                inlineScripts.append(getFileContent(WEB_RESOURCES_PATH + "/mode/xml/xml.js"));
                inlineScripts.append(getFileContent(WEB_RESOURCES_PATH + "/mode/javascript/javascript.js"));
                inlineScripts.append(getFileContent(WEB_RESOURCES_PATH + "/mode/css/css.js"));
                inlineScripts.append(getFileContent(WEB_RESOURCES_PATH + "/mode/htmlmixed/htmlmixed.js"));
                inlineScripts.append(getFileContent(WEB_RESOURCES_PATH + "/addon/hint/html-hint.js"));
                inlineScripts.append(getFileContent(WEB_RESOURCES_PATH + "/addon/hint/javascript-hint.js"));
                contentAssistFunction = "CodeMirror.showHint(cm, CodeMirror.htmlHint);";
                break;
            case JAVASCRIPT:
                inlineScripts.append(getFileContent(WEB_RESOURCES_PATH + "/mode/javascript/javascript.js"));
                inlineScripts.append(getFileContent(WEB_RESOURCES_PATH + "/addon/hint/javascript-hint.js"));
                contentAssistFunction = "CodeMirror.showHint(cm, CodeMirror.javascriptHint);";
                break;
            case CSS:
                inlineScripts.append(getFileContent(WEB_RESOURCES_PATH + "/mode/css/css.js"));
                break;
            case MARKDOWN:
                inlineScripts.append(getFileContent(WEB_RESOURCES_PATH + "/mode/markdown/markdown.js"));
                break;
            case GROOVY:
                inlineScripts.append(getFileContent(WEB_RESOURCES_PATH + "/mode/groovy/groovy.js"));
                break;
            case XML:
                inlineScripts.append(getFileContent(WEB_RESOURCES_PATH + "/mode/xml/xml.js"));
                 inlineScripts.append(getFileContent(WEB_RESOURCES_PATH + "/addon/hint/xml-hint.js"));
                contentAssistFunction = "CodeMirror.showHint(cm, CodeMirror.xmlHint);";
                break;
        }  
      
        return inlineScripts.toString();

    }

    /**
     * Build editor HTML page.
     *
     * @return String
     */
    private String buildPage() {

        String template = this.getFileContent(HTML_TEMPLATE_NAME);

        // Styles
        StringBuilder inlineStyles = new StringBuilder();
        inlineStyles.append(getFileContent(WEB_RESOURCES_PATH + "/lib/codemirror.css"));
        inlineStyles.append(getFileContent(WEB_RESOURCES_PATH + "/theme/ambiance.css"));
        inlineStyles.append(getFileContent(WEB_RESOURCES_PATH + "/addon/hint/show-hint.css"));

        // Scripts
        StringBuilder inlineScripts = new StringBuilder();
        inlineScripts.append(getFileContent(WEB_RESOURCES_PATH + "/lib/codemirror.js"));
        inlineScripts.append(getFileContent(WEB_RESOURCES_PATH + "/addon/hint/show-hint.js"));
        inlineScripts.append(buildInlineScriptsForMode());

        inlineScripts.append(getFileContent(WEB_RESOURCES_PATH + "/addon/edit/closebrackets.js"));
        inlineScripts.append(getFileContent(WEB_RESOURCES_PATH + "/addon/edit/closetag.js"));
        inlineScripts.append(getFileContent(WEB_RESOURCES_PATH + "/addon/edit/continuecomment.js"));
        inlineScripts.append(getFileContent(WEB_RESOURCES_PATH + "/addon/edit/continuelist.js"));
        inlineScripts.append(getFileContent(WEB_RESOURCES_PATH + "/addon/edit/matchbrackets.js"));
        
        inlineStyles.append(getFileContent(WEB_RESOURCES_PATH + "/addon/dialog/dialog.css"));
        inlineScripts.append(getFileContent(WEB_RESOURCES_PATH + "/addon/dialog/dialog.js"));
        
        inlineScripts.append(getFileContent(WEB_RESOURCES_PATH + "/addon/search/search.js")
                .replace("'Search: <input type=\"text\" style=\"width: 10em\"/> <span style=\"color: #888\">(Use /re/ syntax for regexp search)</span>';", 
                String.format("'%s <input type=\"text\" style=\"width: 15em\" class=\"search\"/> <span style=\"color: #888\">%s</span>';", getSearchPromptLabel(), getSearchTipLabel()))
               
                .replace("'Replace: <input type=\"text\" style=\"width: 10em\"/> <span style=\"color: #888\">(Use /re/ syntax for regexp search)</span>';",
                String.format("'%s <input type=\"text\" style=\"width: 15em\" class=\"search\"/> <span style=\"color: #888\">%s</span>';", getReplacePromptLabel(), getReplaceTipLabel()))
                
                .replace("'With: <input type=\"text\" style=\"width: 10em\"/>'", 
                String.format("'With: <input type=\"text\" style=\"width: 15em\" class=\"search\"/>'", getReplaceWithPromptLabel()))
                
                .replace("\"Replace? <button>Yes</button> <button>No</button> <button>Stop</button>\"", 
                String.format("\"%s <button>%s</button> <button>%s</button> <button>%s</button>\"", getReplaceConfirmPromptLabel(), getReplaceConfirmYesLabel(), getReplaceConfirmNoLabel(), getReplaceConfirmStopLabel()))
                );
        inlineScripts.append(getFileContent(WEB_RESOURCES_PATH + "/addon/search/searchcursor.js"));
        inlineScripts.append(getFileContent(WEB_RESOURCES_PATH + "/addon/search/match-highlighter.js"));


        String editorMode = "";
        switch (mode) {
            case HTML_MIXED:
                editorMode = "htmlmixed";
                break;
            case JAVASCRIPT:
                editorMode = "javascript";
                break;
            case CSS:
                editorMode = "css";
                break;
            case MARKDOWN:
                editorMode = "markdown";
                break;
            case GROOVY:
                editorMode = "groovy";
            case XML:
                editorMode = "xml";
        }

        return template.replace("[mode]", editorMode).replace("[inline-styles]", inlineStyles.toString()).replace("[inline-scripts]", inlineScripts.toString());
    }

    /**
     * Refresh editor.
     */
    private void refresh() {

        webView.getEngine().loadContent(buildPage());
    }

    /**
     * Read content of a file on the class path and store it into a String.
     *
     * @param filename Filename
     * @return String
     */
    private String getFileContent(String filename) {
        InputStream inStream = this.getClass().getResourceAsStream(filename);

        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
        StringBuilder builder = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
        } catch (IOException e) {
            // Nothing
        } finally {
            try {
                inStream.close();
            } catch (IOException e) {
                // Nothing
            }
        }

        return builder.toString();
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

        this.refresh();
    }

    /**
     * @return the onContentChanged
     */
    public EventHandler<ContentChangedEvent> getOnContentChanged() {
        return onContentChanged;
    }

    /**
     * @param onContentChanged the onContentChanged to set
     */
    public void setOnContentChanged(EventHandler<ContentChangedEvent> onContentChanged) {
        this.onContentChanged = onContentChanged;
    }

    /**
     * @return the onCursorChanged
     */
    public EventHandler<CursorChangedEvent> getOnCursorChanged() {
        return onCursorChanged;
    }

    /**
     * @param onCursorChanged the onCursorChanged to set
     */
    public void setOnCursorChanged(EventHandler<CursorChangedEvent> onCursorChanged) {
        this.onCursorChanged = onCursorChanged;
    }

    private JSObject getCodeMirrorJSInstance() {

        try {
            return (JSObject) webView.getEngine().executeScript("cm");
        } catch (Exception e) {
            return null;
        }
    }

    public void setContent(String content) {

        JSObject cmInstance = this.getCodeMirrorJSInstance();
        if (cmInstance != null) {
            cmInstance.call("setValue", content);
            JSObject document = (JSObject) cmInstance.call("getDoc");
            document.call("clearHistory");
            initialContent = null;
        } else {
            // If code editor is not ready (view is not loaded yet) : store into initialContent
            initialContent = content;
        }
    }

    public String getContent() {

        String content = "";

        JSObject cmInstance = this.getCodeMirrorJSInstance();
        if (cmInstance != null) {
            content = (String) cmInstance.call("getValue");
        }

        return content;
    }
    
    private boolean isDocumentFocused() {
        return (Boolean) webView.getEngine().executeScript("document.activeElement instanceof HTMLTextAreaElement");
    }

    public void cut() {

        // First copy
        this.copy();

        // Then remove
        JSObject cmInstance = this.getCodeMirrorJSInstance();
        if (cmInstance != null) {
            JSObject document = (JSObject) cmInstance.call("getDoc");
            document.call("replaceSelection", "");
        }
    }

    public void copy() {

        JSObject cmInstance = this.getCodeMirrorJSInstance();
        if (cmInstance != null) {
            JSObject document = (JSObject) cmInstance.call("getDoc");
            Boolean selected = (Boolean) document.call("somethingSelected");
            if (selected) {
                String selection = (String) document.call("getSelection");
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(selection);
                clipboard.setContent(content);
            }
        }
    }

    public void paste() {
        
        Clipboard clipboard = Clipboard.getSystemClipboard();
        String content = (String) clipboard.getContent(DataFormat.PLAIN_TEXT);
        if (content != null) {
            JSObject cmInstance = this.getCodeMirrorJSInstance();
            if (cmInstance != null) {
                if (this.isDocumentFocused()) {
                    JSObject document = (JSObject) cmInstance.call("getDoc");
                    CursorPosition cursorPosition = this.getCursorPosition();
                    Boolean selected = (Boolean) document.call("somethingSelected");
                    if (selected) {
                        // If something is selected : replace selection
                        document.call("replaceSelection", content);
                    } else {
                        JSObject position = (JSObject) webView.getEngine().executeScript(String.format("endPos = {ch: %d, line: %d}", cursorPosition.getCh() - 1, cursorPosition.getLine() - 1));
                        document.call("replaceRange", content, position);
                    }
                } else {
                    // Document is not focused : the search box is opened
                    webView.getEngine().executeScript(String.format("document.activeElement.value = '%s'", content));
                }
            }
        }
    }

    public void selectAll() {

        JSObject cmInstance = this.getCodeMirrorJSInstance();
        if (cmInstance != null) {
            JSObject document = (JSObject) cmInstance.call("getDoc");
            Integer lastLine = (Integer) document.call("lastLine");
            Integer lastLineLength = ((String) document.call("getLine", lastLine)).length();
            JSObject startPos = (JSObject) webView.getEngine().executeScript("startPos = {ch: 0, line: 0}");
            JSObject endPos = (JSObject) webView.getEngine().executeScript(String.format("endPos = {ch: %d, line: %d}", lastLineLength, lastLine));
            document.call("setSelection", startPos, endPos);

        }

    }
    
    public void find() {
        JSObject cmInstance = this.getCodeMirrorJSInstance();
        if (cmInstance != null) {
            JSObject commands =  (JSObject) webView.getEngine().executeScript("CodeMirror.commands");
            commands.call("find", cmInstance);
        }
    }
    
    public void findNext() {
        JSObject cmInstance = this.getCodeMirrorJSInstance();
        if (cmInstance != null) {
            JSObject commands =  (JSObject) webView.getEngine().executeScript("CodeMirror.commands");
            commands.call("findNext", cmInstance);
        }
    }
    
    public void findPrevious() {
        JSObject cmInstance = this.getCodeMirrorJSInstance();
        if (cmInstance != null) {
            JSObject commands =  (JSObject) webView.getEngine().executeScript("CodeMirror.commands");
            commands.call("findPrev", cmInstance);
        }
    }
    
    
    public void replace() {
        JSObject cmInstance = this.getCodeMirrorJSInstance();
        if (cmInstance != null) {
            JSObject commands =  (JSObject) webView.getEngine().executeScript("CodeMirror.commands");
            commands.call("replace", cmInstance);
        }
    }
    
    public void replaceAll() {
        JSObject cmInstance = this.getCodeMirrorJSInstance();
        if (cmInstance != null) {
            JSObject commands =  (JSObject) webView.getEngine().executeScript("CodeMirror.commands");
            commands.call("replaceAll", cmInstance);
        }
    }
    
    public boolean canContentAssist() {
        return (contentAssistFunction != null);
    }
    
    public void contentAssist() {
        
        JSObject cmInstance = this.getCodeMirrorJSInstance();
        if (cmInstance != null) {
            webView.getEngine().executeScript(contentAssistFunction);
        }
    }

    public void undo() {

        JSObject cmInstance = this.getCodeMirrorJSInstance();
        if (cmInstance != null) {
            JSObject document = (JSObject) cmInstance.call("getDoc");
            document.call("undo");
        }
    }

    public void redo() {

        JSObject cmInstance = this.getCodeMirrorJSInstance();
        if (cmInstance != null) {
            JSObject document = (JSObject) cmInstance.call("getDoc");
            document.call("redo");
        }
    }

    public void clearHistory() {

        JSObject cmInstance = this.getCodeMirrorJSInstance();
        if (cmInstance != null) {
            JSObject document = (JSObject) cmInstance.call("getDoc");
            document.call("clearHistory");
        }
    }

    public HistorySize getHistorySize() {
        HistorySize result = new HistorySize();

        JSObject cmInstance = this.getCodeMirrorJSInstance();
        if (cmInstance != null) {
            JSObject document = (JSObject) cmInstance.call("getDoc");
            JSObject historySize = (JSObject) document.call("historySize");
            result.setUndo((Integer) historySize.getMember("undo"));
            result.setRedo((Integer) historySize.getMember("redo"));
        }

        return result;
    }

    public CursorPosition getCursorPosition() {

        CursorPosition result = new CursorPosition();

        JSObject cmInstance = this.getCodeMirrorJSInstance();
        if (cmInstance != null) {
            JSObject document = (JSObject) cmInstance.call("getDoc");
            JSObject cursor = (JSObject) document.call("getCursor");
            // Code mirror returns first line as 0 = must add 1
            result.setLine((Integer) cursor.getMember("line") + 1);
            result.setCh((Integer) cursor.getMember("ch") + 1);
        }

        return result;
    }

    public class HistorySize {

        private int redo;
        private int undo;

        /**
         * @return the redo
         */
        public int getRedo() {
            return redo;
        }

        /**
         * @param redo the redo to set
         */
        public void setRedo(int redo) {
            this.redo = redo;
        }

        /**
         * @return the undo
         */
        public int getUndo() {
            return undo;
        }

        /**
         * @param undo the undo to set
         */
        public void setUndo(int undo) {
            this.undo = undo;
        }
    }

    /**
     * Cursor position
     */
    public class CursorPosition {

        private long line = 1;
        private long ch = 1;

        /**
         * @return the line
         */
        public long getLine() {
            return line;
        }

        /**
         * @param line the line to set
         */
        public void setLine(long line) {
            this.line = line;
        }

        /**
         * @return the char
         */
        public long getCh() {
            return ch;
        }

        /**
         * @param ch the char to set
         */
        public void setCh(long ch) {
            this.ch = ch;
        }
    }

    /**
     * Javascript bridge object injected in the HTML page.
     */
    public class JSBridge {

        public void changed() {

            // Notify content changed event
            if (CodeEditor.this.getOnContentChanged() != null) {
                CodeEditor.this.getOnContentChanged().handle(new ContentChangedEvent(EventType.ROOT));
            }
        }

        public void cursorActivity() {

            // Notify cursor changed event
            if (CodeEditor.this.getOnCursorChanged() != null) {
                CodeEditor.this.getOnCursorChanged().handle(new CursorChangedEvent(EventType.ROOT));
            }
        }
    }

    /**
     * Content changed event.
     */
    public class ContentChangedEvent extends Event {

        protected ContentChangedEvent(EventType<? extends Event> et) {
            super(et);
        }
    }

    public class CursorChangedEvent extends Event {

        protected CursorChangedEvent(EventType<? extends Event> et) {
            super(et);
        }
    }
}
