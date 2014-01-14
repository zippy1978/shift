package com.backelite.shift.workspace.artifact;

/*
 * #%L
 * Artifact.java - shift - Gilles Grousset - 2013
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

import java.io.IOException;
import java.util.Observer;
import javafx.scene.image.Image;

/**
 * Abstract representation of an item of the workspace (file, folder, project ...)
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public interface Artifact {
    
    public String getName();
    public String getPath();
    public String getWorkspacePath();
    
    public void rename(String newName) throws IOException;
    public void delete() throws IOException;
    
    public boolean isDeleted();
    public boolean isModified();
    
    /**
     * Load artifact and all its children.
     * @throws IOException 
     */
    public void load() throws IOException;
    
    /**
     * Save the artifact.
     * @throws IOException 
     */
    public void save() throws IOException;
    
    /**
     * Save the artifact and all its children.
     * @throws IOException 
     */
    public void saveAll() throws IOException;
    public void refresh() throws IOException;
    public void moveTo(Artifact newParent) throws IOException;
    
    public void addObserver(Observer o);
    public void deleteObserver(Observer o);
    public void deleteObservers();
    
}
