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

/**
 * Editor controller interface.
 * All editor controllers must implement this interface.
 * @author ggrousset
 */
public interface EditorController {
    
    public void close();
    
    public Document getDocument();
    
    public boolean canUndo();
    
    public boolean canRedo();
    
    public void undo();
    
    public void redo();
    
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


