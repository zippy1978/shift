package org.shiftedit.plugin;

/*
 * #%L
 * AbstractPluginRegistryImpl.java - shift - 2013
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
import org.shiftedit.gui.editor.EditorController;
import org.shiftedit.gui.preview.PreviewController;
import org.shiftedit.gui.projectwizard.ProjectWizardController;
import org.shiftedit.util.FileUtils;
import org.shiftedit.workspace.artifact.Document;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public abstract class AbstractPluginRegistry implements PluginRegistry {

    protected List<Plugin> plugins = new ArrayList<>();
    protected Map<String, Set<EditorFactory>> editorExtensions = new HashMap<>();
    protected Map<String, Set<PreviewFactory>> previewExtensions = new HashMap<>();

    @Override
    public Node newEditor(Document document, FXMLLoader loader) throws PluginException {

        try {

            // Look for matching editor factory
            Set<EditorFactory> matchingFactories = editorExtensions.get(FileUtils.getFileExtension(document.getName()));

            if (matchingFactories == null || matchingFactories.isEmpty()) {
                // No matching factory found : take the default editor
                matchingFactories = editorExtensions.get("*");
            }
            // Take the first factory
            // TODO : get the one set in preferences instead or present the picker first
            EditorFactory factory = matchingFactories.iterator().next();


            Node node = factory.newEditor(document, loader);

            // Check if the controller implements EditorController interface
            if (!(loader.getController() instanceof EditorController)) {
                throw new ClassCastException(String.format("%s s not an instance of EditorController", loader.getController().getClass().getName()));
            }

            return node;

        } catch (ClassCastException e) {
            throw new PluginException(e);
        }
    }

    @Override
    public boolean canPreview(Document document) {
        
        // Look for matching preview factory
        Set<PreviewFactory> matchingFactories = previewExtensions.get(FileUtils.getFileExtension(document.getName()));

        if (matchingFactories != null && !matchingFactories.isEmpty()) {
            return true;
        } else {
            return false;
        }
        
    }

    @Override
    public List<PreviewFactory> getAvailablePreviewFactories(Document document) {
        
        List<PreviewFactory> result = new ArrayList<>();
        
        // Look for matching preview factory
        Set<PreviewFactory> matchingFactories = previewExtensions.get(FileUtils.getFileExtension(document.getName()));

        if (matchingFactories != null) {
            result.addAll(matchingFactories);
        }
        
        return result;
        
    }
    
    @Override
    public Node newPreview(Document document, FXMLLoader loader) throws PluginException {
        
        // Look for matching preview factory
        Set<PreviewFactory> matchingFactories = previewExtensions.get(FileUtils.getFileExtension(document.getName()));

        if (matchingFactories != null && !matchingFactories.isEmpty()) {
            return this.newPreview(matchingFactories.iterator().next(), loader);
        } else {
            // Not found
            return null;
        }
    }
    
    @Override
    public Node newPreview(PreviewFactory factory, FXMLLoader loader) throws PluginException {

        Node node = factory.newPreview(loader);

        // Check if the controller implements PreviewController interface
        if (!(loader.getController() instanceof PreviewController)) {
            throw new ClassCastException(String.format("%s s not an instance of PreviewController", loader.getController().getClass().getName()));
        } else {
            // Set factory on preview controller
            PreviewController previewController = (PreviewController)loader.getController();
            previewController.setFactory(factory);
        }

        return node;
    }
    
    @Override
    public Node newProjectWizard(ProjectWizardFactory factory, FXMLLoader loader) throws PluginException {
        
        Node node = factory.newProjectWizard(loader);
        
        // Check if the controller implements ProjectWizardController interface
        if (!(loader.getController() instanceof ProjectWizardController)) {
            throw new ClassCastException(String.format("%s s not an instance of ProjectWizardController", loader.getController().getClass().getName()));
        }

        return node;
    }

    @Override
    public List<PreviewFactory> getPreviewFactories() {
        
        List<PreviewFactory> factories = new ArrayList<>();
        
        for(Plugin plugin : plugins) {
            factories.addAll(plugin.getPreviewFactories());
        }
        
        return factories;
    }

    @Override
    public List<PreferencesPanelFactory> getPreferencesPanelFactories() {
        
        List<PreferencesPanelFactory> factories = new ArrayList<>();
        
        for(Plugin plugin : plugins) {
            factories.addAll(plugin.getPreferencesPanelFactories());
        }
        
        return factories;
    }
    
    
    
    @Override
    public List<ProjectWizardFactory> getProjectWizardFactories() {
        
        List<ProjectWizardFactory> factories = new ArrayList<>();
        
        for(Plugin plugin : plugins) {
            factories.addAll(plugin.getProjectWizardFactories());
        }
        
        return factories;
    }

    @Override
    public List<Plugin> getPlugins() {
        return this.plugins;
    }
    
    

    protected void addEditorFactoryToExtension(String extension, EditorFactory editorFactory) {

        Set<EditorFactory> factories = editorExtensions.get(extension.toLowerCase());
        if (factories == null) {
            factories = new HashSet<>();
            editorExtensions.put(extension.toLowerCase(), factories);
        }
        factories.add(editorFactory);
    }
    
    protected void addPreviewFactoryToExtension(String extension, PreviewFactory previewFactory) {

        Set<PreviewFactory> factories = previewExtensions.get(extension.toLowerCase());
        if (factories == null) {
            factories = new HashSet<>();
            previewExtensions.put(extension.toLowerCase(), factories);
        }
        factories.add(previewFactory);
    }
}
