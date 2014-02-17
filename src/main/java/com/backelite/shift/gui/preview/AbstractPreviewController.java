package com.backelite.shift.gui.preview;

/*
 * #%L
 * AbstractPreviewController.java - shift - 2013
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
                if (activeDocumentTrackingEnabled && t1 != null && t1.getDocument() != null && isDocumentSupported(t1.getDocument())) {
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
