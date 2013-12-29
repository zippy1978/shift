/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.backelite.shift.gui.preview;

/*
 * #%L
 * PreviewController.java - shift - 2013
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
import com.backelite.shift.gui.editor.EditorController;
import com.backelite.shift.plugin.PreviewFactory;
import com.backelite.shift.workspace.artifact.Document;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Tab;
import javafx.stage.Stage;

/**
 * Preview controller interface. All preview controllers must implement this
 * interface.
 *
 * @author ggrousset
 */
public interface PreviewController {

    public void setDocument(Document document);

    public Document getDocument();

    public Stage getParentStage();

    public void setParentStage(Stage parentStage);
    
    public void setFactory(PreviewFactory factory);
    
    /**
     * Return a change listener to track the active editor.
     * If the preview does not provide the ability to track the active editor, return null.
     * @param editorController 
     */
    public ChangeListener<EditorController> getActiveEditorChangeListener();
    
    /**
     * Close the current window.
     */
    public void close();
}
