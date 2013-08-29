package com.backelite.shift.workspace.artifact;

/*
 * #%L
 * FileSystemDocumentImpl.java - shift - 2013
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
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class FileSystemDocument extends AbstractFileSystemArtifact implements Document {

    private Folder parentFolder;
    private Project project;
    private byte[] content;
    private boolean opened;
    private boolean modified;
    private boolean newDocument;

    public FileSystemDocument(Folder parentFolder, File file) {
        super(file);
        this.parentFolder = parentFolder;
        opened = false;
    }

    @Override
    public void load() throws IOException {

        super.load();

        // Test if file exists
        if (!file.exists()) {
            throw new IOException(String.format("File %s not found", file.getAbsolutePath()));
        }

        loaded = true;
    }

    @Override
    public void notifyObservers() {
        super.notifyObservers();

        // Notify change on parent (if any)
        FileSystemFolder folder = (FileSystemFolder) this.getParentFolder();
        if (folder != null) {
            folder.notifyObservers(this);
        }
    }

    public synchronized void save() throws IOException {

        if (content != null) {
            FileUtils.saveContentToFile(content, file);

            modified = false;

            // Notify document changed
            this.setChanged();
            this.notifyObservers();
        }

    }

    public void saveAll() throws IOException {
        this.save();
    }

    public void refresh() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Folder getParentFolder() {
        return parentFolder;
    }

    public synchronized Project getProject() {

        // Lazy project lookup
        if (project == null) {
            Folder folder = parentFolder;
            while (folder != null && !(folder instanceof Project)) {
                folder = folder.getParentFolder();
            }

            project = (Project) folder;
        }

        return project;
    }

    public String getContentAsString() {
        if (opened) {
            return new String(content);
        } else {
            return null;
        }
    }

    public byte[] getContent() {
        return this.content;
    }

    public synchronized void setContentAsString(String newContent) {

        byte[] newContentBytes = newContent.getBytes();

        // Track content modification : first with length, then with string comparaison
        if (content != null) {
            newDocument = false;
            modified = (content.length != newContentBytes.length);
            if (!modified) {
                if (!new String(content).equals(newContent)) {
                    modified = true;
                }
            }
        } else {
            modified = true;
            newDocument = true;
        }

        // Update content and notify document changed
        if (modified) {
            content = newContentBytes;
            this.setChanged();
            this.notifyObservers();
        }
    }

    /**
     * Load document.
     *
     * @throws IOException
     */
    public void open() throws IOException {

        if (!opened) {
            content = FileUtils.getFileContent(file);
            opened = true;
        }
    }

    public void close() {

        content = null;
        modified = false;
        opened = false;
    }

    public boolean isOpened() {
        return opened;
    }

    public boolean isModified() {
        return modified;
    }

    public boolean isNew() {
        return newDocument;
    }
    
    
}
