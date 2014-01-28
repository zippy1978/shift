package com.backelite.shift.workspace.artifact;

/*
 * #%L
 * FileSystemFolderImpl.java - shift - 2013
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
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class FileSystemFolder extends AbstractFileSystemArtifact implements Folder, Observer {

    private List<Folder> subFolders = new ArrayList<>();
    private List<Document> documents = new ArrayList<>();
    private Folder parentFolder;
    private Project project;
    
    public FileSystemFolder(Folder parentFolder, File file) {
        super(file);
        this.parentFolder = parentFolder;
    }

    @Override
    public boolean isModified() {
        for (Document document : documents) {
            if (document.isModified()) {
                return true;
            }
        }
        
        return false;
    }
    
    

    @Override
    public void save() throws IOException {
        
        // Create folder if it does not exist yet
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new IOException(String.format("Failed to write directory %s", file.getAbsolutePath()));
            }
        }
        
        this.setChanged();
        this.notifyObservers();
    }

    @Override
    public void saveAll() throws IOException {
        
        // Save folder
        this.save();
        
        // Save all documents
        for (Document document : this.getDocuments()) {
            document.saveAll();
        }
                
        // Save all subfolders
        for (Folder folder : this.getSubFolders()) {
            folder.saveAll();
        }
        
        this.setChanged();
        this.notifyObservers();
    }

    @Override
    public void refresh() throws IOException {
        
        this.load();
    }

    @Override
    public void rename(String newName) throws IOException {
        super.rename(newName);
        
        // Reload children
        subFolders.clear();
        documents.clear();
        this.load();
    }

    @Override
    public void moveTo(Artifact newParent) throws IOException {
        super.moveTo(newParent);
        
        // Reload children
        subFolders.clear();
        documents.clear();
        this.load();
    }
    
    

    @Override
    public void load() throws IOException {
        
        super.load();
        
        subFolders.clear();
        documents.clear();
        
        // Test if file exists
        if (!file.exists()) {
            throw new IOException(String.format("Directory %s not found", file.getAbsolutePath()));
        }
        
        for (File child : file.listFiles()) {
            
            // Hidden files are skipped
            if (!child.isHidden()) {
            
                if (child.isDirectory()) {
                    // It's a folder
                    Folder folder = new FileSystemFolder(this, child);
                    folder.addObserver(this);
                    subFolders.add(folder);
                    folder.load();

                } else if (child.isFile()) {
                    // It's a document
                    Document document =  new FileSystemDocument(this, child);
                    document.addObserver(this);
                    documents.add(document);
                    document.load();
                }
            
            }
        }
        
        loaded = true;
    }
    
    @Override
    public void delete() throws IOException {
        super.delete();
        
        // Delete
        if (!FileUtils.deleteDirectory(file)) {
            throw new IOException(String.format("Failed to delete %s ", file.getAbsolutePath()));
        }
        
        // Remove from parent
        if (parentFolder != null) {
            parentFolder.getSubFolders().remove(this);
        }
        
        // Notify
        this.setChanged();
        this.notifyObservers();
    }

    @Override
    public Folder getParentFolder() {
        return parentFolder;
    }

    @Override
    public List<Folder> getSubFolders() {
        return subFolders;
    }

    @Override
    public List<Document> getDocuments() {
        return documents;
    }

    @Override
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
    
    @Override
    public Document createDocument(String name) throws IOException {
        Document newDocument = new FileSystemDocument(this, new File(file, name));
        this.getDocuments().add(newDocument);
        newDocument.addObserver(this);
        // Set content to other than null : otherwise file is not written
        newDocument.setContentAsString("");
        newDocument.save();
        newDocument.load();
        
        return newDocument;
 
    }

    @Override
    public Folder createSubFolder(String name) throws IOException {
        Folder newFolder = new FileSystemFolder(this, new File(file, name));
        this.getSubFolders().add(newFolder);
        newFolder.addObserver(this);
        newFolder.save();
        newFolder.load();
        
        return newFolder;
    }
    
    

    @Override
    public void update(Observable o, Object arg) {
        
        // On update : forward notification
        this.setChanged();
        if (arg != null) {
            this.notifyObservers(arg);
        } else {
            this.notifyObservers(o);
        }
    }

    
}
