package com.backelite.shift.workspace.artifact;

import java.io.IOException;
import java.util.List;

/*
 * #%L
 * Folder.java - shift - Gilles Grousset - 2013
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
 * Folder.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public interface Folder extends Artifact {
    
    /**
     * Get the parent folder.
     * @return Parent folder (should always be set)
     */
    public Folder getParentFolder();
    
    /**
     * Get sub folders.
     * @return List of Folder objects.
     */
    public List<Folder> getSubFolders();
    
    /**
     * Get documents inside this folder.
     * @return List of Document objects.
     */
    public List<Document> getDocuments();
    
    /**
     * Create a new document into the folder.
     * @param name Name of the document.
     * @return Created document
     * @throws IOException 
     */
    public Document createDocument(String name) throws IOException;
    
    /**
     * Create a new subfolder folder into the folder.
     * @param name Name of the folder.
     * @return Created folder
     * @throws IOException 
     */
    public Folder createSubFolder(String name) throws IOException;
    
    /**
     * Get Project to which the folder belongs.
     * @return Project (should always be set)
     */
    public Project getProject();
    
}
