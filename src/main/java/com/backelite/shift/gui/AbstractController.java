package com.backelite.shift.gui;

/*
 * #%L
 * AbstractController.java - shift - 2013
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
import com.backelite.shift.gui.dialog.ConfirmDialogController;
import com.backelite.shift.gui.dialog.ErrorDialogController;
import com.backelite.shift.ApplicationContext;
import com.backelite.shift.gui.dialog.InfoDialogController;
import com.backelite.shift.gui.dialog.PickerDialogController;
import com.backelite.shift.state.PersistableState;
import com.backelite.shift.state.StateException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
public abstract class AbstractController implements Controller, Initializable, PersistableState {

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
        return System.getProperty("os.name").toLowerCase().contains("mac");
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
    
    public void onChildWindowAdded(Stage stage) {
        this.childrenWindows.add(stage);
    }

    @Override
    public void close() {
        
        // Remove parent controller
        this.setParentController(null);
    }

    

    /**
     * Create new window.
     *
     * @param title Window title
     * @param rootNode Window content
     * @param style Window style
     * @param alwaysOnTop If true window is displayed always on top of the main newStage
     * @return Window created (Stage)
     */
    public Stage newWindow(String title, Parent rootNode, StageStyle style, boolean alwaysOnTop) {

        Stage newStage = new Stage();
        newStage.initStyle(style);
        Scene scene = new Scene(rootNode);
        scene.getStylesheets().add(ApplicationContext.getThemeManager().getCSS());
        newStage.setScene(scene);
        newStage.setTitle(title);
        
        if (alwaysOnTop) {
            newStage.initOwner(ApplicationContext.getMainStage());
        }
        
        // Register listeners
        closeChildrenWindowEventHandler = new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent t) {
                onChildWindowRemoved((Stage)t.getSource());
            }
        };
        newStage.setOnCloseRequest(new WeakEventHandler<>(closeChildrenWindowEventHandler));
        
        shownChildrenWindowEventHandler = new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent t) {
                onChildWindowAdded((Stage)t.getSource());
            }
        };
        newStage.setOnShown(new WeakEventHandler<>(shownChildrenWindowEventHandler));

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
     * @param alwaysOnTop If true window is displayed always on top of the main newStage
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
        stage.initModality(Modality.APPLICATION_MODAL);
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
