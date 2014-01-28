package com.backelite.shift.workspace;

/*
 * #%L
 * LocalWorkspaceImpl.java - shift - 2013
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
import com.backelite.shift.workspace.artifact.Artifact;
import com.backelite.shift.workspace.artifact.Document;
import com.backelite.shift.workspace.artifact.Folder;
import com.backelite.shift.workspace.artifact.FileSystemProject;
import com.backelite.shift.workspace.artifact.Project;
import com.backelite.shift.state.StateException;
import com.backelite.shift.util.FileUtils;
import com.backelite.shift.util.WeakObservable;
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
     * Create a new project. The new project is not automatically added to the workspace.
     * @param location Project location. At the moment only file system path is supported.
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
        for(File file : files) {
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
