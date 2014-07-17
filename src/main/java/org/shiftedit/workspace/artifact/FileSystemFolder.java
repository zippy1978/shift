package org.shiftedit.workspace.artifact;

/*
 * #%L
 * FileSystemFolderImpl.java - shift - 2013
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

    private final List<Folder> subFolders = new ArrayList<>();
    private final List<Document> documents = new ArrayList<>();
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
        
        super.refresh();
        
        boolean changeDetected = false;
        
        // Test if file exists
        if (!file.exists()) {
            throw new IOException(String.format("Directory %s not found", file.getAbsolutePath()));
        }
        
        for (File child : file.listFiles()) {
            
            // Hidden files are skipped
            if (!child.isHidden()) {
            
                if (child.isDirectory()) {
                    // It's a folder
                    
                    Folder existingFolder = this.findChildFolder(child);
                    
                    // If not there : add it
                    if (existingFolder == null) {
                         changeDetected = true;
                         Folder folder = new FileSystemFolder(this, child);
                         folder.addObserver(this);
                         subFolders.add(folder);
                         folder.load();
                    // If already there : refresh it
                    } else {
                        existingFolder.refresh();
                    }
                    
                    // Look for children to delete (in folder, but not on file system anymore)
                    List<Folder> foldersToDelete = new ArrayList<>();
                    for(Folder folder : this.getSubFolders()) {
                        File folderFile = new File(folder.getPath());
                        if (!folderFile.exists()) {
                            changeDetected = true;
                            foldersToDelete.add(folder);
                        }
                    }
                    for (Folder folder : foldersToDelete) {
                        folder.delete();
                    }
                    

                } else if (child.isFile()) {
                    // It's a document
                    
                    Document existingDocument = this.findChildDocument(child);
                    
                    // If not there : add it
                    if (existingDocument == null) {
                        changeDetected = true;
                        Document document =  new FileSystemDocument(this, child);
                        document.addObserver(this);
                        documents.add(document);
                        document.load();
                    // If already there : refresh if, only if not opened
                    } else {
                        if (!existingDocument.isOpened()) {
                            existingDocument.refresh();
                        }
                    }
                    
                    // Look for children to delete (in folder, but not on file system anymore)
                    List<Document> documentsToDelete = new ArrayList<>();
                    for(Document document : this.getDocuments()) {
                        File documentFile = new File(document.getPath());
                        if (!documentFile.exists() && !document.isOpened()) {
                            changeDetected = true;
                            documentsToDelete.add(document);
                        }
                    }
                    for(Document document : documentsToDelete) {
                        document.delete();
                    }
                    
                }
            
            }
        }
        
        // Notify
        if (changeDetected) {
            this.setChanged();
            this.notifyObservers();
        }
    }
    
    /**
     * Returns a folder mathching a given file into current folder children.
     * @param file File to look for
     * @return Folder found or null
     */
    private Folder findChildFolder(File file) {
        
        for (Folder folder : this.getSubFolders()) {
            if (folder.getPath().equals(file.getAbsolutePath())) {
                return folder;
            }
        }
        
        return null;
    }
    
    /**
     * Returns a document matching a given file into the current folder children.
     * @param file File to look for
     * @return File found or null
     */
    private Document findChildDocument(File file) {
        
        for (Document document : this.getDocuments()) {
            if (document.getPath().equals(file.getAbsolutePath())) {
                return document;
            }
        }
        
        return null;
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
        
        // Watch
        if (this.parentFolder != null) {
            this.watcher = ((FileSystemProject)this.getProject()).getWatcher();
        }
        this.watcher.removeArtifact(this);
        this.watcher.addArtifact(this);
        
        loaded = true;
    }
    
    @Override
    public void delete() throws IOException {
        super.delete();
        
        // Delete
        if (file.exists() && !FileUtils.deleteDirectory(file)) {
            throw new IOException(String.format("Failed to delete %s ", file.getAbsolutePath()));
        }
        
        // Remove from parent
        if (parentFolder != null) {
            parentFolder.getSubFolders().remove(this);
        }
        
        // Remove watch
        this.watcher.removeArtifact(this);
        
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
