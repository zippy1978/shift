/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.backelite.shift.gui.editor;

import com.backelite.shift.workspace.artifact.Document;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;

/*
 * #%L
 * EditorController.java - shift - 2013
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

/**
 * Editor controller interface.
 * All editor controllers must implement this interface.
 * @author ggrousset
 */
public interface EditorController {
    
    public void close();
    
    public Document getDocument();
    
    public void find();
    
    public void findPrevious();
    
    public void findNext();
    
    public void replace();
    
    public void replaceAll();
    
    public boolean canSearch();
    
    public boolean canUndo();
    
    public boolean canRedo();
    
    public void undo();
    
    public void redo();
    
    /**
     * Cut editor selection.
     */
    public void cut();
    
    /**
     * Copy editor selection to clipboard.
     */
    public void copy();
    
    /**
     * Paste clipboard content into the editor.
     */
    public void paste();
    
    public void selectAll();
    
    /**
     * Check if the editor supports content assist
     * @return 
     */
    public boolean canContentAssist();
    
    public void contentAssist();
    
    public void clearHistory();
    
    public CursorPosition getCursorPosition();
    
    public void setOnCursorChanged(EventHandler<CursorChangedEvent> onCursorChanged);
    public EventHandler<CursorChangedEvent> getOnCursorChanged();
    
    /**
     * Cursor changed event.
     */
    public class CursorChangedEvent extends Event {

        public CursorChangedEvent(EventType<? extends Event> et) {
            super(et);
        }

        public CursorChangedEvent(EventType<? extends Event> et, Document document) {
            super(et);
        }
    }
}


