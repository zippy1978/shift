package com.backelite.shift.gui.projectwizard;

/*
 * #%L
 * BasicProjectWizardController.java - shift - 2013
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
import com.backelite.shift.gui.control.ValidatedTextField;
import com.backelite.shift.gui.validation.CompoundValidator;
import com.backelite.shift.gui.validation.FilenameValidator;
import com.backelite.shift.gui.validation.NotBlankValidator;
import com.backelite.shift.workspace.artifact.Project;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;

/**
 * A simple project wizard with project name and location.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class BasicProjectWizardController extends AbstractProjectWizardController {

    @FXML
    protected ValidatedTextField nameTextField;
    @FXML
    protected ValidatedTextField locationTextField;
    @FXML
    protected Button browseButton;
    @FXML
    protected Button okButton;
    @FXML
    protected Button cancelButton;
    @FXML
    protected Label nameErrorLabel;
    @FXML
    protected Label locationErrorLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        
        // Set validators on text field
        nameTextField.setValidator(new CompoundValidator(new NotBlankValidator(), new FilenameValidator()));
        locationTextField.setValidator(new NotBlankValidator());
        
        // Listen to input validity
        okButton.setDisable(!nameTextField.isValid() || !locationTextField.isValid());
        
        nameErrorLabel.setVisible(false);
        nameTextField.validProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {

                okButton.setDisable(!nameTextField.isValid() || !locationTextField.isValid());
                
                if (nameTextField.isValid()) {
                    nameErrorLabel.setVisible(false);
                } else {
                    nameErrorLabel.setVisible(true);
                    nameErrorLabel.setText(getResourceBundle().getString(nameTextField.getLastValidatorResult().getErrorMessages().get(0)));
                }

            }
        });
        
        locationErrorLabel.setVisible(false);
        locationTextField.validProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {

                okButton.setDisable(!nameTextField.isValid() || !locationTextField.isValid());
                
                if (locationTextField.isValid()) {
                    locationErrorLabel.setVisible(false);
                } else {
                    locationErrorLabel.setVisible(true);
                    locationErrorLabel.setText(getResourceBundle().getString(locationTextField.getLastValidatorResult().getErrorMessages().get(0)));
                }

            }
        });

        // Browse button click
        browseButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                handleBrowseButtonAction();
            }
        });

        // Cancel button click
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                handleCancelButtonAction();
            }
        });
        
        // OK button click
        okButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent t) {
                handleOKButtonAction();
            }
        });
       
    }
    
    private void handleCancelButtonAction() {
        this.close();
    }

    private void handleBrowseButtonAction() {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(this.getResourceBundle().getString("project_wizard.browse.title"));
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            locationTextField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    private void handleOKButtonAction() {
        
        // Create project and import into workspace
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("location", locationTextField.getText());
        ApplicationContext.getTaskManager().addTask(new Task() {

            @Override
            protected Object call() throws Exception {
                Project project = getProjectGenerator().generate(nameTextField.getText(), attributes);
                ApplicationContext.getWorkspace().openProject(project);
                
                return true;
            }
            
            
        });
        
        // Close Window
        this.close();
    }
}
