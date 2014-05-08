package com.backelite.shift.gui.editor;

/*
 * #%L
 * EditorsPaneController.java - shift - 2013
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
import com.backelite.shift.gui.FXMLLoaderFactory;
import com.backelite.shift.gui.dialog.ConfirmDialogController;
import com.backelite.shift.plugin.PluginException;
import com.backelite.shift.state.StateException;
import com.backelite.shift.workspace.Workspace;
import com.backelite.shift.workspace.artifact.Document;
import com.backelite.shift.workspace.artifact.Project;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class EditorsPaneController extends AbstractController implements Observer {

    @FXML
    private TabPane tabPane;
    
    @FXML 
    private Label welcomeLabel;
    
    /**
     * EventHandler in charge of notifying active document change or active
     * document updates. To track only active editor / document change consider
     * using a listener on activeEditorControllerProperty instead.
     */
    private EventHandler<ActiveDocumentUpdatedEvent> onActiveDocumentUpdated;
    public ReadOnlyObjectWrapper<EditorController> activeEditorControllerProperty = new ReadOnlyObjectWrapper<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);

        // Position
        AnchorPane.setTopAnchor(tabPane, 0.0);

        // Tab selection listener
        tabPane.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> tab, Tab oldTab, Tab newTab) -> {
            if (onActiveDocumentUpdated != null && newTab != null) {
                EditorController controller = (EditorController) newTab.getUserData();
                if (controller != null) {
                    activeEditorControllerProperty.set(controller);
                    onActiveDocumentUpdated.handle(new ActiveDocumentUpdatedEvent(EventType.ROOT, controller.getDocument()));
                }
            } else {
                activeEditorControllerProperty.set(null);
            }
        });
        
        // Display welcome message if no tab opened
        this.refreshWelcomeMessage();

        // Observe Worspace changes (in case a project is closed)
        ApplicationContext.getWorkspace().addObserver(this);
    }
    
    private void refreshWelcomeMessage() {
        // Display welcome message if no tab opened
        welcomeLabel.setVisible(tabPane.getTabs().isEmpty());
    }

    /**
     * Open document into a new tab.
     *
     * @param document
     * @return Document tab
     */
    public Tab openDocument(Document document) {

        FXMLLoader loader = FXMLLoaderFactory.newInstance();
        Node node;

        if (!document.isOpened()) {

            try {

                // Load editor
                node = ApplicationContext.getPluginRegistry().newEditor(document, loader);
                EditorController controller = (EditorController) loader.getController();
                controller.getDocument().addObserver(this);

                // Close cross and tab head
                Hyperlink closeLink = new Hyperlink();
                closeLink.setText("X");
                closeLink.setFocusTraversable(false);
                closeLink.getStyleClass().add("close-button");
                closeLink.setUserData(controller);
                Tab tab = new Tab();
                tab.setClosable(false);
                tab.setText(document.getName());
                tab.setContent(node);
                tab.setUserData(controller);
                tab.setGraphic(closeLink);
                closeLink.setOnAction((ActionEvent event) -> {
                    final Tab tab1 = getTab((EditorController) ((Hyperlink) event.getSource()).getUserData());
                    final EditorController controller1 = (EditorController) tab1.getUserData();
                    if (controller1.getDocument().isModified()) {
                        displayConfirmDialog(getResourceBundle().getString("dialog.confirm.close_editor.unsaved.title"), getResourceBundle().getString("dialog.confirm.close_editor.unsaved.text"), (ConfirmDialogController.ChoiceEvent t) -> {
                            if (t.getChoice() == ConfirmDialogController.Choice.POSITIVE) {
                                closeTab(tab1);
                            }
                        });
                    } else {
                        closeTab(tab1);
                    }
                });

                tabPane.getTabs().add(tab);
                SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
                selectionModel.select(tab);
                
                this.refreshWelcomeMessage();

                return tab;

            } catch (PluginException ex) {
                this.displayErrorDialog(ex);
            }

        } else {

            // If document is already opened : only select the existing tab
            int index = 0;

            for (Tab tab : tabPane.getTabs()) {
                CodeEditorController controller = (CodeEditorController) tab.getUserData();
                if (controller.getDocument().getPath().equals(document.getPath())) {
                    SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
                    selectionModel.select(index);
                    return tab;
                }
                index++;
            }
        }

        return null;


    }

    private void closeTab(Tab tab) {

        // Close editor
        EditorController controller = (EditorController) tab.getUserData();
        controller.close();
        controller.setOnCursorChanged(null);
        tabPane.getTabs().remove(tab);
        tab.setGraphic(null);
        
        this.refreshWelcomeMessage();

        // Notify document changed
        if (tabPane.getTabs().size() == 0) {
            if (onActiveDocumentUpdated != null) {
                onActiveDocumentUpdated.handle(new ActiveDocumentUpdatedEvent(EventType.ROOT, null));
            }
        }
    }

    public Document getActiveDocument() {

        EditorController controller = this.getActiveEditorController();
        if (controller != null) {
            return controller.getDocument();
        } else {
            return null;
        }
    }

    public EditorController getActiveEditorController() {

        return activeEditorControllerProperty.get();
    }

    @Override
    public void update(Observable observable, Object arg) {

        // A document was updated
        if (observable instanceof Document) {

            ObservableList<Tab> tabs = tabPane.getTabs();
            List<Tab> tabsToRemove = new ArrayList<>();
            for (Tab tab : tabs) {

                EditorController controller = (EditorController) tab.getUserData();
                Document document = controller.getDocument();
                if (document == observable) {

                    // Document was modified ?
                    if (document.isModified()) {

                        // Update name with a star
                        tab.setText(document.getName() + "*");

                    } else {

                        // Update name without a star
                        tab.setText(document.getName());

                        // If document is deleted : mark tab to removal
                        if (document.isDeleted()) {
                            tabsToRemove.add(tab);
                        }
                    }
                }
            }

            // Remove tabs
            tabPane.getTabs().removeAll(tabsToRemove);

            // Notify change on active document
            if (onActiveDocumentUpdated != null) {
                onActiveDocumentUpdated.handle(new ActiveDocumentUpdatedEvent(EventType.ROOT, (Document) observable));
            }

            // Workspace was updated
        } else if (observable instanceof Workspace) {

            if (arg != null && arg instanceof Project) {
                Project project = (Project) arg;

                // Project was removed from workspace : all its documents must be closed
                if (!ApplicationContext.getWorkspace().getProjects().contains(project)) {

                    List<Tab> tabsToRemove = new ArrayList<>();
                    for (Tab tab : tabPane.getTabs()) {
                        CodeEditorController controller = (CodeEditorController) tab.getUserData();
                        if (controller.getDocument().getProject() == project) {
                            tabsToRemove.add(tab);
                        }
                    }
                    for (Tab tab : tabsToRemove) {
                        tabPane.getTabs().remove(tab);
                    }
                }
            }

        }
    }

    /**
     * Return Tab matching a given EditorController instance
     *
     * @param controller EditorController to search
     * @return Matching Tab or null (if not found)
     */
    private Tab getTab(EditorController controller) {

        for (Tab tab : tabPane.getTabs()) {
            if (tab.getUserData() == controller) {
                return tab;
            }
        }

        return null;
    }

    @Override
    public void saveState(Map<String, Object> state) throws StateException {
        super.saveState(state);

        // Editors
        List<Map<String, Object>> editorsState = new ArrayList<>();
        tabPane.getTabs().stream().map((tab) -> {
            EditorController controller = (EditorController) tab.getUserData();
            Map<String, Object> editorState = new HashMap<>();
            if (tab.isSelected()) {
                editorState.put("selected", tab.isSelected());
            }
            editorState.put("documentWorkspacePath", controller.getDocument().getWorkspacePath());
            return editorState;
        }).forEach((editorState) -> {
            editorsState.add(editorState);
        });

        state.put("editors", editorsState);
    }

    @Override
    public void restoreState(Map<String, Object> state) throws StateException {
        super.restoreState(state);

        // Editors
        List<Map<String, Object>> editorsState = (List<Map<String, Object>>) state.get("editors");
        Tab selectedTab = null;
        if (editorsState != null) {
            for (Map<String, Object> editorState : editorsState) {
                Document document = (Document) ApplicationContext.getWorkspace().findArtifactByWorkspacePath((String) editorState.get("documentWorkspacePath"));
                // If document is still in workspace : open a tab
                if (document != null) {
                    Tab tab = this.openDocument(document);
                    if (editorState.get("selected") != null) {
                        selectedTab = tab;
                    }
                }
            }
            // Restore selected tab
            if (selectedTab != null) {
                SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
                selectionModel.select(selectedTab);
            }
        }

    }

    /**
     * @return the onActiveDocumentUpdated
     */
    public EventHandler<ActiveDocumentUpdatedEvent> getOnActiveDocumentUpdated() {
        return onActiveDocumentUpdated;
    }

    /**
     * @param onActiveDocumentChanged the onActiveDocumentChanged to set
     */
    public void setOnActiveDocumentUpdated(EventHandler<ActiveDocumentUpdatedEvent> onActiveDocumentChanged) {
        this.onActiveDocumentUpdated = onActiveDocumentChanged;
    }

    public class ActiveDocumentUpdatedEvent extends Event {

        private Document document;

        public ActiveDocumentUpdatedEvent(EventType<? extends Event> et) {
            super(et);
        }

        public ActiveDocumentUpdatedEvent(EventType<? extends Event> et, Document document) {
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
}
