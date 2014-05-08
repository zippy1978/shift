/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiftedit.plugin;

import org.shiftedit.workspace.artifact.Document;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

/*
 * #%L
 * PluginRegistry.java - shift - 2013
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
 *
 * @author ggrousset
 */
public interface PluginRegistry {
    
    public void loadPlugins() throws PluginException;
    
    public void unloadPlugins() throws PluginException;
    
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
     * Return a list of available preview factories for a given document.
     * @param document Document
     * @return List of PreviewFactory objects
     */
    public List<PreviewFactory> getAvailablePreviewFactories(Document document);
    
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
