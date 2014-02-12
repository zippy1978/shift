package com.backelite.shift.gui.projectwizard;

/*
 * #%L
 * BasicProjectWizardController.java - shift - 2013
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
import com.backelite.shift.gui.control.ValidatedTextField;
import com.backelite.shift.gui.validation.CompoundValidator;
import com.backelite.shift.gui.validation.FilenameValidator;
import com.backelite.shift.gui.validation.NotBlankValidator;
import com.backelite.shift.workspace.artifact.Project;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
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
            @Override
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
            @Override
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
            @Override
            public void handle(ActionEvent t) {
                handleBrowseButtonAction();
            }
        });

        // Cancel button click
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                handleCancelButtonAction();
            }
        });
        
        // OK button click
        okButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
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
        File selectedDirectory = directoryChooser.showDialog(this.getStage());

        if (selectedDirectory != null) {
            locationTextField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    protected void handleOKButtonAction() {
        
        // Create project and import into workspace
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put("location", locationTextField.getText());
        ApplicationContext.getTaskManager().addTask(new Task() {

            @Override
            protected Object call() throws Exception {
                
                updateTitle(String.format(getResourceBundle().getString("task.generating_project"), nameTextField.getText()));
                Project project = getProjectGenerator().generate(nameTextField.getText(), attributes);
                ApplicationContext.getWorkspace().openProject(project);
                updateProgress(1, 1);
                
                return true;
            }
            
            
        });
        
        // Close Window
        this.close();
    }
}
