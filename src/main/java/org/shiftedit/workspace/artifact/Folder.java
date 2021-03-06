package org.shiftedit.workspace.artifact;

import java.io.IOException;
import java.util.List;

/*
 * #%L
 * Folder.java - shift - Gilles Grousset - 2013
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
