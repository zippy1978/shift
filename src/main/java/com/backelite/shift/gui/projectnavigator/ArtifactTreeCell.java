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
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeCell;

/**
 * Artifact cell for project navigator TreeView.
 * Note that Platform.runLater blocks must be used in context menus 
 * handlers otherwise strange things happen (8.0.0-ea-b123)
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class ArtifactTreeCell extends TreeCell<Artifact> {

    private ProjectNavigatorController projectNavigatorController;
    private EventHandler<ActionEvent> closeActionEventHandler;
    private EventHandler<ActionEvent> newFileActionEventHandler;
    private EventHandler<ActionEvent> newFolderActionEventHandler;
    private EventHandler<ActionEvent> deleteActionEventHandler;
    private EventHandler<ActionEvent> renameActionEventHandler;

    public ArtifactTreeCell() {
        super();

    }
    
    @Override
    protected void updateItem(Artifact artifact, boolean empty) {
        
        super.updateItem(artifact, empty);

        
        // Retrieve parent controller from user data
        this.projectNavigatorController = (ProjectNavigatorController) this.getUserData();

        if (!empty && artifact != null) {
            setContentDisplay(ContentDisplay.LEFT);
            setText(artifact.getName());

            // Project
            if (artifact instanceof Project) {

                // Context menu
                ContextMenu projectContextMenu = new ContextMenu();

                // Close
                MenuItem closeMenuItem = new MenuItem(projectNavigatorController.getResourceBundle().getString("project_navigator.project.menu.close"));
                projectContextMenu.getItems().add(closeMenuItem);

                closeActionEventHandler = new EventHandler() {
                    @Override
                    public void handle(Event t) {
                        if (getItem() instanceof Project) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    projectNavigatorController.closeProject((Project) getItem());
                                }
                            });

                        }
                    }
                };
                closeMenuItem.setOnAction(new WeakEventHandler<>(closeActionEventHandler));

                // New file
                MenuItem newFileMenuItem = new MenuItem(projectNavigatorController.getResourceBundle().getString("project_navigator.folder.menu.new_file"));
                projectContextMenu.getItems().add(newFileMenuItem);
                newFileActionEventHandler = new EventHandler() {
                    @Override
                    public void handle(Event t) {
                        if (getItem() instanceof Project) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    projectNavigatorController.newFile((Project) getItem());
                                }
                            });

                        }
                    }
                };
                newFileMenuItem.setOnAction(new WeakEventHandler<>(newFileActionEventHandler));

                // New folder
                MenuItem newFolderMenuItem = new MenuItem(projectNavigatorController.getResourceBundle().getString("project_navigator.folder.menu.new_folder"));
                projectContextMenu.getItems().add(newFolderMenuItem);
                newFolderActionEventHandler = new EventHandler() {
                    @Override
                    public void handle(Event t) {
                        if (getItem() instanceof Project) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    projectNavigatorController.newFolder((Project) getItem());
                                }
                            });

                        }
                    }
                };
                newFolderMenuItem.setOnAction(new WeakEventHandler<>(newFolderActionEventHandler));
                
                // -
                projectContextMenu.getItems().add(new SeparatorMenuItem());
                
                // Rename
                MenuItem renameMenuItem = new MenuItem(projectNavigatorController.getResourceBundle().getString("project_navigator.artifact.menu.rename"));
                projectContextMenu.getItems().add(renameMenuItem);
                renameActionEventHandler = new EventHandler() {
                    @Override
                    public void handle(Event t) {
                        if (getItem() instanceof Project) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    projectNavigatorController.renameArtifact(getItem());
                                }
                            });

                        }
                    }
                };
                renameMenuItem.setOnAction(new WeakEventHandler<>(renameActionEventHandler));

                // -
                projectContextMenu.getItems().add(new SeparatorMenuItem());

                // Delete
                MenuItem deleteMenuItem = new MenuItem(projectNavigatorController.getResourceBundle().getString("project_navigator.artifact.menu.delete"));
                projectContextMenu.getItems().add(deleteMenuItem);
                deleteActionEventHandler = new EventHandler() {
                    @Override
                    public void handle(Event t) {
                        if (getItem() instanceof Project) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    projectNavigatorController.deleteArtifact(getItem());
                                }
                            });

                        }
                    }
                };
                deleteMenuItem.setOnAction(new WeakEventHandler<>(deleteActionEventHandler));

                setContextMenu(projectContextMenu);

                // Folder
            } else if (artifact instanceof Folder) {

                // Context menu
                ContextMenu folderContextMenu = new ContextMenu();

                // New file
                MenuItem newFileMenuItem = new MenuItem(projectNavigatorController.getResourceBundle().getString("project_navigator.folder.menu.new_file"));
                folderContextMenu.getItems().add(newFileMenuItem);
                newFileActionEventHandler = new EventHandler() {
                    @Override
                    public void handle(Event t) {
                        if (getItem() instanceof Folder) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    projectNavigatorController.newFile((Folder) getItem());
                                }
                            });

                        }
                    }
                };
                newFileMenuItem.setOnAction(new WeakEventHandler<>(newFileActionEventHandler));

                // New folder
                MenuItem newFolderMenuItem = new MenuItem(projectNavigatorController.getResourceBundle().getString("project_navigator.folder.menu.new_folder"));
                folderContextMenu.getItems().add(newFolderMenuItem);
                newFolderActionEventHandler = new EventHandler() {
                    @Override
                    public void handle(Event t) {
                        if (getItem() instanceof Folder) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    projectNavigatorController.newFolder((Folder) getItem());
                                }
                            });

                        }
                    }
                };
                newFolderMenuItem.setOnAction(new WeakEventHandler<>(newFolderActionEventHandler));
                
                // -
                folderContextMenu.getItems().add(new SeparatorMenuItem());
                
                // Rename
                MenuItem renameMenuItem = new MenuItem(projectNavigatorController.getResourceBundle().getString("project_navigator.artifact.menu.rename"));
                folderContextMenu.getItems().add(renameMenuItem);
                renameActionEventHandler = new EventHandler() {
                    @Override
                    public void handle(Event t) {
                        if (getItem() instanceof Folder) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    projectNavigatorController.renameArtifact(getItem());
                                }
                            });

                        }
                    }
                };
                renameMenuItem.setOnAction(new WeakEventHandler<>(renameActionEventHandler));

                // -
                folderContextMenu.getItems().add(new SeparatorMenuItem());

                // Delete
                MenuItem deleteMenuItem = new MenuItem(projectNavigatorController.getResourceBundle().getString("project_navigator.artifact.menu.delete"));
                folderContextMenu.getItems().add(deleteMenuItem);
                deleteActionEventHandler = new EventHandler() {
                    @Override
                    public void handle(Event t) {
                        if (getItem() instanceof Folder) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    projectNavigatorController.deleteArtifact(getItem());
                                }
                            });

                        }
                    }
                };
                deleteMenuItem.setOnAction(new WeakEventHandler<>(deleteActionEventHandler));

                setContextMenu(folderContextMenu);

                // Document
            } else if (artifact instanceof Document) {

                // Context menu
                ContextMenu documentContextMenu = new ContextMenu();

                // Rename
                MenuItem renameMenuItem = new MenuItem(projectNavigatorController.getResourceBundle().getString("project_navigator.artifact.menu.rename"));
                documentContextMenu.getItems().add(renameMenuItem);
                renameActionEventHandler = new EventHandler() {
                    @Override
                    public void handle(Event t) {
                        if (getItem() instanceof Document) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    projectNavigatorController.renameArtifact(getItem());
                                }
                            });

                        }
                    }
                };
                renameMenuItem.setOnAction(new WeakEventHandler<>(renameActionEventHandler));

                // -
                documentContextMenu.getItems().add(new SeparatorMenuItem());

                // Delete
                MenuItem deleteMenuItem = new MenuItem(projectNavigatorController.getResourceBundle().getString("project_navigator.artifact.menu.delete"));
                documentContextMenu.getItems().add(deleteMenuItem);
                deleteActionEventHandler = new EventHandler() {
                    @Override
                    public void handle(Event t) {
                        if (getItem() instanceof Document) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    projectNavigatorController.deleteArtifact(getItem());
                                }
                            });

                        }
                    }
                };
                deleteMenuItem.setOnAction(new WeakEventHandler<>(deleteActionEventHandler));

                setContextMenu(documentContextMenu);
            }





            // Folder (including project)
            if (artifact instanceof Folder) {

                this.getStyleClass().add("folder");

            } else {
                this.getStyleClass().remove("folder");
            }
            
        } else {
            
            // If cell is empty : clear it (for reuse)
            setText(null);
            setTextFill(null);
        }

    }
}
