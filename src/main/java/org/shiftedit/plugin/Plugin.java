package org.shiftedit.plugin;

import java.util.ArrayList;
import java.util.List;

/*
 * #%L
 * Plugin.java - shift - 2013
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
    private List<EditorFactory> editorFactories = new ArrayList<>();
    private List<PreviewFactory> previewFactories = new ArrayList<>();
    private List<ProjectWizardFactory> projectWizardFactories = new ArrayList<>();

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
