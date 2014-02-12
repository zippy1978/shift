package com.backelite.shift.workspace;

/*
 * #%L
 * LocalWorkspaceImpl.java - shift - 2013
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
import com.backelite.shift.state.StateException;
import com.backelite.shift.util.FileUtils;
import com.backelite.shift.util.WeakObservable;
import com.backelite.shift.workspace.artifact.Artifact;
import com.backelite.shift.workspace.artifact.Document;
import com.backelite.shift.workspace.artifact.FileSystemProject;
import com.backelite.shift.workspace.artifact.Folder;
import com.backelite.shift.workspace.artifact.Project;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class LocalWorkspace extends WeakObservable implements Workspace {

    private List<Project> projects = new ArrayList<>();

    @Override
    public List<Project> getProjects() {
        return projects;
    }

    @Override
    public Artifact findArtifactByWorkspacePath(String workspacePath) {

        for (Project project : projects) {
            Artifact artifact = this.findArtifactByWorkspacePathFromArtifact(project, workspacePath);
            if (artifact != null) {
                return artifact;
            }
        }

        // Nothing found ...
        return null;
    }

    /**
     * Recursive Workspace path search.
     *
     * @param artifact Root artifact
     * @param workspacePath Workspace pathx
     * @return
     */
    private Artifact findArtifactByWorkspacePathFromArtifact(Artifact artifact, String workspacePath) {

        Artifact result = null;

        if (artifact.getWorkspacePath().equals(workspacePath)) {
            return artifact;
        } else {
            if (artifact instanceof Folder) {
                for (Document document : ((Folder) artifact).getDocuments()) {
                    result = findArtifactByWorkspacePathFromArtifact(document, workspacePath);
                    if (result != null) {
                        return result;
                    }
                }
                for (Folder subFolder : ((Folder) artifact).getSubFolders()) {
                    result = findArtifactByWorkspacePathFromArtifact(subFolder, workspacePath);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }

        // Nothing found ...
        return null;
    }

    @Override
    public void synchronize() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Create a new project. The new project is not automatically added to the
     * workspace.
     *
     * @param location Project location. At the moment only file system path is
     * supported.
     * @param name Name of the project
     * @return The new project.
     * @throws IOException
     */
    @Override
    public Project createProject(String location, String name) throws IOException {

        Project newProject = new FileSystemProject(new File(location, name));
        newProject.save();

        return newProject;
    }

    @Override
    public Project importProjectFromDirectory(File sourceDirectory, String location, String name) throws IOException {

        Project project = this.createProject(location, name);

        this.importFolderFromDirectory(sourceDirectory, project);

        return project;

    }

    private void importFolderFromDirectory(File sourceDirectory, Folder folder) throws IOException {

        // Browse source dir
        File[] files = sourceDirectory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                Folder newFolder = folder.createSubFolder(file.getName());
                newFolder.save();
                this.importFolderFromDirectory(file, newFolder);
            } else {
                Document newDocument = folder.createDocument(file.getName());
                newDocument.setContent(FileUtils.getFileContent(file));
                newDocument.save();

            }
        }
    }

    @Override
    public void openProject(Project project) throws IOException {

        if (!this.isProjectOpened(project.getPath())) {

            // Load project
            project.load();

            // Add it to the workspace
            projects.add(project);

            // Notify
            this.setChanged();
            this.notifyObservers(project);

        }
    }

    @Override
    public void closeProject(Project project) throws IOException {

        // Remove from workspace
        projects.remove(project);

        // Notify
        this.setChanged();
        this.notifyObservers(project);
    }

    @Override
    public boolean isProjectOpened(String path) {

        for (Project project : projects) {
            if (project.getPath().equals(path)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isProjectOpened(Project project) {
        return this.isProjectOpened(project.getPath());
    }

    @Override
    public boolean isModified() {

        for (Project project : projects) {
            if (project.isModified()) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public void saveState(Map<String, Object> state) throws StateException {

        // Projects
        List<Map<String, Object>> projectsState = new ArrayList<>();
        for (Project project : projects) {
            Map<String, Object> projectState = new HashMap<>();
            projectState.put("path", project.getPath());
            projectState.put("class", project.getClass().getSimpleName());

            projectsState.add(projectState);
        }

        state.put("projects", projectsState);

    }

    @Override
    public void restoreState(Map<String, Object> state) throws StateException {

        // Projects
        List<Map<String, Object>> projectsState = (List<Map<String, Object>>) state.get("projects");
        for (Map<String, Object> projectState : projectsState) {
            String path = (String) projectState.get("path");
            String clazz = (String) projectState.get("class");

            if (clazz.equals(FileSystemProject.class.getSimpleName())) {
                File file = new File(path);
                Project project = new FileSystemProject(file);
                if (file.exists()) {
                    try {
                        project.load();
                        projects.add(project);
                    } catch (IOException ex) {
                        throw new StateException(ex);
                    }
                }
            }
        }
    }

    @Override
    public String getInstanceIdentifier() {
        return null;
    }
}
