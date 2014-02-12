package com.backelite.shift.workspace;

/*
 * #%L
 * Workspace.java - shift - Gilles Grousset - 2013
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

import com.backelite.shift.state.PersistableState;
import com.backelite.shift.workspace.artifact.Artifact;
import com.backelite.shift.workspace.artifact.Project;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Observer;

/**
 * Workspace representation. A workspace contains multiple projects.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public interface Workspace extends PersistableState {
    
    public List<Project> getProjects();
    
    public Artifact findArtifactByWorkspacePath(String workspacePath);
    
    public void addObserver(Observer o);
    
    public void synchronize() throws IOException;
    
    public void openProject(Project project) throws IOException;
    
    public Project createProject(String location, String name) throws IOException;
    
    public Project importProjectFromDirectory(File sourceDirectory, String location, String name) throws IOException;
    
    public void closeProject(Project project) throws IOException;
    
    public boolean isProjectOpened(String path);
    
    public boolean isProjectOpened(Project project);
    
    /**
     * Check if at least one artifact was modified in the workspace.
     * @return 
     */
    public boolean isModified();
}
