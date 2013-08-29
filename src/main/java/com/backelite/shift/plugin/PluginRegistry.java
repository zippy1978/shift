/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.backelite.shift.plugin;

import com.backelite.shift.workspace.artifact.Document;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

/*
 * #%L
 * PluginRegistry.java - shift - 2013
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
 *
 * @author ggrousset
 */
public interface PluginRegistry {
    
    public void loadPlugins() throws PluginException;
    
    /**
     * Create a new editor for the given document.
     * @param document Document
     * @param loader FXML loader
     * @return Editor (as Node)
     */
    public Node newEditor(Document document, FXMLLoader loader) throws PluginException;
    
    /**
     * Create a new preview (using a provided factory).
     * @param factory PReview factory
     * @param loader FXML loader
     * @return Preview (as Node)
     */
    public Node newPreview(PreviewFactory factory, FXMLLoader loader) throws PluginException;
    
    /**
     * Create a new preview for a given document.
     * @param document Document
     * @param loader FXML loader
     * @return PReview (as Node) or null if no preview found
     * @throws PluginException 
     */
    public Node newPreview(Document document, FXMLLoader loader) throws PluginException;
    
    /**
     * Check if a given document can be previewed
     * @param document Document to test
     * @return true or false
     */
    public boolean canPreview(Document document);
    
    /**
     * Create a new project wizard.
     * @param factory
     * @param loader FXML loader
     * @return ProjectWizard (as Node)
     * @throws PluginException 
     */
    public Node newProjectWizard(ProjectWizardFactory factory, FXMLLoader loader) throws PluginException;
    /**
     * Return all available preview factories.
     * @return PreviewFactory list
     */
    public List<PreviewFactory> getPreviewFactories();
    
    /**
     * Return all available project wizard factories.
     * @return  ProjectWizardFactory list
     */
    public List<ProjectWizardFactory> getProjectWizardFactories();
}
