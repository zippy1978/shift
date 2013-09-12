package com.backelite.shift;

/*
 * #%L
 * MainApp.java - shift - Gilles Grousset - 2013
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
import com.sun.javafx.runtime.VersionInfo;
import com.backelite.shift.gui.dialog.ConfirmDialogController;
import com.backelite.shift.gui.FXMLLoaderFactory;
import com.backelite.shift.gui.MainController;
import com.backelite.shift.preferences.PreferencesManager;
import com.backelite.shift.workspace.artifact.Project;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainApp extends Application {

    private static final Logger log = LoggerFactory.getLogger(MainApp.class);
    private MainController mainController;

    public static void main(String[] args) throws Exception {

        log.debug(String.format("JavaFX version: %s", VersionInfo.getVersion()));
        log.debug(String.format("JavaFX runtime version: %s", VersionInfo.getRuntimeVersion()));
        log.debug(String.format("JavaFX release milestone: %s", VersionInfo.getReleaseMilestone()));

        launch(args);
    }

    public void start(Stage stage) throws Exception {

        // *** This section should be asynced and moved to splash screen
        // Initialize preferences
        this.initializePreferences();
        // Load plugins
        ApplicationContext.getPluginRegistry().loadPlugins();
        // Restore workspace state
        ApplicationContext.getStateManager().restore(ApplicationContext.getWorkspace());
        // ***

        // Load custom fonts
        Font.loadFont(getClass().getResource("/fonts/SourceCodePro-Medium.ttf").toExternalForm(), 12);

        // Load root
        FXMLLoader loader = FXMLLoaderFactory.newInstance();
        Parent rootNode = null;
        rootNode = (Parent) loader.load(getClass().getResourceAsStream("/fxml/main.fxml"));
        mainController = (MainController) loader.getController();

        // Restore state
        ApplicationContext.getStateManager().restore(mainController);


        // Set scene
        Scene scene = new Scene(rootNode, 800, 600);
        scene.getStylesheets().add(ApplicationContext.getThemeManager().getCSS());

        stage.setTitle(ApplicationContext.getProperties().getProperty("application.name"));
        stage.setScene(scene);
        stage.show();

        // Register close request handler
        this.registerCloseRequestHandler(stage);

        ApplicationContext.setMainStage(stage);
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        // Save workspace state
        ApplicationContext.getStateManager().save(ApplicationContext.getWorkspace());

        // Save main controller state
        ApplicationContext.getStateManager().save(mainController);

        // Destroy application context
        ApplicationContext.destroy();
    }

    /**
     * Register close request handler.
     *
     * @param stage Main stage
     */
    private void registerCloseRequestHandler(Stage stage) {

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(final WindowEvent we) {

                boolean unsavedFiles = false;
                List<Project> projects = ApplicationContext.getWorkspace().getProjects();
                for (Project project : projects) {
                    if (project.isModified()) {
                        unsavedFiles = true;
                        break;
                    }
                }

                if (unsavedFiles) {
                    mainController.displayConfirmDialog(mainController.getResourceBundle().getString("dialog.confirm.close_app.unsaved.title"), mainController.getResourceBundle().getString("dialog.confirm.close_app.unsaved.text"), new EventHandler<ConfirmDialogController.ChoiceEvent>() {
                        public void handle(ConfirmDialogController.ChoiceEvent t) {

                            if (t.getChoice() == ConfirmDialogController.Choice.NEGATIVE) {
                                // Consume event to stop propagation and prevent close
                                we.consume();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * Initialize preferences.
     */
    private void initializePreferences() {

        try {
            PreferencesManager preferencesManager = ApplicationContext.getPreferencesManager();
            Map<String, String> shortcuts = new HashMap<String, String>();
            shortcuts.put(Constants.SHORTCUT_NEW_FILE, new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN).getName());
            shortcuts.put(Constants.SHORTCUT_OPEN_PROJECT, new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN).getName());
            shortcuts.put(Constants.SHORTCUT_SAVE, new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN).getName());
            shortcuts.put(Constants.SHORTCUT_UNDO, new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN).getName());
            shortcuts.put(Constants.SHORTCUT_REDO, new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN).getName());
            shortcuts.put(Constants.SHORTCUT_SELECT_ALL, new KeyCodeCombination(KeyCode.A, KeyCombination.SHORTCUT_DOWN).getName());
            shortcuts.put(Constants.SHORTCUT_NEW_PREVIEW, new KeyCodeCombination(KeyCode.P, KeyCombination.SHORTCUT_DOWN).getName());
            preferencesManager.setInitialValue(Constants.PREFERENCES_KEY_SHORTCUTS, shortcuts);
            preferencesManager.commit();
        } catch (Exception e) {
            log.error("Failed to initialize preferences");
        }
    }
}
