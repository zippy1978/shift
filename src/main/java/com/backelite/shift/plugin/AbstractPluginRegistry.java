package com.backelite.shift.plugin;

/*
 * #%L
 * AbstractPluginRegistryImpl.java - shift - 2013
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
import com.backelite.shift.gui.preview.PreviewController;
import com.backelite.shift.gui.projectwizard.ProjectWizardController;
import com.backelite.shift.workspace.artifact.Document;
import com.backelite.shift.util.FileUtils;
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

    protected List<Plugin> plugins = new ArrayList<Plugin>();
    protected Map<String, Set<EditorFactory>> editorExtensions = new HashMap<String, Set<EditorFactory>>();
    protected Map<String, Set<PreviewFactory>> previewExtensions = new HashMap<String, Set<PreviewFactory>>();

    public Node newEditor(Document document, FXMLLoader loader) throws PluginException {

        try {
            // Open document
            document.open();

            // Look for matching editor factory
            Set<EditorFactory> matchingFactories = editorExtensions.get(FileUtils.getFileExtension(document.getName()));

            if (matchingFactories == null || matchingFactories.isEmpty()) {
                // No matching factory found : take the default editor
                matchingFactories = editorExtensions.get("*");
            }
            // Take the first factory
            // TODO : get the one set in preferences instead
            EditorFactory factory = matchingFactories.iterator().next();


            Node node = factory.newEditor(document, loader);

            // Check if the controller implements EditorController interface
            if (!(loader.getController() instanceof EditorController)) {
                throw new ClassCastException(String.format("%s s not an instance of EditorController", loader.getController().getClass().getName()));
            }

            return node;

        } catch (Exception e) {
            throw new PluginException(e);
        }
    }

    public boolean canPreview(Document document) {
        
        // Look for matching preview factory
        Set<PreviewFactory> matchingFactories = previewExtensions.get(FileUtils.getFileExtension(document.getName()));

        if (matchingFactories != null && !matchingFactories.isEmpty()) {
            return true;
        } else {
            return false;
        }
        
    }

    public List<PreviewFactory> getAvailablePreviewFactories(Document document) {
        
        List<PreviewFactory> result = new ArrayList<PreviewFactory>();
        
        // Look for matching preview factory
        Set<PreviewFactory> matchingFactories = previewExtensions.get(FileUtils.getFileExtension(document.getName()));

        if (matchingFactories != null) {
            result.addAll(matchingFactories);
        }
        
        return result;
        
    }
    
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
    
    public Node newProjectWizard(ProjectWizardFactory factory, FXMLLoader loader) throws PluginException {
        
        Node node = factory.newProjectWizard(loader);
        
        // Check if the controller implements ProjectWizardController interface
        if (!(loader.getController() instanceof ProjectWizardController)) {
            throw new ClassCastException(String.format("%s s not an instance of ProjectWizardController", loader.getController().getClass().getName()));
        }

        return node;
    }

    public List<PreviewFactory> getPreviewFactories() {
        
        List<PreviewFactory> factories = new ArrayList<PreviewFactory>();
        
        for(Plugin plugin : plugins) {
            factories.addAll(plugin.getPreviewFactories());
        }
        
        return factories;
    }
    
    public List<ProjectWizardFactory> getProjectWizardFactories() {
        
        List<ProjectWizardFactory> factories = new ArrayList<ProjectWizardFactory>();
        
        for(Plugin plugin : plugins) {
            factories.addAll(plugin.getProjectWizardFactories());
        }
        
        return factories;
    }

    protected void addEditorFactoryToExtension(String extension, EditorFactory editorFactory) {

        Set<EditorFactory> factories = editorExtensions.get(extension.toLowerCase());
        if (factories == null) {
            factories = new HashSet<EditorFactory>();
            editorExtensions.put(extension.toLowerCase(), factories);
        }
        factories.add(editorFactory);
    }
    
    protected void addPreviewFactoryToExtension(String extension, PreviewFactory previewFactory) {

        Set<PreviewFactory> factories = previewExtensions.get(extension.toLowerCase());
        if (factories == null) {
            factories = new HashSet<PreviewFactory>();
            previewExtensions.put(extension.toLowerCase(), factories);
        }
        factories.add(previewFactory);
    }
}
