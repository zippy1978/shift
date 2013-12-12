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
import com.backelite.shift.workspace.artifact.Document;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javafx.stage.Stage;

/**
 * Abstract base implementaiton of PreviewController.
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public abstract class AbstractPreviewController extends AbstractController implements PreviewController, Observer {

    protected Document document;
    protected Stage parentStage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb); //To change body of generated methods, choose Tools | Templates.
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
    public void update(Observable o, Object arg) {

        // Something in the project was updated
        // Refresh view
        this.refresh();

    }

    /**
     * @return the document
     */
    public Document getDocument() {
        return document;
    }

    /**
     * @param document the document to set
     */
    public void setDocument(Document document) {
        this.document = document;

        if (this.document != null) {
            this.document.getProject().addObserver(this);
            this.refresh();
        }
    }

    /**
     * @return the parentStage
     */
    public Stage getParentStage() {
        return parentStage;
    }

    /**
     * @param parentStage the parentStage to set
     */
    public void setParentStage(Stage parentStage) {
        this.parentStage = parentStage;
    }
    
    @Override
    public void close() {
        if (getParentStage() != null) {
            getParentStage().close();
        }
    }
}
