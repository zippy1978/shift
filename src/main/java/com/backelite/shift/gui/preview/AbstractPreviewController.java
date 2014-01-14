package com.backelite.shift.gui.preview;

/*
 * #%L
 * AbstractPreviewController.java - shift - 2013
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
import com.backelite.shift.gui.dialog.AbstractDialogController;
import com.backelite.shift.gui.editor.EditorController;
import com.backelite.shift.plugin.PreviewFactory;
import com.backelite.shift.util.FileUtils;
import com.backelite.shift.workspace.artifact.Document;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.Stage;

/**
 * Abstract base implementaiton of PreviewController.
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public abstract class AbstractPreviewController extends AbstractDialogController implements PreviewController, Observer {

    protected Document document;
    protected ChangeListener<EditorController> editorChangeListener;
    private boolean activeDocumentTrackingEnabled = true;
    
    /**
     * Factory that created the preview.
     */
    private PreviewFactory factory;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        
        // Editor change listener
        editorChangeListener = new ChangeListener<EditorController>() {

            @Override
            public void changed(ObservableValue<? extends EditorController> ov, EditorController t, EditorController t1) {
                
                // Set new document
                if (activeDocumentTrackingEnabled && isDocumentSupported(t1.getDocument())) {
                    setDocument(t1.getDocument());
                }
            }
        };
    }
    
    /**
     * Check if a given document is supported by the current preview controller.
     * @param document Document to test
     * @return true if document is supported, false otherwise
     */
    protected boolean isDocumentSupported(Document document) {
        String extension = FileUtils.getFileExtension(document.getName());
        return factory.getSupportedExtensions().contains(extension.toLowerCase());
    }

    /**
     * Called to refresh preview. Implement to render preview.
     */
    protected abstract void refresh();

    /**
     * Track document updates.
     *
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {

        // Something in the project was updated
        // Refresh view
        this.refresh();

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
    @Override
    public void setDocument(Document document) {
        this.document = document;

        if (this.document != null) {
            this.document.getProject().addObserver(this);
            this.refresh();
        }
    }
    

    @Override
    public ChangeListener<EditorController> getActiveEditorChangeListener() {
        return editorChangeListener;
    }

    /**
     * @return the activeDocumentTrackingEnabled
     */
    public boolean isActiveDocumentTrackingEnabled() {
        return activeDocumentTrackingEnabled;
    }

    /**
     * @param activeDocumentTrackingEnabled the activeDocumentTrackingEnabled to set
     */
    public void setActiveDocumentTrackingEnabled(boolean activeDocumentTrackingEnabled) {
        this.activeDocumentTrackingEnabled = activeDocumentTrackingEnabled;
    }

    @Override
    public void setFactory(PreviewFactory factory) {
        this.factory = factory;
    }
    
    
    
}
