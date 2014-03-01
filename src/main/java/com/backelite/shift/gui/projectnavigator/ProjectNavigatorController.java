package com.backelite.shift.gui.projectnavigator;

/*
 * #%L
 * ProjectNavigatorController.java - shift - 2013
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
import com.backelite.shift.ApplicationContext;
import com.backelite.shift.gui.AbstractController;
import com.backelite.shift.state.StateException;
import com.backelite.shift.workspace.artifact.Artifact;
import com.backelite.shift.workspace.artifact.Document;
import com.backelite.shift.workspace.artifact.Folder;
import com.backelite.shift.workspace.artifact.PlaceHolderArtifact;
import com.backelite.shift.workspace.artifact.Project;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class ProjectNavigatorController extends AbstractController implements Observer, EventHandler<MouseEvent> {

    @FXML
    private TreeView<Artifact> treeView;
    
    @FXML
    private Label welcomeLabel;
    
    private TreeItem<Artifact> treeItemRoot;
    private EventHandler<OpenFileEvent> onOpenFile;
    private EventHandler<ArtifactSelectedEvent> onArtifactSelected;
    private EventHandler<ProjectClosedEvent> onProjectClosed;
    private EventHandler<NewFileEvent> onNewFile;
    private EventHandler<NewFolderEvent> onNewFolder;
    private EventHandler<ImportArtifactsEvent> onImportArtifacts;
    private EventHandler<DeleteArtifactEvent> onDeleteArtifact;
    private EventHandler<RenameArtifactEvent> onRenameArtifact;
    /**
     * Maintains expanded state of each artifact of the treee
     */
    private Map<String, Boolean> expandedStates = new HashMap<>();
    private Artifact lastSelectedArtifact;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        
        // Build root
        treeItemRoot = new TreeItem<>(new PlaceHolderArtifact(this.getResourceBundle().getString("project_navigator.root.title")));
        treeView.setRoot(treeItemRoot);
        treeView.setShowRoot(false);
        treeItemRoot.setExpanded(true);
        treeView.setCellFactory((TreeView<Artifact> treeView) -> {
            TreeCell<Artifact> cell = new ArtifactTreeCell();
            cell.setOnMouseClicked(ProjectNavigatorController.this);
            cell.setUserData(ProjectNavigatorController.this);
            
            return cell;
        });

        // Position
        AnchorPane.setTopAnchor(treeView, 0.0);

        // Listen to selection change
        treeView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<Artifact>> ov, TreeItem<Artifact> t, TreeItem<Artifact> t1) -> {
            if (t1 != null) {
                Artifact artifact = (Artifact) t1.getValue();
                lastSelectedArtifact = artifact;
                if (getOnArtifactSelected() != null) {
                    getOnArtifactSelected().handle(new ArtifactSelectedEvent(EventType.ROOT, artifact));
                }
            } else {
                lastSelectedArtifact = null;
            }
        });
        
        // If workspace if empty : display welcome label
        this.refreshWelcomeMessage();

        // Observe workspace
        ApplicationContext.getWorkspace().addObserver(this);
    }
    
    private void refreshWelcomeMessage() {
        // If workspace if empty : display welcome label
        welcomeLabel.setVisible(ApplicationContext.getWorkspace().getProjects().isEmpty());
    }

    /**
     * Close project.
     *
     * @param project Project to close.
     */
    protected void closeProject(final Project project) {
        if (onProjectClosed != null) {
            onProjectClosed.handle(new ProjectClosedEvent(EventType.ROOT, project));
        }
    }

    public void newFile(Folder parentFolder) {
        if (onNewFile != null) {
            onNewFile.handle(new NewFileEvent(EventType.ROOT, parentFolder));
        }
    }
    
    public void newFolder(Folder parentFolder) {
        if (getOnNewFolder() != null) {
            getOnNewFolder().handle(new NewFolderEvent(EventType.ROOT, parentFolder));
        }
    }
    
    public void importArtifacts(Folder parentFolder) {
        if (getOnImportArtifacts() != null) {
            getOnImportArtifacts().handle(new ImportArtifactsEvent(EventType.ROOT, parentFolder));
        }
    }
    
    public void deleteArtifact(Artifact artifact) {
        if (getOnDeleteArtifact() != null) {
            onDeleteArtifact.handle(new DeleteArtifactEvent(EventType.ROOT, artifact));
        }
    }
    
    public void renameArtifact(Artifact artifact) {
        if (getOnRenameArtifact()!= null) {
            onRenameArtifact.handle(new RenameArtifactEvent(EventType.ROOT, artifact));
        }
    }

    public Artifact getSelectedArtifact() {
        TreeItem<Artifact> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            return selectedItem.getValue();
        } else {
            return null;
        }
    }

    /**
     * Synchronize view with the workspace model.
     */
    public void refresh(boolean saveExpandedStateBefore) {

        if (saveExpandedStateBefore) {
            // Save expanded states
            this.expandedStates.clear();
            this.saveExpandedStates(treeItemRoot);
        }

        // Clear root
        treeItemRoot.getChildren().clear();

        // Add each project again
        List<Project> projects = ApplicationContext.getWorkspace().getProjects();
        for (Project project : projects) {
            this.addProjectNode(project);
        }

        // Restore saved selection (if any)
        if (lastSelectedArtifact != null) {
            TreeItem<Artifact> selectedItem = this.searchTreeItem(treeItemRoot, lastSelectedArtifact);
            if (selectedItem != null) {
                treeView.getSelectionModel().select(selectedItem);
                Boolean expanded = expandedStates.get(selectedItem.getValue().getWorkspacePath());
                if (expanded != null)
                selectedItem.setExpanded(expanded);
            }
        }
    }

    private void addProjectNode(Project project) {

        TreeItem<Artifact> projectNode = new TreeItem<>(project);
        treeItemRoot.getChildren().add(projectNode);

        // Restore expanded state
        Boolean expanded = expandedStates.get(project.getWorkspacePath());
        if (expanded != null) {
            projectNode.setExpanded(expanded);
        }

        // Folders
        for (Folder folder : project.getSubFolders()) {
            this.addFolderNode(projectNode, folder);
        }

        // Documents
        for (Document document : project.getDocuments()) {
            this.addDocumentNode(projectNode, document);
        }

        // Observe
        project.addObserver(this);
    }

    private void addFolderNode(TreeItem<Artifact> parentNode, Folder folder) {

        TreeItem<Artifact> folderNode = new TreeItem<>(folder);
        parentNode.getChildren().add(folderNode);

        // Restore expanded state
        Boolean expanded = expandedStates.get(folder.getWorkspacePath());
        if (expanded != null) {
            folderNode.setExpanded(expanded);
        }

        // Subfolders
        for (Folder subFolder : folder.getSubFolders()) {
            this.addFolderNode(folderNode, subFolder);
        }

        // Documents
        for (Document document : folder.getDocuments()) {
            this.addDocumentNode(folderNode, document);
        }

    }

    private void addDocumentNode(TreeItem<Artifact> parentNode, Document document) {

        TreeItem<Artifact> documentNode = new TreeItem<>(document);
        parentNode.getChildren().add(documentNode);

    }

    @Override
    public void update(Observable o, Object arg) {

        boolean canRefresh = true;
        boolean canSelect = true;
        
        // If workspace if empty : display welcome label
        this.refreshWelcomeMessage();

        // Special case for document : no need to refresh if document is marked as modified
        Document document = null;
        if (o instanceof Document) {
            document = (Document) o;

        }
        if (arg != null && arg instanceof Document) {

            document = (Document) arg;
        }
        if (document != null) {
            
            if (document.isModified() && !document.isDeleted()) {
                canRefresh = false;
            }
            if (!document.isNew()) {
                canSelect = false;
            }
        }
        
        if (canRefresh) {

            // Refresh display
            this.refresh(true);

            // Select artifact if it is new
            Artifact artifact = (Artifact) arg;
            if (artifact != null && !artifact.isDeleted() && canRefresh) {
                TreeItem<Artifact> item = searchTreeItem(treeItemRoot, artifact);

                // Expand parent
                if (item !=null && item.getParent() != null) {
                    item.getParent().setExpanded(true);
                    treeView.getSelectionModel().select(item);
                }
            }

        }

    }

    // Save expanded state of each node of the tree.
    private void saveExpandedStates(TreeItem<Artifact> rootItem) {

        if (rootItem.getValue() instanceof Folder) {
            expandedStates.put(rootItem.getValue().getWorkspacePath(), rootItem.isExpanded());
        }

        for (TreeItem<Artifact> child : rootItem.getChildren()) {
            this.saveExpandedStates(child);
        }

    }

    /**
     * Search a tree item matching the given artifact.
     *
     * @param rootItem Item to start the search from
     * @param artifact Matching artifact
     * @return TreeItem found or null
     */
    private TreeItem<Artifact> searchTreeItem(TreeItem<Artifact> rootItem, Artifact artifact) {
        if (!(rootItem.getValue() instanceof PlaceHolderArtifact) && rootItem.getValue().getWorkspacePath().equals(artifact.getWorkspacePath())) {
            return rootItem;
        } else {
            for (TreeItem<Artifact> item : rootItem.getChildren()) {
                TreeItem<Artifact> result = this.searchTreeItem(item, artifact);
                if (result != null) {
                    return result;
                }
            }
            return null;
        }
    }

    @Override
    public void handle(MouseEvent event) {

        if (event.getSource() instanceof ArtifactTreeCell) {

            ArtifactTreeCell cell = (ArtifactTreeCell) event.getSource();

            // Document
            if (cell.getItem() instanceof Document) {

                Document document = (Document) cell.getItem();

                // Double click
                if (event.getClickCount() == 2 && event.getButton().equals(MouseButton.PRIMARY)) {
                    if (onOpenFile != null) {
                        onOpenFile.handle(new OpenFileEvent(EventType.ROOT, document));
                    }
                }
            }

        }
    }

    /**
     * @return the onArtifactSelected
     */
    public EventHandler<ArtifactSelectedEvent> getOnArtifactSelected() {
        return onArtifactSelected;
    }

    /**
     * @param onArtifactSelected the onArtifactSelected to set
     */
    public void setOnArtifactSelected(EventHandler<ArtifactSelectedEvent> onArtifactSelected) {
        this.onArtifactSelected = onArtifactSelected;
    }

    /**
     * @return the onProjectClosed
     */
    public EventHandler<ProjectClosedEvent> getOnProjectClosed() {
        return onProjectClosed;
    }

    /**
     * @param onProjectClosed the onProjectClosed to set
     */
    public void setOnProjectClosed(EventHandler<ProjectClosedEvent> onProjectClosed) {
        this.onProjectClosed = onProjectClosed;
    }

    /**
     * @return the onNewFile
     */
    public EventHandler<NewFileEvent> getOnNewFile() {
        return onNewFile;
    }

    /**
     * @param onNewFile the onNewFile to set
     */
    public void setOnNewFile(EventHandler<NewFileEvent> onNewFile) {
        this.onNewFile = onNewFile;
    }

    @Override
    public void saveState(Map<String, Object> state) throws StateException {
        super.saveState(state);

        // Update expanded states
        this.saveExpandedStates(treeItemRoot);

        // Expanded states
        state.put("expandedStates", expandedStates);

        // Selected
        if (this.getSelectedArtifact() != null) {
            state.put("selected", this.getSelectedArtifact().getWorkspacePath());
        }
    }

    @Override
    public void restoreState(Map<String, Object> state) throws StateException {
        super.restoreState(state);

        // Expanded states
        Map<String, Boolean> readStates = (Map<String, Boolean>) state.get("expandedStates");
        if (readStates != null) {
            expandedStates = readStates;
        }

        // Selected
        lastSelectedArtifact = ApplicationContext.getWorkspace().findArtifactByWorkspacePath((String) state.get("selected"));

        this.refresh(false);
    }

    /**
     * @return the onNewFolder
     */
    public EventHandler<NewFolderEvent> getOnNewFolder() {
        return onNewFolder;
    }

    /**
     * @param onNewFolder the onNewFolder to set
     */
    public void setOnNewFolder(EventHandler<NewFolderEvent> onNewFolder) {
        this.onNewFolder = onNewFolder;
    }

    /**
     * @return the onOpenFile
     */
    public EventHandler<OpenFileEvent> getOnOpenFile() {
        return onOpenFile;
    }

    /**
     * @param onOpenFile the onOpenFile to set
     */
    public void setOnOpenFile(EventHandler<OpenFileEvent> onOpenFile) {
        this.onOpenFile = onOpenFile;
    }

    /**
     * @return the onDeleteArtifact
     */
    public EventHandler<DeleteArtifactEvent> getOnDeleteArtifact() {
        return onDeleteArtifact;
    }

    /**
     * @param onDeleteArtifact the onDeleteArtifact to set
     */
    public void setOnDeleteArtifact(EventHandler<DeleteArtifactEvent> onDeleteArtifact) {
        this.onDeleteArtifact = onDeleteArtifact;
    }

    /**
     * @return the onRenameArtifact
     */
    public EventHandler<RenameArtifactEvent> getOnRenameArtifact() {
        return onRenameArtifact;
    }

    /**
     * @param onRenameArtifact the onRenameArtifact to set
     */
    public void setOnRenameArtifact(EventHandler<RenameArtifactEvent> onRenameArtifact) {
        this.onRenameArtifact = onRenameArtifact;
    }

    /**
     * @return the onImportArtifacts
     */
    public EventHandler<ImportArtifactsEvent> getOnImportArtifacts() {
        return onImportArtifacts;
    }

    /**
     * @param onImportArtifacts the onImportArtifacts to set
     */
    public void setOnImportArtifacts(EventHandler<ImportArtifactsEvent> onImportArtifacts) {
        this.onImportArtifacts = onImportArtifacts;
    }

    /*
     * Artifact selected event.
     */
    public class ArtifactSelectedEvent extends Event {

        private Artifact artifact;

        public ArtifactSelectedEvent(EventType<? extends Event> et) {
            super(et);
        }

        public ArtifactSelectedEvent(EventType<? extends Event> et, Artifact artifact) {
            super(et);
            this.artifact = artifact;
        }

        /**
         * @return the artifact
         */
        public Artifact getArtifact() {
            return artifact;
        }
    }

    /**
     * Open file event.
     */
    public class OpenFileEvent extends Event {

        private Document document;

        protected OpenFileEvent(EventType<? extends Event> et) {
            super(et);
        }

        protected OpenFileEvent(EventType<? extends Event> et, Document document) {
            super(et);
            this.document = document;
        }

        /**
         * @return the document
         */
        public Document getDocument() {
            return document;
        }
    }

    /**
     * Project closed event.
     */
    public class ProjectClosedEvent extends Event {

        private Project project;

        protected ProjectClosedEvent(EventType<? extends Event> et) {
            super(et);
        }

        protected ProjectClosedEvent(EventType<? extends Event> et, Project project) {
            super(et);
            this.project = project;
        }

        /**
         * @return the project
         */
        public Project getProject() {
            return project;
        }
    }
    
    /**
     * Delete artifact event.
     */
    public class DeleteArtifactEvent extends Event {
        
        private Artifact artifact;
        
        protected DeleteArtifactEvent(EventType<? extends Event> et) {
            super(et);
        }

        protected DeleteArtifactEvent(EventType<? extends Event> et, Artifact artifact) {
            super(et);
            this.artifact = artifact;
        }

        /**
         * @return the artifact
         */
        public Artifact getArtifact() {
            return artifact;
        }
    }
    
    /**
     * Rename artifact event.
     */
    public class RenameArtifactEvent extends Event {
        
        private Artifact artifact;
        
        protected RenameArtifactEvent(EventType<? extends Event> et) {
            super(et);
        }

        protected RenameArtifactEvent(EventType<? extends Event> et, Artifact artifact) {
            super(et);
            this.artifact = artifact;
        }

        /**
         * @return the artifact
         */
        public Artifact getArtifact() {
            return artifact;
        }
    }

    /**
     * New file event.
     */
    public class NewFileEvent extends Event {

        private Folder folder;

        protected NewFileEvent(EventType<? extends Event> et) {
            super(et);
        }

        protected NewFileEvent(EventType<? extends Event> et, Folder folder) {
            super(et);
            this.folder = folder;
        }

        /**
         * @return the folder
         */
        public Folder getFolder() {
            return folder;
        }
    }
    
      /**
     * New folder event.
     */
    public class NewFolderEvent extends Event {

        private Folder folder;

        protected NewFolderEvent(EventType<? extends Event> et) {
            super(et);
        }

        protected NewFolderEvent(EventType<? extends Event> et, Folder folder) {
            super(et);
            this.folder = folder;
        }

        /**
         * @return the folder
         */
        public Folder getFolder() {
            return folder;
        }
    }
    
    /**
     * Import artifacts event.
     */
    public class ImportArtifactsEvent extends Event {
        
        private Folder folder;

        protected ImportArtifactsEvent(EventType<? extends Event> et) {
            super(et);
        }

        protected ImportArtifactsEvent(EventType<? extends Event> et, Folder folder) {
            super(et);
            this.folder = folder;
        }

        /**
         * @return the folder
         */
        public Folder getFolder() {
            return folder;
        }
    }
}
