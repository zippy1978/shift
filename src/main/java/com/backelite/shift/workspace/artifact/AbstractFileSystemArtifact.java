package com.backelite.shift.workspace.artifact;

/*
 * #%L
 * AbstractFileSystemArtifactImpl.java - shift - 2013
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
import com.backelite.shift.util.FileUtils;
import com.backelite.shift.util.WeakObservable;
import java.io.File;
import java.io.IOException;

/**
 * Abstract file system artifact implementation.
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public abstract class AbstractFileSystemArtifact extends WeakObservable implements Artifact {

    protected File file;
    protected boolean loaded;
    protected boolean deleted;

    protected AbstractFileSystemArtifact(File file) {

        this.file = file;
        
    }

    public void load() throws IOException {
        if (loaded) {
            return;
        }
    }

    public String getName() {
        return file.getName();
    }
    
    public abstract Folder getParentFolder();

    public String getPath() {
        return file.getAbsolutePath();
    }
    
    public String getWorkspacePath() {
        
        String path = "/" + this.getName();
        Folder parent = this.getParentFolder();
        while (parent != null) {
            path = "/" + parent.getName() + path;
            parent = parent.getParentFolder();
        }
        
        return path;
    }

    public void rename(String newName) throws IOException {

        File parent = file.getParentFile();
        File destFile = new File(parent, newName);

        FileUtils.moveFile(file, destFile);
        file = destFile;

        this.setChanged();
        this.notifyObservers();

    }

    public void delete() throws IOException {

        // Delete
        if (!file.delete()) {
            throw new IOException(String.format("Failed to delete %s ", file.getAbsolutePath()));
        }

        // Mark as deleted
        deleted = true;
        
        // Notify
        this.setChanged();
        this.notifyObservers();
    }

    public void moveTo(Artifact newParent) throws IOException {

        File destFile = new File(newParent.getPath(), file.getName());

        FileUtils.moveFile(file, destFile);
        file = destFile;

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
    
}
