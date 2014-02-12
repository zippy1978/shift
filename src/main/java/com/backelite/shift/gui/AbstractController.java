package com.backelite.shift.gui;

/*
 * #%L
 * AbstractController.java - shift - 2013
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
import com.backelite.shift.gui.dialog.ConfirmDialogController;
import com.backelite.shift.gui.dialog.ErrorDialogController;
import com.backelite.shift.gui.dialog.InfoDialogController;
import com.backelite.shift.gui.dialog.PickerDialogController;
import com.backelite.shift.state.StateException;
import com.backelite.shift.util.PlatformUtils;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public abstract class AbstractController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(AbstractController.class);
    private ResourceBundle resourceBundle;
    private List<Stage> childrenWindows = new ArrayList<>();
    private List<Controller> childrenControllers = new ArrayList<>();
    private Controller parentController;
    private EventHandler<WindowEvent> closeChildrenWindowEventHandler;
    private EventHandler<WindowEvent> shownChildrenWindowEventHandler;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        resourceBundle = rb;

        // Children windows listeners
        closeChildrenWindowEventHandler = new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
        
                Stage stage = (Stage) t.getSource();
                onChildWindowRemoved(stage);
            }
        };
        shownChildrenWindowEventHandler = new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                
                Stage stage = (Stage) t.getSource();
                onChildWindowAdded(stage);
            }
        };
    }

    @Override
    public List<Controller> getChildrenControllers() {
        return childrenControllers;
    }

    @Override
    public void setParentController(Controller controller) {

        if (controller != null) {
            controller.getChildrenControllers().add(this);
        } else if (this.parentController != null) {
            this.parentController.getChildrenControllers().remove(this);
        }

        this.parentController = controller;
    }

    @Override
    public Controller getParentController() {
        return parentController;
    }

    /**
     * Test if the target platform supports application wide menu bar.
     *
     * @return true or false
     */
    protected boolean supportsApplicationWideMenu() {

        // Only Mac OS supports menu outside window
        return PlatformUtils.isMacOSX();
    }

    public void displayInfoDialog(String title, String message) {

        try {
            FXMLLoader loader = FXMLLoaderFactory.newInstance();
            if (title == null) {
                title = getResourceBundle().getString("dialog.info.default.title");
            }
            Stage stage = newModalWindow(title, (Parent) loader.load(getClass().getResourceAsStream("/fxml/info_dialog.fxml")));
            InfoDialogController controller = (InfoDialogController) loader.getController();
            controller.setStage(stage);
            controller.setMessage(message);
            stage.setResizable(false);
            stage.setFullScreen(false);
            stage.showAndWait();
        } catch (IOException ex) {
            this.displayErrorDialog(ex);
        }
    }

    public void displayErrorDialog(String title, String message, Throwable e) {

        log.error(title, e);

        try {
            FXMLLoader loader = FXMLLoaderFactory.newInstance();
            if (title == null) {
                title = getResourceBundle().getString("dialog.error.default.title");
            }
            Stage stage = newModalWindow(title, (Parent) loader.load(getClass().getResourceAsStream("/fxml/error_dialog.fxml")));
            ErrorDialogController controller = (ErrorDialogController) loader.getController();
            controller.setStage(stage);
            controller.setMessage(message);
            stage.setResizable(false);
            stage.setFullScreen(false);
            stage.showAndWait();
        } catch (IOException ex) {
            this.displayErrorDialog(ex);
        }
    }

    public void displayErrorDialog(Throwable e) {

        this.displayErrorDialog(null, e.getMessage(), e);
    }

    public void displayPickerDialog(String title, String message, List<String> options, EventHandler<PickerDialogController.SelectionEvent> onSelection) {
        try {
            FXMLLoader loader = FXMLLoaderFactory.newInstance();
            Stage stage = newModalWindow(title, (Parent) loader.load(getClass().getResourceAsStream("/fxml/picker_dialog.fxml")));
            PickerDialogController controller = (PickerDialogController) loader.getController();
            controller.setStage(stage);
            controller.setOnSelection(onSelection);
            controller.setOptions(options);
            controller.setMessage(message);
            stage.setResizable(false);
            stage.setFullScreen(false);
            stage.showAndWait();
        } catch (IOException ex) {
            this.displayErrorDialog(ex);
        }
    }

    public void displayConfirmDialog(String title, String message, EventHandler<ConfirmDialogController.ChoiceEvent> onChoice) {
        this.displayConfirmDialog(title, message, null, null, onChoice);
    }

    public void displayConfirmDialog(String title, String message, String positiveText, String negativeText, EventHandler<ConfirmDialogController.ChoiceEvent> onChoice) {
        try {
            FXMLLoader loader = FXMLLoaderFactory.newInstance();
            Stage stage = newModalWindow(title, (Parent) loader.load(getClass().getResourceAsStream("/fxml/confirm_dialog.fxml")));
            ConfirmDialogController controller = (ConfirmDialogController) loader.getController();
            controller.setStage(stage);
            controller.setOnChoice(onChoice);
            if (positiveText != null) {
                controller.setPositiveButtonText(positiveText);
            }
            if (negativeText != null) {
                controller.setNegativeButtonText(negativeText);
            }
            controller.setMessage(message);
            stage.setResizable(false);
            stage.setFullScreen(false);
            stage.showAndWait();
        } catch (IOException ex) {
            this.displayErrorDialog(ex);
        }
    }

    protected Object loadFXML(String path) {
        try {
            return FXMLLoaderFactory.newInstance().load(getClass().getResourceAsStream(path));
        } catch (IOException ex) {
            this.displayErrorDialog(ex);
        }

        return null;
    }

    public void onChildWindowRemoved(Stage stage) {
        this.childrenWindows.remove(stage);

    }

    public void onChildWindowAdded(final Stage stage) {
        this.childrenWindows.add(stage);

        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                stage.toFront();
            }
        });
    }

    @Override
    public void close() {

        // Close children
        List<Controller> controllerstoRemove = new ArrayList<>(childrenControllers);
        for (Controller childController : controllerstoRemove) {
            childController.close();
        }
        childrenControllers.removeAll(childrenWindows);


        // Remove parent controller
        this.setParentController(null);
    }

    /**
     * Create new window.
     *
     * @param title Window title
     * @param rootNode Window content
     * @param style Window style
     * @param alwaysOnTop If true window is displayed always on top of the main
     * newStage
     * @return Window created (Stage)
     */
    public Stage newWindow(String title, Parent rootNode, StageStyle style, boolean alwaysOnTop) {

        final Stage newStage = new Stage();
        newStage.initStyle(style);
        Scene scene = new Scene(rootNode);
        scene.getStylesheets().add(ApplicationContext.getThemeManager().getCSS());
        newStage.setScene(scene);
        newStage.setTitle(title);

        if (alwaysOnTop) {
            newStage.initOwner(ApplicationContext.getMainStage());
        }

        // Register listeners
        newStage.setOnCloseRequest(new WeakEventHandler<>(closeChildrenWindowEventHandler));
        newStage.setOnShown(new WeakEventHandler<>(shownChildrenWindowEventHandler));
        newStage.setOnHiding(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                newStage.getOnCloseRequest().handle(t);

            }
        });
        
        return newStage;
    }

    /**
     * Create new utility window.
     *
     * @param title Window title
     * @param rootNode Window content
     * @return Window created (Stage)
     */
    public Stage newUtilityWindow(String title, Parent rootNode) {

        return this.newWindow(title, rootNode, StageStyle.UTILITY, false);
    }

    /**
     * Create new basic window.
     *
     * @param title Window title
     * @param rootNode Window content
     * @return Window created (Stage)
     */
    public Stage newDecoratedWindow(String title, Parent rootNode) {

        return this.newDecoratedWindow(title, rootNode, false);
    }

    /**
     * Create new basic window.
     *
     * @param title Window title
     * @param rootNode Window content
     * @param alwaysOnTop If true window is displayed always on top of the main
     * newStage
     * @return Window created (Stage)
     */
    public Stage newDecoratedWindow(String title, Parent rootNode, boolean alwaysOnTop) {

        return this.newWindow(title, rootNode, StageStyle.DECORATED, alwaysOnTop);
    }

    /**
     * Create new modal window
     *
     * @param title Window title
     * @param rootNode Window content
     * @return Window created (Stage)
     */
    public Stage newModalWindow(String title, Parent rootNode) {

        Stage stage = new Stage();
        if (!PlatformUtils.isMacOSX()) {
            stage.initModality(Modality.APPLICATION_MODAL);
        }
        stage.initOwner(ApplicationContext.getMainStage());
        Scene scene = new Scene(rootNode);
        scene.getStylesheets().add(ApplicationContext.getThemeManager().getCSS());
        stage.setScene(scene);
        stage.setTitle(title);

        return stage;
    }

    /**
     * @return the resourceBundle
     */
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    @Override
    public void saveState(Map<String, Object> state) throws StateException {
    }

    @Override
    public void restoreState(Map<String, Object> state) throws StateException {
    }

    @Override
    public String getInstanceIdentifier() {
        return null;
    }

    /**
     * Return list of children windows.
     */
    public List<Stage> getChildrenWindows() {

        return this.childrenWindows;
    }
}
