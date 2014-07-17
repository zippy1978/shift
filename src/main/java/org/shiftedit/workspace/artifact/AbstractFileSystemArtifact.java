package org.shiftedit.workspace.artifact;

/*
 * #%L
 * AbstractFileSystemArtifactImpl.java - shift - 2013
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
import org.shiftedit.util.FileUtils;
import org.shiftedit.util.WeakObservable;
import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Abstract file system artifact implementation.
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public abstract class AbstractFileSystemArtifact extends WeakObservable implements Artifact {

    protected File file;
    protected boolean loaded;
    protected boolean deleted;
    private Date lastModificationDate;
    private Date loadDate;
    
    protected FileSystemArtifactWatcher watcher;

    protected AbstractFileSystemArtifact(File file) {

        this.file = file;
        
    }

    @Override
    public void load() throws IOException {
        this.loadDate = new Date();
    }

    @Override
    public void refresh() throws IOException {
        this.loadDate = new Date();
    }
    
    

    @Override
    public String getName() {
        return file.getName();
    }
    
    public abstract Folder getParentFolder();

    @Override
    public String getPath() {
        return file.getAbsolutePath();
    }
    
    @Override
    public String getWorkspacePath() {
        
        String path = "/" + this.getName();
        Folder parent = this.getParentFolder();
        while (parent != null) {
            path = "/" + parent.getName() + path;
            parent = parent.getParentFolder();
        }
        
        return path;
    }

    @Override
    public void rename(String newName) throws IOException {

        File parent = file.getParentFile();
        File destFile = new File(parent, newName);

        FileUtils.moveFile(file, destFile);
        file = destFile;
        
        // Update watcher data
        this.getWatcher().removeArtifact(this);
        this.getWatcher().addArtifact(this);

        this.setChanged();
        this.notifyObservers();

    }

    @Override
    public void delete() throws IOException {

        // Mark as deleted
        deleted = true;
        
    }

    @Override
    public void moveTo(Artifact newParent) throws IOException {

        File destFile = new File(newParent.getPath(), file.getName());

        FileUtils.moveFile(file, destFile);
        file = destFile;
        
        // Update watcher data
        this.getWatcher().removeArtifact(this);
        this.getWatcher().addArtifact(this);

        this.setChanged();
        this.notifyObservers();
    }

    /**
     * @return the deleted
     */
    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public synchronized boolean isOutOfSync() {
        
        this.setLastModificationDate(new Date(new File(file.getAbsolutePath()).lastModified()));
        return this.loaded && this.lastModificationDate.after(this.loadDate);
    }
    
    /**
     * Notify if document is out of sync.
     */
    protected void notifyOutOfSync() {
        
        if (this.isOutOfSync()) {
            this.setChanged();
            this.notifyObservers();
        }
    }
    
    /**
     * @return the watcher
     */
    public FileSystemArtifactWatcher getWatcher() {
        return watcher;
    }

    /**
     * @return the lastModificationDate
     */
    public Date getLastModificationDate() {
        return lastModificationDate;
    }

    /**
     * @param lastModificationDate the lastModificationDate to set
     */
    public void setLastModificationDate(Date lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    /**
     * @return the loadDate
     */
    public Date getLoadDate() {
        return loadDate;
    }

    /**
     * @param loadDate the loadDate to set
     */
    public void setLoadDate(Date loadDate) {
        this.loadDate = loadDate;
    }
    
}
