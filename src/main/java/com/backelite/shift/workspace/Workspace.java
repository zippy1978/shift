package com.backelite.shift.workspace;

/*
 * #%L
 * Workspace.java - shift - Gilles Grousset - 2013
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
import com.backelite.shift.workspace.artifact.Project;
import com.backelite.shift.state.PersistableState;
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
    
    public void closeProject(Project project) throws IOException;
    
    public boolean isProjectOpened(String path);
    
    public boolean isProjectOpened(Project project);
}
