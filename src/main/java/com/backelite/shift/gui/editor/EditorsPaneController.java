package com.backelite.shift.gui.editor;

/*
 * #%L
 * EditorsPaneController.java - shift - 2013
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
import com.backelite.shift.ApplicationContext;
import com.backelite.shift.gui.AbstractController;
import com.backelite.shift.gui.dialog.ConfirmDialogController;
import com.backelite.shift.gui.FXMLLoaderFactory;
import com.backelite.shift.workspace.artifact.Document;
import com.backelite.shift.plugin.PluginException;
import com.backelite.shift.state.StateException;
import com.backelite.shift.workspace.Workspace;
import com.backelite.shift.workspace.artifact.Project;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
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
    private EventHandler<ActiveDocumentChangedEvent> onActiveDocumentChanged;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);

        // Position
        AnchorPane.setTopAnchor(tabPane, 0.0);

        // Tab selection listener
        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> tab, Tab oldTab, Tab newTab) {
                if (onActiveDocumentChanged != null && newTab != null) {
                    EditorController controller = (EditorController) newTab.getUserData();
                    onActiveDocumentChanged.handle(new ActiveDocumentChangedEvent(EventType.ROOT, controller.getDocument()));
                }
            }
        });

        // Observe Worspace changes (in case a project is closed)
        ApplicationContext.getWorkspace().addObserver(this);
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

                node = ApplicationContext.getPluginRegistry().newEditor(document, loader);
                EditorController controller = (EditorController) loader.getController();
                controller.getDocument().addObserver(this);

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
                closeLink.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        final Tab tab = getTab((EditorController) ((Hyperlink) event.getSource()).getUserData());
                        final EditorController controller = (EditorController) tab.getUserData();

                        // Confirm close if document was modified
                        if (controller.getDocument().isModified()) {
                            displayConfirmDialog(getResourceBundle().getString("dialog.confirm.close_editor.unsaved.title"), getResourceBundle().getString("dialog.confirm.close_editor.unsaved.text"), new EventHandler<ConfirmDialogController.ChoiceEvent>() {
                                public void handle(ConfirmDialogController.ChoiceEvent t) {

                                    if (t.getChoice() == ConfirmDialogController.Choice.POSITIVE) {
                                        // Close editor
                                        controller.close();
                                        tabPane.getTabs().remove(tab);

                                        // Notify document changed
                                        if (tabPane.getTabs().size() == 0) {
                                            if (onActiveDocumentChanged != null) {
                                                onActiveDocumentChanged.handle(new ActiveDocumentChangedEvent(EventType.ROOT, null));
                                            }
                                        }
                                    }
                                }
                            });
                        } else {
                            // Close editor
                            controller.close();
                            tabPane.getTabs().remove(tab);

                            // Notify document changed
                            if (tabPane.getTabs().size() == 0) {
                                if (onActiveDocumentChanged != null) {
                                    onActiveDocumentChanged.handle(new ActiveDocumentChangedEvent(EventType.ROOT, null));
                                }
                            }
                        }


                    }
                });

                tabPane.getTabs().add(tab);
                SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
                selectionModel.select(tab);

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

    public Document getActiveDocument() {

        EditorController controller = this.getActiveEditorController();
        if (controller != null) {
            return controller.getDocument();
        } else {
            return null;
        }
    }

    public EditorController getActiveEditorController() {

        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null) {
            return (EditorController) selectedTab.getUserData();
        } else {
            return null;
        }
    }

    public void update(Observable observable, Object arg) {

        // A document was updated
        if (observable instanceof Document) {

            ObservableList<Tab> tabs = tabPane.getTabs();
            List<Tab> tabsToRemove = new ArrayList<Tab>();
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

                        // Clear editor history
                        EditorController editorController = (EditorController) tab.getUserData();
                        editorController.clearHistory();

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
            if (onActiveDocumentChanged != null) {
                onActiveDocumentChanged.handle(new ActiveDocumentChangedEvent(EventType.ROOT, (Document) observable));
            }

        // Workspace was updated
        } else if (observable instanceof Workspace) {

            if (arg != null && arg instanceof Project) {
                Project project = (Project) arg;

                // Project was removed from workspace : all its documents must be closed
                if (!ApplicationContext.getWorkspace().getProjects().contains(project)) {

                    List<Tab> tabsToRemove = new ArrayList<Tab>();
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
        List<Map<String, Object>> editorsState = new ArrayList<Map<String, Object>>();
        for (Tab tab : tabPane.getTabs()) {
            EditorController controller = (EditorController) tab.getUserData();
            Map<String, Object> editorState = new HashMap<String, Object>();
            if (tab.isSelected()) {
                editorState.put("selected", tab.isSelected());
            }
            editorState.put("documentWorkspacePath", controller.getDocument().getWorkspacePath());
            editorsState.add(editorState);
        }

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
     * @return the onActiveDocumentChanged
     */
    public EventHandler<ActiveDocumentChangedEvent> getOnActiveDocumentChanged() {
        return onActiveDocumentChanged;
    }

    /**
     * @param onActiveDocumentChanged the onActiveDocumentChanged to set
     */
    public void setOnActiveDocumentChanged(EventHandler<ActiveDocumentChangedEvent> onActiveDocumentChanged) {
        this.onActiveDocumentChanged = onActiveDocumentChanged;
    }

    public class ActiveDocumentChangedEvent extends Event {

        private Document document;

        public ActiveDocumentChangedEvent(EventType<? extends Event> et) {
            super(et);
        }

        public ActiveDocumentChangedEvent(EventType<? extends Event> et, Document document) {
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
