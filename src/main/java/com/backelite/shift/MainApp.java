package com.backelite.shift;

/*
 * #%L
 * MainApp.java - shift - Gilles Grousset - 2013
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
import com.backelite.shift.gui.FXMLLoaderFactory;
import com.backelite.shift.gui.MainController;
import com.backelite.shift.gui.dialog.ConfirmDialogController;
import com.backelite.shift.preferences.PreferencesManager;
import com.sun.javafx.runtime.VersionInfo;
import java.util.HashMap;
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
        log.debug(String.format("File encoding is %s", System.getProperty("file.encoding")));

        launch(args);
    }

    @Override
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
        Font.loadFont(getClass().getResource("/fonts/SourceCodePro-Regular.ttf").toExternalForm(), 12);
  
        // Load root
        FXMLLoader loader = FXMLLoaderFactory.newInstance();
        Parent rootNode = (Parent) loader.load(getClass().getResourceAsStream("/fxml/main.fxml"));
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
        ApplicationContext.setHostServices(this.getHostServices());
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
    private void registerCloseRequestHandler(final Stage stage) {

        stage.setOnCloseRequest((final WindowEvent we) -> {
            if (ApplicationContext.getWorkspace().isModified()) {
                mainController.displayConfirmDialog(mainController.getResourceBundle().getString("dialog.confirm.close_app.unsaved.title"), mainController.getResourceBundle().getString("dialog.confirm.close_app.unsaved.text"), (ConfirmDialogController.ChoiceEvent t) -> {
                    if (t.getChoice() == ConfirmDialogController.Choice.NEGATIVE) {
                        // Consume event to stop propagation and prevent close
                        we.consume();
                    } else {
                        
                        mainController.close();
                        stage.setOnCloseRequest(null);
                        stage.setOnHiding(null);
                    }
                });
            } else {
                mainController.close();
            }
        });
        
        stage.setOnHiding((WindowEvent t) -> {
            stage.getOnCloseRequest().handle(t);
        });
       
    }

    /**
     * Initialize preferences.
     */
    private void initializePreferences() {
        try {
            PreferencesManager preferencesManager = ApplicationContext.getPreferencesManager();
            Map<String, String> shortcuts = new HashMap<>();
            shortcuts.put(Constants.SHORTCUT_NEW_FILE, new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN).getName());
            shortcuts.put(Constants.SHORTCUT_OPEN_PROJECT, new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN).getName());
            shortcuts.put(Constants.SHORTCUT_SAVE, new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN).getName());
            shortcuts.put(Constants.SHORTCUT_UNDO, new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN).getName());
            shortcuts.put(Constants.SHORTCUT_REDO, new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN).getName());
            shortcuts.put(Constants.SHORTCUT_CUT, new KeyCodeCombination(KeyCode.X, KeyCombination.SHORTCUT_DOWN).getName());
            shortcuts.put(Constants.SHORTCUT_COPY, new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN).getName());
            shortcuts.put(Constants.SHORTCUT_PASTE, new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN).getName());
            shortcuts.put(Constants.SHORTCUT_SELECT_ALL, new KeyCodeCombination(KeyCode.A, KeyCombination.SHORTCUT_DOWN).getName());
            shortcuts.put(Constants.SHORTCUT_FIND, new KeyCodeCombination(KeyCode.F, KeyCombination.SHORTCUT_DOWN).getName());
            shortcuts.put(Constants.SHORTCUT_FIND_NEXT, new KeyCodeCombination(KeyCode.G, KeyCombination.SHORTCUT_DOWN).getName());
            shortcuts.put(Constants.SHORTCUT_FIND_PREVIOUS, new KeyCodeCombination(KeyCode.G, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN).getName());
            shortcuts.put(Constants.SHORTCUT_REPLACE, new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN).getName());
            shortcuts.put(Constants.SHORTCUT_REPLACE_ALL, new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN).getName());
            shortcuts.put(Constants.SHORTCUT_CONTENT_ASSIST, new KeyCodeCombination(KeyCode.SPACE, KeyCombination.CONTROL_DOWN).getName());
            shortcuts.put(Constants.SHORTCUT_NEW_PREVIEW, new KeyCodeCombination(KeyCode.P, KeyCombination.SHORTCUT_DOWN).getName());
            preferencesManager.setInitialValue(Constants.PREFERENCES_KEY_SHORTCUTS, shortcuts);
            preferencesManager.commit();
        } catch (Exception e) {
            log.error("Failed to initialize preferences");
        }
    }
}
