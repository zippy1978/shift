package com.backelite.shift.gui.projectnavigator;

/*
 * #%L
 * ArtifactTreeCell.java - shift - 2013
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
import com.backelite.shift.workspace.artifact.Artifact;
import com.backelite.shift.workspace.artifact.Document;
import com.backelite.shift.workspace.artifact.Folder;
import com.backelite.shift.workspace.artifact.Project;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeCell;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class ArtifactTreeCell extends TreeCell<Artifact> {

    private ProjectNavigatorController projectNavigatorController;

    public ArtifactTreeCell() {
        super();

    }

    @Override
    protected void updateItem(Artifact artifact, boolean empty) {
        super.updateItem(artifact, empty);

        // Retrieve parent controller from user data
        this.projectNavigatorController = (ProjectNavigatorController) this.getUserData();
        
        if (!empty) {
            setContentDisplay(ContentDisplay.LEFT);
            setText(artifact.getName());

            // Project
            if (artifact instanceof Project) {

                // Context menu
                ContextMenu projectContextMenu = new ContextMenu();

                // Close
                MenuItem closeMenuItem = new MenuItem(projectNavigatorController.getResourceBundle().getString("project_navigator.project.menu.close"));
                projectContextMenu.getItems().add(closeMenuItem);
                closeMenuItem.setOnAction(new EventHandler() {
                    public void handle(Event t) {
                        if (getItem() instanceof Project) {
                            projectNavigatorController.closeProject((Project) getItem());
                        }
                    }
                });

                // New file
                MenuItem newFileMenuItem = new MenuItem(projectNavigatorController.getResourceBundle().getString("project_navigator.folder.menu.new_file"));
                projectContextMenu.getItems().add(newFileMenuItem);
                newFileMenuItem.setOnAction(new EventHandler() {
                    public void handle(Event t) {
                        if (getItem() instanceof Project) {
                            projectNavigatorController.newFile((Project) getItem());
                        }
                    }
                });

                // New folder
                MenuItem newFolderMenuItem = new MenuItem(projectNavigatorController.getResourceBundle().getString("project_navigator.folder.menu.new_folder"));
                projectContextMenu.getItems().add(newFolderMenuItem);
                newFolderMenuItem.setOnAction(new EventHandler() {
                    public void handle(Event t) {
                        if (getItem() instanceof Project) {
                            projectNavigatorController.newFolder((Project) getItem());
                        }
                    }
                });
                
                // -
                projectContextMenu.getItems().add(new SeparatorMenuItem());
                
                // Delete
                MenuItem deleteMenuItem = new MenuItem(projectNavigatorController.getResourceBundle().getString("project_navigator.artifact.menu.delete"));
                projectContextMenu.getItems().add(deleteMenuItem);
                deleteMenuItem.setOnAction(new EventHandler() {
                    public void handle(Event t) {
                        if (getItem() instanceof Project) {
                            projectNavigatorController.deleteArtifact(getItem());
                        }
                    }
                });

                setContextMenu(projectContextMenu);

                // Folder
            } else if (artifact instanceof Folder) {

                // Context menu
                ContextMenu folderContextMenu = new ContextMenu();

                // New file
                MenuItem newFileMenuItem = new MenuItem(projectNavigatorController.getResourceBundle().getString("project_navigator.folder.menu.new_file"));
                folderContextMenu.getItems().add(newFileMenuItem);
                newFileMenuItem.setOnAction(new EventHandler() {
                    public void handle(Event t) {
                        if (getItem() instanceof Folder) {
                            projectNavigatorController.newFile((Folder) getItem());
                        }
                    }
                });

                // New folder
                MenuItem newFolderMenuItem = new MenuItem(projectNavigatorController.getResourceBundle().getString("project_navigator.folder.menu.new_folder"));
                folderContextMenu.getItems().add(newFolderMenuItem);
                newFolderMenuItem.setOnAction(new EventHandler() {
                    public void handle(Event t) {
                        if (getItem() instanceof Folder) {
                            projectNavigatorController.newFolder((Folder) getItem());
                        }
                    }
                });
                
                // -
                folderContextMenu.getItems().add(new SeparatorMenuItem());
                
                // Delete
                MenuItem deleteMenuItem = new MenuItem(projectNavigatorController.getResourceBundle().getString("project_navigator.artifact.menu.delete"));
                folderContextMenu.getItems().add(deleteMenuItem);
                deleteMenuItem.setOnAction(new EventHandler() {
                    public void handle(Event t) {
                        if (getItem() instanceof Folder) {
                            projectNavigatorController.deleteArtifact(getItem());
                        }
                    }
                });

                setContextMenu(folderContextMenu);

                // Document
            } else if (artifact instanceof Document) {

                // Context menu
                ContextMenu documentContextMenu = new ContextMenu();

                // Delete
                MenuItem deleteMenuItem = new MenuItem(projectNavigatorController.getResourceBundle().getString("project_navigator.artifact.menu.delete"));
                documentContextMenu.getItems().add(deleteMenuItem);
                deleteMenuItem.setOnAction(new EventHandler() {
                    public void handle(Event t) {
                        if (getItem() instanceof Document) {
                            projectNavigatorController.deleteArtifact(getItem());
                        }
                    }
                });
                
                setContextMenu(documentContextMenu);
            }





            // Folder (including project)
            if (artifact instanceof Folder) {

                this.getStyleClass().add("folder");

            } else {
                this.getStyleClass().remove("folder");
            }
        }

    }
}
