package com.backelite.shift.plugin;

import java.util.ArrayList;
import java.util.List;

/*
 * #%L
 * Plugin.java - shift - 2013
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
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class Plugin {

    private String uid;
    private String name;
    private String description;
    private String author;
    private int versionCode;
    private String versionName;
    private PluginLifecycle lifecycle;
    private List<EditorFactory> editorFactories = new ArrayList<EditorFactory>();
    private List<PreviewFactory> previewFactories = new ArrayList<PreviewFactory>();
    private List<ProjectWizardFactory> projectWizardFactories = new ArrayList<ProjectWizardFactory>();

     /**
     * @return the uid
     */
    public String getUid() {
        return uid;
    }

    /**
     * @param uid the uid to set
     */
    public void setUid(String uid) {
        this.uid = uid;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the versionCode
     */
    public int getVersionCode() {
        return versionCode;
    }

    /**
     * @param versionCode the versionCode to set
     */
    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    /**
     * @return the versionName
     */
    public String getVersionName() {
        return versionName;
    }

    /**
     * @param versionName the versionName to set
     */
    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    /**
     * @return the editorFactories
     */
    public List<EditorFactory> getEditorFactories() {
        return editorFactories;
    }

    /**
     * @param editorFactories the editorFactories to set
     */
    public void setEditorFactories(List<EditorFactory> editorFactories) {
        this.editorFactories = editorFactories;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the previewFactories
     */
    public List<PreviewFactory> getPreviewFactories() {
        return previewFactories;
    }

    /**
     * @param previewFactories the previewFactories to set
     */
    public void setPreviewFactories(List<PreviewFactory> previewFactories) {
        this.previewFactories = previewFactories;
    }

    /**
     * @return the projectWizardFactories
     */
    public List<ProjectWizardFactory> getProjectWizardFactories() {
        return projectWizardFactories;
    }

    /**
     * @param projectWizardFactories the projectWizardFactories to set
     */
    public void setProjectWizardFactories(List<ProjectWizardFactory> projectWizardFactories) {
        this.projectWizardFactories = projectWizardFactories;
    }

    /**
     * @return the lifecycle
     */
    public PluginLifecycle getLifecycle() {
        return lifecycle;
    }

    /**
     * @param lifecycle the lifecycle to set
     */
    public void setLifecycle(PluginLifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

}
