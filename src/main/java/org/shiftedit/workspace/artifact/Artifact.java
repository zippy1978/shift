package org.shiftedit.workspace.artifact;

/*
 * #%L
 * Artifact.java - shift - Gilles Grousset - 2013
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

import java.io.IOException;
import java.util.Observer;

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
    public boolean isOutOfSync();
    
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
