package org.shiftedit.gui;

/*
 * #%L
 * MainController.java - shift - 2013
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
import org.shiftedit.ApplicationContext;
import org.shiftedit.Constants;
import org.shiftedit.gui.dialog.ConfirmDialogController;
import org.shiftedit.gui.dialog.DialogController;
import org.shiftedit.gui.dialog.PickerDialogController;
import org.shiftedit.gui.dialog.WelcomeDialogController;
import org.shiftedit.gui.editor.EditorController;
import org.shiftedit.gui.editor.EditorsPaneController;
import org.shiftedit.gui.preview.PreviewController;
import org.shiftedit.gui.projectnavigator.ProjectNavigatorController;
import org.shiftedit.gui.projectwizard.ProjectWizardController;
import org.shiftedit.gui.statusbar.StatusBarController;
import org.shiftedit.plugin.PluginException;
import org.shiftedit.plugin.PreviewFactory;
import org.shiftedit.plugin.ProjectWizardFactory;
import org.shiftedit.state.StateException;
import org.shiftedit.util.FileUtils;
import org.shiftedit.workspace.artifact.Artifact;
import org.shiftedit.workspace.artifact.Document;
import org.shiftedit.workspace.artifact.FileSystemProject;
import org.shiftedit.workspace.artifact.Folder;
import org.shiftedit.workspace.artifact.Project;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.shiftedit.gui.preferences.PreferencesDialogController;
import org.shiftedit.workspace.LocalWorkspace;
import org.shiftedit.workspace.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class MainController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);
    @FXML
    private MenuBar menuBar;
    @FXML
    private SplitPane splitPane;
    @FXML
    private ProjectNavigatorController projectNavigatorController;
    @FXML
    private EditorsPaneController editorsPaneController;
    @FXML
    private StatusBarController statusBarController;
    private Menu fileMenu;
    private MenuItem newFileMenuItem;
    private MenuItem newFolderMenuItem;
    private MenuItem saveMenuItem;
    private MenuItem closeProjectMenuItem;
    private MenuItem quitMenuItem;
    private Menu editMenu;
    private MenuItem undoMenuItem;
    private MenuItem redoMenuItem;
    private MenuItem cutMenuItem;
    private MenuItem copyMenuItem;
    private MenuItem pasteMenuItem;
    private MenuItem selectAllMenuItem;
    private Menu findSubMenu;
    private MenuItem contentAssistMenuItem;
    private Menu windowMenu;
    private MenuItem newPreviewMenuItem;
    private Menu helpMenu;
    private MenuItem preferencesMenuItem;
    private MenuItem aboutMenuItem;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        super.initialize(url, rb);

        menuBar.setUseSystemMenuBar(true);
        this.buildMenu();

        // Register open document handler on project navigator
        projectNavigatorController.setOnOpenFile((ProjectNavigatorController.OpenFileEvent t) -> {
            editorsPaneController.openDocument(t.getDocument());
        });

        // Register artifact selection on project navigator
        projectNavigatorController.setOnArtifactSelected((ProjectNavigatorController.ArtifactSelectedEvent t) -> {
            refreshFileMenu();
        });

        // Register project close action on project navigator
        projectNavigatorController.setOnProjectClosed((ProjectNavigatorController.ProjectClosedEvent t) -> {
            handleCloseProjectMenuAction();
        });

        // Register new file action on project navigator
        projectNavigatorController.setOnNewFile((ProjectNavigatorController.NewFileEvent t) -> {
            handleNewFileMenuAction();
        });

        // Register new folder action on project navigator
        projectNavigatorController.setOnNewFolder((ProjectNavigatorController.NewFolderEvent t) -> {
            handleNewFolderMenuAction();
        });

        // Register import artifacts action on project navigator
        projectNavigatorController.setOnImportArtifacts((ProjectNavigatorController.ImportArtifactsEvent t) -> {
            handleImportArtifactsMenuAction();
        });

        // Register rename artifact action on project navigator
        projectNavigatorController.setOnRenameArtifact((ProjectNavigatorController.RenameArtifactEvent t) -> {
            handleRenameArtifactMenuAction();
        });

        // Register delete artifact action on project navigator
        projectNavigatorController.setOnDeleteArtifact((ProjectNavigatorController.DeleteArtifactEvent t) -> {
            handleDeleteArtifactMenuAction();
        });

        // Register active document change listener on editor pane
        editorsPaneController.setOnActiveDocumentUpdated((EditorsPaneController.ActiveDocumentUpdatedEvent t) -> {
            refreshFileMenu();
            refreshEditMenu();
            refreshWindowMenu();
            EditorController editorController = editorsPaneController.getActiveEditorController();
            if (editorController != null) {
                statusBarController.setCursorPosition(editorController.getCursorPosition());
                if (editorController.getOnCursorChanged() == null) {
                    editorController.setOnCursorChanged((EditorController.CursorChangedEvent t1) -> {
                        statusBarController.setCursorPosition(editorsPaneController.getActiveEditorController().getCursorPosition());
                    });
                }
            }
        });

        // If menu is application wide : split pane must dock to top
        if (this.supportsApplicationWideMenu()) {
            AnchorPane.setTopAnchor(splitPane, 0.0);
        }

        // Open welcome dialog if first start or upgraded version
        if (ApplicationContext.isFirstLaunch()) {
            this.openWelcomeWindow("/webcontent/firstlaunch.html");
        } else if (ApplicationContext.isUpdated() && !ApplicationContext.isSnapshotRelease()) {
            this.openWelcomeWindow("/webcontent/updated.html");
        }
    }

    private void openWelcomeWindow(String page) {

        FXMLLoader loader = FXMLLoaderFactory.newInstance();
        try {
            Stage stage = this.newDecoratedWindow(getResourceBundle().getString("welcome.title"), (Parent) loader.load(getClass().getResourceAsStream("/fxml/welcome_dialog.fxml")));
            WelcomeDialogController controller = (WelcomeDialogController) loader.getController();
            controller.setStage(stage);
            controller.setParentController(this);
            controller.setPage(page);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException ex) {
            this.displayErrorDialog(ex);
        }
    }

    /**
     * Return a key combination (shortcut) read from preferences.
     *
     * @param name Name of the shortcut
     * @return KeyCombination or null
     */
    private KeyCombination getShortcut(String name) {
        KeyCombination result = null;
        Map<String, String> shortcuts = (Map<String, String>) ApplicationContext.getPreferencesManager().getValue(Constants.PREFERENCES_KEY_SHORTCUTS);
        if (shortcuts != null && shortcuts.get(name) != null) {
            result = KeyCodeCombination.keyCombination(shortcuts.get(name));
        }
        return result;
    }

    private void refreshFileMenu() {

        Artifact selectedArtifact = projectNavigatorController.getSelectedArtifact();

        // New file is enabled only if a folder is selected in the project navigator
        if (selectedArtifact != null && (selectedArtifact instanceof Folder)) {
            newFileMenuItem.setDisable(false);
            newFolderMenuItem.setDisable(false);
        } else {
            newFileMenuItem.setDisable(true);
            newFolderMenuItem.setDisable(true);
        }

        // Close project is enabled only if a project is selected in the project navigator
        if (selectedArtifact != null && (selectedArtifact instanceof Project)) {
            closeProjectMenuItem.setDisable(false);
        } else {
            closeProjectMenuItem.setDisable(true);
        }

        // Save is enabled only is the current document in editor pane was modified
        Document activeDocument = editorsPaneController.getActiveDocument();
        if (activeDocument != null && activeDocument.isModified()) {
            saveMenuItem.setDisable(false);
        } else {
            saveMenuItem.setDisable(true);
        }
    }

    private void refreshEditMenu() {

        EditorController editorController = editorsPaneController.getActiveEditorController();

        if (editorController != null) {
            undoMenuItem.setDisable(!editorController.canUndo());
            redoMenuItem.setDisable(!editorController.canRedo());
            selectAllMenuItem.setDisable(false);
            copyMenuItem.setDisable(false);
            pasteMenuItem.setDisable(false);
            cutMenuItem.setDisable(false);
            findSubMenu.setDisable(!editorController.canSearch());
            contentAssistMenuItem.setDisable(!editorController.canContentAssist());
        } else {
            undoMenuItem.setDisable(true);
            redoMenuItem.setDisable(true);
            selectAllMenuItem.setDisable(true);
            copyMenuItem.setDisable(true);
            pasteMenuItem.setDisable(true);
            cutMenuItem.setDisable(true);
            findSubMenu.setDisable(true);
            contentAssistMenuItem.setDisable(true);
        }
    }

    private void refreshWindowMenu() {

        EditorController editorController = editorsPaneController.getActiveEditorController();

        // New preview (only if active document can be previewed)
        if (editorController != null) {
            newPreviewMenuItem.setDisable(!ApplicationContext.getPluginRegistry().canPreview(editorController.getDocument()));
        } else {
            newPreviewMenuItem.setDisable(true);
        }

    }

    /**
     * Build application menu.
     */
    private void buildMenu() {

        // File menu
        fileMenu = new Menu(this.getResourceBundle().getString("main.menu.file"));

        // File > New project
        this.buildNewProjectSubMenu();

        // File > New file
        newFileMenuItem = new MenuItem(this.getResourceBundle().getString("main.menu.file.new_file"));
        newFileMenuItem.setOnAction((ActionEvent t) -> {
            handleNewFileMenuAction();
        });
        newFileMenuItem.setAccelerator(this.getShortcut(Constants.SHORTCUT_NEW_FILE));
        fileMenu.getItems().add(newFileMenuItem);

        // File > New folder
        newFolderMenuItem = new MenuItem(this.getResourceBundle().getString("main.menu.file.new_folder"));
        newFolderMenuItem.setOnAction((ActionEvent t) -> {
            handleNewFolderMenuAction();
        });
        fileMenu.getItems().add(newFolderMenuItem);

        // File > -
        fileMenu.getItems().add(new SeparatorMenuItem());

        // File > Open project
        MenuItem openProjectMenuItem = new MenuItem(this.getResourceBundle().getString("main.menu.file.open_project"));
        openProjectMenuItem.setAccelerator(this.getShortcut(Constants.SHORTCUT_OPEN_PROJECT));
        openProjectMenuItem.setOnAction((ActionEvent t) -> {
            handleOpenProjectMenuAction();
        });

        fileMenu.getItems().add(openProjectMenuItem);
        // File > Close project
        closeProjectMenuItem = new MenuItem(this.getResourceBundle().getString("main.menu.file.close_project"));
        closeProjectMenuItem.setOnAction((ActionEvent t) -> {
            handleCloseProjectMenuAction();
        });
        fileMenu.getItems().add(closeProjectMenuItem);

        // File > -
        fileMenu.getItems().add(new SeparatorMenuItem());
        // File > Save
        saveMenuItem = new MenuItem(this.getResourceBundle().getString("main.menu.file.save"));
        saveMenuItem.setAccelerator(this.getShortcut(Constants.SHORTCUT_SAVE));
        saveMenuItem.setOnAction((ActionEvent t) -> {
            handleSaveMenuAction();
        });
        fileMenu.getItems().add(saveMenuItem);

        // File > Quit (only available when no application wide menu)
        if (!this.supportsApplicationWideMenu()) {
            // File > -
            fileMenu.getItems().add(new SeparatorMenuItem());
            // File > Quit 
            quitMenuItem = new MenuItem(this.getResourceBundle().getString("main.menu.file.quit"));
            quitMenuItem.setOnAction((ActionEvent t) -> {
                ApplicationContext.getMainStage().close();
            });
            fileMenu.getItems().add(quitMenuItem);
        }

        // Edit menu
        editMenu = new Menu(this.getResourceBundle().getString("main.menu.edit"));

        // Edit > Undo
        undoMenuItem = new MenuItem(this.getResourceBundle().getString("main.menu.edit.undo"));
        undoMenuItem.setAccelerator(this.getShortcut(Constants.SHORTCUT_UNDO));
        undoMenuItem.setDisable(true);
        undoMenuItem.setOnAction((ActionEvent t) -> {
            handleUndoMenuAction();
        });
        editMenu.getItems().add(undoMenuItem);

        // Edit > Redo
        redoMenuItem = new MenuItem(this.getResourceBundle().getString("main.menu.edit.redo"));
        redoMenuItem.setAccelerator(this.getShortcut(Constants.SHORTCUT_REDO));
        redoMenuItem.setDisable(true);
        redoMenuItem.setOnAction((ActionEvent t) -> {
            handleRedoMenuAction();
        });
        editMenu.getItems().add(redoMenuItem);

        // Edit > -
        editMenu.getItems().add(new SeparatorMenuItem());

        // Edit > Cut
        cutMenuItem = new MenuItem(this.getResourceBundle().getString("main.menu.edit.cut"));
        cutMenuItem.setAccelerator(this.getShortcut(Constants.SHORTCUT_CUT));
        cutMenuItem.setDisable(true);
        cutMenuItem.setOnAction((ActionEvent t) -> {
            handleCutMenuAction();
        });
        editMenu.getItems().add(cutMenuItem);

        // Edit > Copy
        copyMenuItem = new MenuItem(this.getResourceBundle().getString("main.menu.edit.copy"));
        copyMenuItem.setAccelerator(this.getShortcut(Constants.SHORTCUT_COPY));
        copyMenuItem.setDisable(true);
        copyMenuItem.setOnAction((ActionEvent t) -> {
            handleCopyMenuAction();
        });
        editMenu.getItems().add(copyMenuItem);

        // Edit > Paste
        pasteMenuItem = new MenuItem(this.getResourceBundle().getString("main.menu.edit.paste"));
        pasteMenuItem.setAccelerator(this.getShortcut(Constants.SHORTCUT_PASTE));
        pasteMenuItem.setDisable(true);
        pasteMenuItem.setOnAction((ActionEvent t) -> {
            handlePasteMenuAction();
        });
        editMenu.getItems().add(pasteMenuItem);

        // Edit > Select all
        selectAllMenuItem = new MenuItem(this.getResourceBundle().getString("main.menu.edit.select_all"));
        selectAllMenuItem.setAccelerator(this.getShortcut(Constants.SHORTCUT_SELECT_ALL));
        selectAllMenuItem.setDisable(true);
        selectAllMenuItem.setOnAction((ActionEvent t) -> {
            handleSelectAllMenuAction();
        });
        editMenu.getItems().add(selectAllMenuItem);

        // Edit > -
        editMenu.getItems().add(new SeparatorMenuItem());

        // Edit > Find
        this.buildFindSubMenu();

        // Edit > -
        editMenu.getItems().add(new SeparatorMenuItem());

        // Edit > Content Assist
        contentAssistMenuItem = new MenuItem(this.getResourceBundle().getString("main.menu.edit.content_assist"));
        contentAssistMenuItem.setAccelerator(this.getShortcut(Constants.SHORTCUT_CONTENT_ASSIST));
        contentAssistMenuItem.setDisable(true);
        contentAssistMenuItem.setOnAction((ActionEvent t) -> {
            handleContentAssistMenuAction();
        });
        editMenu.getItems().add(contentAssistMenuItem);

        // Window menu
        windowMenu = new Menu(this.getResourceBundle().getString("main.menu.window"));
        this.buildWindowMenu();

        // Help menu
        helpMenu = new Menu(this.getResourceBundle().getString("main.menu.help"));

        // Help > Preferences
        preferencesMenuItem = new MenuItem(this.getResourceBundle().getString("main.menu.help.preferences"));
        preferencesMenuItem.setOnAction((ActionEvent t) -> {
            handlePreferencesMenuAction();
        });
        helpMenu.getItems().add(preferencesMenuItem);

        // Help > -
        helpMenu.getItems().add(new SeparatorMenuItem());

        // Help > About
        aboutMenuItem = new MenuItem(this.getResourceBundle().getString("main.menu.help.about"));
        aboutMenuItem.setOnAction((ActionEvent t) -> {
            handleAboutMenuAction();
        });
        helpMenu.getItems().add(aboutMenuItem);

        // Add root menus to menu bar
        menuBar.getMenus().add(fileMenu);
        menuBar.getMenus().add(editMenu);
        menuBar.getMenus().add(windowMenu);
        menuBar.getMenus().add(helpMenu);

        // Refresh states
        this.refreshFileMenu();
        this.refreshEditMenu();
        this.refreshWindowMenu();
    }

    /**
     * Build Edit > Find submenu
     */
    private void buildFindSubMenu() {

        // Find menu
        findSubMenu = new Menu(this.getResourceBundle().getString("main.menu.edit.find"));

        editMenu.getItems().add(findSubMenu);

        // Find > Find...
        MenuItem findMenuItem = new MenuItem(this.getResourceBundle().getString("main.menu.edit.find.find"));
        findMenuItem.setAccelerator(this.getShortcut(Constants.SHORTCUT_FIND));
        findMenuItem.setOnAction((ActionEvent t) -> {
            handleFindMenuAction();
        });
        findSubMenu.getItems().add(findMenuItem);

        // Find > Find Next
        MenuItem findNextMenuItem = new MenuItem(this.getResourceBundle().getString("main.menu.edit.find.find_next"));
        findNextMenuItem.setAccelerator(this.getShortcut(Constants.SHORTCUT_FIND_NEXT));
        findNextMenuItem.setOnAction((ActionEvent t) -> {
            handleFindNextMenuAction();
        });
        findSubMenu.getItems().add(findNextMenuItem);

        // Find > Find Previous
        MenuItem findPreviousMenuItem = new MenuItem(this.getResourceBundle().getString("main.menu.edit.find.find_previous"));
        findPreviousMenuItem.setAccelerator(this.getShortcut(Constants.SHORTCUT_FIND_PREVIOUS));
        findPreviousMenuItem.setOnAction((ActionEvent t) -> {
            handleFindPreviousMenuAction();
        });
        findSubMenu.getItems().add(findPreviousMenuItem);

        // Find > -
        findSubMenu.getItems().add(new SeparatorMenuItem());

        // Find > Replace ...
        MenuItem replaceMenuItem = new MenuItem(this.getResourceBundle().getString("main.menu.edit.find.replace"));
        replaceMenuItem.setAccelerator(this.getShortcut(Constants.SHORTCUT_REPLACE));
        replaceMenuItem.setOnAction((ActionEvent t) -> {
            handleReplaceMenuAction();
        });
        findSubMenu.getItems().add(replaceMenuItem);

        // Find > Replace All...
        MenuItem replaceAllMenuItem = new MenuItem(this.getResourceBundle().getString("main.menu.edit.find.replace_all"));
        replaceAllMenuItem.setAccelerator(this.getShortcut(Constants.SHORTCUT_REPLACE_ALL));
        replaceAllMenuItem.setOnAction((ActionEvent t) -> {
            handleReplaceAllMenuAction();
        });
        findSubMenu.getItems().add(replaceAllMenuItem);
    }

    /**
     * Build new project menu according to plugins
     */
    private void buildNewProjectSubMenu() {

        // New project menu
        Menu newProjectMenu = new Menu(this.getResourceBundle().getString("main.menu.file.new_project"));

        List<ProjectWizardFactory> projectWizardFactories = ApplicationContext.getPluginRegistry().getProjectWizardFactories();
        for (final ProjectWizardFactory projectWizardFactory : projectWizardFactories) {
            MenuItem projectWizardFactoryMenu = new MenuItem(projectWizardFactory.getName());
            projectWizardFactoryMenu.setOnAction((ActionEvent t) -> {
                try {
                    // Open new project wizard
                    FXMLLoader loader = FXMLLoaderFactory.newInstance();
                    Stage stage = newModalWindow(projectWizardFactory.getName(), (Parent) ApplicationContext.getPluginRegistry().newProjectWizard(projectWizardFactory, loader));
                    ProjectWizardController controller = (ProjectWizardController) loader.getController();
                    controller.setProjectGenerator(projectWizardFactory.getProjectGenerator());
                    controller.setStage(stage);
                    stage.showAndWait();

                } catch (PluginException ex) {
                    displayErrorDialog(ex);
                }
            });
            newProjectMenu.getItems().add(projectWizardFactoryMenu);
        }

        fileMenu.getItems().add(newProjectMenu);
    }

    /**
     * Build dynamic opened windows (child windows) menu items in Window menu.
     */
    private void buildWindowMenu() {

        windowMenu.getItems().clear();

        // Window > New preview
        newPreviewMenuItem = new MenuItem(this.getResourceBundle().getString("main.menu.window.new_preview"));
        newPreviewMenuItem.setAccelerator(this.getShortcut(Constants.SHORTCUT_NEW_PREVIEW));
        newPreviewMenuItem.setOnAction((ActionEvent t) -> {
            handleNewPreviewMenuAction();
        });
        newPreviewMenuItem.setDisable(true);

        windowMenu.getItems().add(newPreviewMenuItem);

        if (this.getChildrenWindows().size() > 0) {

            // Window > -
            windowMenu.getItems().add(new SeparatorMenuItem());

            // Add opened windows
            for (Stage stage : this.getChildrenWindows()) {
                final Stage currentStage = stage;
                MenuItem item = new MenuItem(currentStage.getTitle());
                // On click : bring window to front
                item.setOnAction((ActionEvent t) -> {
                    currentStage.toFront();
                    currentStage.requestFocus();
                });
                windowMenu.getItems().add(item);
            }

        }

        // refresh state
        this.refreshWindowMenu();
    }

    @Override
    public void onChildWindowRemoved(Stage stage) {

        super.onChildWindowRemoved(stage);

        // Update opened windows in Window menu
        this.buildWindowMenu();

    }

    @Override
    public void onChildWindowAdded(Stage stage) {

        super.onChildWindowAdded(stage);

        // Update opened windows in Window menu
        Platform.runLater(() -> {
            buildWindowMenu();
        });
    }

    private void handleSaveMenuAction() {

        final Document document = editorsPaneController.getActiveDocument();

        // Async save
        ApplicationContext.getTaskManager().addTask(new Task() {
            @Override
            protected Object call() throws Exception {
                if (document != null && document.isModified()) {

                    updateTitle(String.format(getResourceBundle().getString("task.saving_file"), document.getName()));
                    document.save();
                    updateProgress(1, 1);

                }
                return document;
            }
        });

    }

    private void handleNewPreviewMenuAction() {

        // Open new preview
        final FXMLLoader loader = FXMLLoaderFactory.newInstance();

        try {

            // Preview active document (if any)
            final Document activeDocument = editorsPaneController.getActiveDocument();
            if (activeDocument != null) {

                // Get available preview factories for the active document
                final List<PreviewFactory> availableFactories = ApplicationContext.getPluginRegistry().getAvailablePreviewFactories(editorsPaneController.getActiveDocument());

                // More than on preview available : display picker
                if (availableFactories.size() > 1) {

                    // Option list
                    List<String> options = new ArrayList<>();
                    for (PreviewFactory factory : availableFactories) {
                        options.add(factory.getName());
                    }

                    // Display picker
                    displayPickerDialog(getResourceBundle().getString("main.preview_picker.title"), getResourceBundle().getString("main.preview_picker.text"), options, (PickerDialogController.SelectionEvent t) -> {
                        if (t.getPosition() > -1) {
                            PreviewFactory selection = availableFactories.get(t.getPosition());

                            try {
                                Stage stage = newDecoratedWindow("", (Parent) ApplicationContext.getPluginRegistry().newPreview(selection, loader));
                                setupAndShowPreviewWindow(stage, loader);
                            } catch (PluginException ex) {
                                displayErrorDialog(ex);
                            }
                        }
                    });
                    // Only one preview available ...
                } else {
                    Stage stage = newDecoratedWindow("", (Parent) ApplicationContext.getPluginRegistry().newPreview(editorsPaneController.getActiveDocument(), loader));
                    setupAndShowPreviewWindow(stage, loader);
                }

            }

        } catch (Exception ex) {
            this.displayErrorDialog(ex);
        }
    }

    private void handleFindMenuAction() {
        EditorController editorController = editorsPaneController.getActiveEditorController();
        if (editorController != null && ApplicationContext.getMainStage().isFocused()) {
            editorController.find();
        }
    }

    private void handleFindNextMenuAction() {
        EditorController editorController = editorsPaneController.getActiveEditorController();
        if (editorController != null && ApplicationContext.getMainStage().isFocused()) {
            editorController.findNext();
        }
    }

    private void handleFindPreviousMenuAction() {
        EditorController editorController = editorsPaneController.getActiveEditorController();
        if (editorController != null && ApplicationContext.getMainStage().isFocused()) {
            editorController.findPrevious();
        }
    }

    private void handleReplaceMenuAction() {
        EditorController editorController = editorsPaneController.getActiveEditorController();
        if (editorController != null && ApplicationContext.getMainStage().isFocused()) {
            editorController.replace();
        }
    }

    private void handleReplaceAllMenuAction() {
        EditorController editorController = editorsPaneController.getActiveEditorController();
        if (editorController != null && ApplicationContext.getMainStage().isFocused()) {
            editorController.replaceAll();
        }
    }

    /**
     * Setup a newly created preview window
     *
     * @param parentStage Preview window
     * @param loader Loader used for loading the preview window
     */
    private void setupAndShowPreviewWindow(Stage previewStage, FXMLLoader loader) {

        Document activeDocument = editorsPaneController.getActiveDocument();
        PreviewController previewController = (PreviewController) loader.getController();
        previewController.setDocument(activeDocument);
        previewController.setStage(previewStage);
        previewController.setParentController(this);
        ChangeListener<EditorController> changeListener = previewController.getActiveEditorChangeListener();
        if (changeListener != null) {
            editorsPaneController.activeEditorControllerProperty.addListener(new WeakChangeListener<>(changeListener));
        }

        previewStage.show();

    }

    private void handleUndoMenuAction() {

        EditorController editorController = editorsPaneController.getActiveEditorController();
        if (editorController != null && ApplicationContext.getMainStage().isFocused()) {
            editorController.undo();
        }
    }

    private void handleRedoMenuAction() {

        EditorController editorController = editorsPaneController.getActiveEditorController();
        if (editorController != null && ApplicationContext.getMainStage().isFocused()) {
            editorController.redo();
        }
    }

    private void handleCutMenuAction() {

        EditorController editorController = editorsPaneController.getActiveEditorController();
        if (editorController != null && ApplicationContext.getMainStage().isFocused()) {
            editorController.cut();
        }
    }

    private void handleCopyMenuAction() {

        EditorController editorController = editorsPaneController.getActiveEditorController();
        if (editorController != null && ApplicationContext.getMainStage().isFocused()) {
            editorController.copy();
        }
    }

    private void handlePasteMenuAction() {

        EditorController editorController = editorsPaneController.getActiveEditorController();
        if (editorController != null && ApplicationContext.getMainStage().isFocused()) {
            editorController.paste();
        }
    }

    private void handleSelectAllMenuAction() {

        EditorController editorController = editorsPaneController.getActiveEditorController();
        if (editorController != null && ApplicationContext.getMainStage().isFocused()) {
            editorController.selectAll();
        }
    }

    private void handleContentAssistMenuAction() {

        EditorController editorController = editorsPaneController.getActiveEditorController();
        if (editorController != null && editorController.canContentAssist() && ApplicationContext.getMainStage().isFocused()) {
            editorController.contentAssist();
        }
    }

    private void handlePreferencesMenuAction() {

        try {
            FXMLLoader loader = FXMLLoaderFactory.newInstance();
            Stage stage = newModalWindow(getResourceBundle().getString("preferences.title"), (Parent) loader.load(getClass().getResourceAsStream("/fxml/preferences_dialog.fxml")));
            PreferencesDialogController controller = (PreferencesDialogController) loader.getController();
            controller.setStage(stage);
            stage.showAndWait();
        } catch (IOException ex) {
            this.displayErrorDialog(ex);
        }
    }

    private void handleAboutMenuAction() {

        try {
            FXMLLoader loader = FXMLLoaderFactory.newInstance();
            Stage stage = newModalWindow(getResourceBundle().getString("main.about.title"), (Parent) loader.load(getClass().getResourceAsStream("/fxml/about_dialog.fxml")));
            DialogController controller = (DialogController) loader.getController();
            controller.setStage(stage);
            stage.setResizable(false);
            stage.setFullScreen(false);
            stage.showAndWait();
        } catch (IOException ex) {
            this.displayErrorDialog(ex);
        }
    }

    private void handleNewFileMenuAction() {
        try {
            // Open dialog
            FXMLLoader loader = FXMLLoaderFactory.newInstance();
            Stage stage = newModalWindow(getResourceBundle().getString("main.new_file.title"), (Parent) loader.load(getClass().getResourceAsStream("/fxml/new_file_dialog.fxml")));
            DialogController controller = (DialogController) loader.getController();
            controller.setStage(stage);
            controller.setUserData(projectNavigatorController.getSelectedArtifact());
            stage.setResizable(false);
            stage.setFullScreen(false);
            stage.showAndWait();

        } catch (IOException ex) {
            this.displayErrorDialog(ex);
        }
    }

    private void handleRenameArtifactMenuAction() {
        // For the moment the action is not bound to any menu item
        // But in the future maybe...

        try {
            // Open dialog
            FXMLLoader loader = FXMLLoaderFactory.newInstance();
            Stage stage = newModalWindow(getResourceBundle().getString("main.rename.title"), (Parent) loader.load(getClass().getResourceAsStream("/fxml/rename_dialog.fxml")));
            DialogController controller = (DialogController) loader.getController();
            controller.setStage(stage);
            controller.setUserData(projectNavigatorController.getSelectedArtifact());
            stage.setResizable(false);
            stage.setFullScreen(false);
            stage.showAndWait();

        } catch (IOException ex) {
            this.displayErrorDialog(ex);
        }

    }

    private void handleDeleteArtifactMenuAction() {
        // For the moment the action is not bound to any menu item
        // But in the future maybe...

        final Artifact artifact = projectNavigatorController.getSelectedArtifact();

        if (artifact != null) {

            // Async deletion task
            final Task asyncArtifactDelete = new Task() {
                @Override
                protected Object call() throws Exception {

                    updateTitle(String.format(getResourceBundle().getString("task.deleting_artifact"), artifact.getName()));

                    artifact.delete();

                    // If project : remove from workspace as well
                    if (artifact instanceof Project) {
                        Project project = (Project) artifact;
                        ApplicationContext.getWorkspace().closeProject(project);
                    }

                    updateProgress(1, 1);

                    return true;
                }
            };

            // Display confirm dialog
            String message = "";
            if (artifact instanceof Project) {
                message = String.format(getResourceBundle().getString("dialog.confirm.delete_project.message"), artifact.getName());
            } else if (artifact instanceof Folder) {
                message = String.format(getResourceBundle().getString("dialog.confirm.delete_folder.message"), artifact.getName());
            } else {
                message = String.format(getResourceBundle().getString("dialog.confirm.delete_file.message"), artifact.getName());
            }
            displayConfirmDialog(getResourceBundle().getString("dialog.confirm.delete_artifact.title"), message, (ConfirmDialogController.ChoiceEvent t) -> {
                if (t.getChoice() == ConfirmDialogController.Choice.POSITIVE) {
                    // Trigger close
                    ApplicationContext.getTaskManager().addTask(asyncArtifactDelete);
                }
            });
        }
    }

    private void handleImportArtifactsMenuAction() {
        // For the moment the action is not bound to any menu item
        // But in the future maybe...

        final Artifact artifact = projectNavigatorController.getSelectedArtifact();

        if (artifact != null) {

            // Display file chooser
            FileChooser fileChooser = new FileChooser();

            fileChooser.setTitle(this.getResourceBundle().getString("main.import.title"));
            final List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);

            if (selectedFiles != null) {
                ApplicationContext.getTaskManager().addTask(new Task() {

                    @Override
                    protected Object call() throws Exception {

                        int i = 1;
                        for (File file : selectedFiles) {

                            updateTitle(String.format(getResourceBundle().getString("task.importing_artifact"), file.getName()));

                            if (artifact instanceof Folder) {
                                Folder folder = (Folder) artifact;
                                Document newDocument = folder.createDocument(file.getName());
                                newDocument.setContent(FileUtils.getFileContent(file));
                                newDocument.save();
                            }

                            updateProgress(i, selectedFiles.size());

                            i++;
                        }

                        return true;
                    }
                });
            }
        }
    }

    private void handleNewFolderMenuAction() {
        try {
            // Open dialog
            FXMLLoader loader = FXMLLoaderFactory.newInstance();
            Stage stage = newModalWindow(getResourceBundle().getString("main.new_folder.title"), (Parent) loader.load(getClass().getResourceAsStream("/fxml/new_folder_dialog.fxml")));
            DialogController controller = (DialogController) loader.getController();
            controller.setStage(stage);
            controller.setUserData(projectNavigatorController.getSelectedArtifact());
            stage.setResizable(false);
            stage.setFullScreen(false);
            stage.showAndWait();

        } catch (IOException ex) {
            this.displayErrorDialog(ex);
        }

    }

    private void handleOpenProjectMenuAction() {

        // Display directory chooser
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(this.getResourceBundle().getString("main.open_project.title"));
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {

            // Add project into workspace (if worksapce is a local workspace)
            Workspace workspace = ApplicationContext.getWorkspace();
            if (workspace instanceof LocalWorkspace) {
                
                LocalWorkspace localWorkspace = (LocalWorkspace)workspace;

                Project project = new FileSystemProject(selectedDirectory, localWorkspace.getFileSystemArtifactWatcher());

                try {
                    ApplicationContext.getWorkspace().openProject(project);

                } catch (IOException ex) {
                    this.displayErrorDialog(ex);
                }

            }

        }
    }

    private void handleCloseProjectMenuAction() {

        Artifact selectedArtifact = projectNavigatorController.getSelectedArtifact();

        if (selectedArtifact != null && selectedArtifact instanceof Project) {
            final Project project = (Project) selectedArtifact;

            final Task asyncProjectClose = new Task() {
                @Override
                protected Object call() throws Exception {
                    if (project != null) {

                        updateTitle(String.format(getResourceBundle().getString("task.closing_project"), project.getName()));
                        ApplicationContext.getWorkspace().closeProject(project);
                        updateProgress(1, 1);

                    }
                    return true;
                }
            };

            // Check unsaved files of the project
            if (project != null && project.isModified()) {
                displayConfirmDialog(getResourceBundle().getString("dialog.confirm.close_project.unsaved.title"), getResourceBundle().getString("dialog.confirm.file.close_project.unsaved.text"), (ConfirmDialogController.ChoiceEvent t) -> {
                    if (t.getChoice() == ConfirmDialogController.Choice.POSITIVE) {
                        // Trigger close
                        ApplicationContext.getTaskManager().addTask(asyncProjectClose);
                    }
                });
            } else {
                // No changes : triggt close without prompting
                ApplicationContext.getTaskManager().addTask(asyncProjectClose);
            }

        }
    }

    @Override
    public void saveState(Map<String, Object> state) throws StateException {
        super.saveState(state);

        // Project navigator
        Map<String, Object> projectNavigatorState = new HashMap<>();
        projectNavigatorController.saveState(projectNavigatorState);
        state.put("projectNavigator", projectNavigatorState);

        // Editors pane
        Map<String, Object> editorsPaneState = new HashMap<>();
        editorsPaneController.saveState(editorsPaneState);
        state.put("editorsPane", editorsPaneState);
    }

    @Override
    public void restoreState(Map<String, Object> state) throws StateException {
        super.restoreState(state);

        // Project navigator
        Map<String, Object> projectNavigatorState = (Map<String, Object>) state.get("projectNavigator");
        projectNavigatorController.restoreState(projectNavigatorState);

        // Editors pane
        Map<String, Object> editorsPaneState = (Map<String, Object>) state.get("editorsPane");
        editorsPaneController.restoreState(editorsPaneState);

    }

}
