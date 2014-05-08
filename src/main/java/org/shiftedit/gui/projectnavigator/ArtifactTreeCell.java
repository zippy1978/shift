package org.shiftedit.gui.projectnavigator;

/*
 * #%L
 * ArtifactTreeCell.java - shift - 2013
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
import org.shiftedit.workspace.artifact.Artifact;
import org.shiftedit.workspace.artifact.Document;
import org.shiftedit.workspace.artifact.Folder;
import org.shiftedit.workspace.artifact.Project;
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
    private EventHandler<ActionEvent> importActionEventHandler;
    private EventHandler<ActionEvent> deleteActionEventHandler;
    private EventHandler<ActionEvent> renameActionEventHandler;

    public ArtifactTreeCell() {
        super();

    }
    
    @Override
    protected void updateItem(Artifact artifact, boolean empty) {
        
        super.updateItem(artifact, empty);

        this.getStyleClass().remove("folder");
        
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
                
                // Import
                MenuItem importMenuItem = new MenuItem(projectNavigatorController.getResourceBundle().getString("project_navigator.folder.menu.import"));
                projectContextMenu.getItems().add(importMenuItem);
                importActionEventHandler = new EventHandler() {
                    @Override
                    public void handle(Event t) {
                        if (getItem() instanceof Project) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    projectNavigatorController.importArtifacts((Project) getItem());
                                }
                            });

                        }
                    }
                };
                importMenuItem.setOnAction(new WeakEventHandler<>(importActionEventHandler));
                
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
                
                // Import
                MenuItem importMenuItem = new MenuItem(projectNavigatorController.getResourceBundle().getString("project_navigator.folder.menu.import"));
                folderContextMenu.getItems().add(importMenuItem);
                importActionEventHandler = new EventHandler() {
                    @Override
                    public void handle(Event t) {
                        if (getItem() instanceof Folder) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    projectNavigatorController.importArtifacts((Folder) getItem());
                                }
                            });

                        }
                    }
                };
                importMenuItem.setOnAction(new WeakEventHandler<>(importActionEventHandler));
                
                
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

            }
            
        } else {
            
            // If cell is empty : clear it (for reuse)
            setText(null);
            setTextFill(null);
            
        }

    }
}
