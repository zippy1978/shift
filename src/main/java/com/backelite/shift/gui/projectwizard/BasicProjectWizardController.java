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
import com.backelite.shift.workspace.artifact.Project;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

/**
 * A simple project wizard with project name and location.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class BasicProjectWizardController extends AbstractProjectWizardController {

    @FXML
    private TextField nameTextField;
    @FXML
    private TextField locationTextField;
    @FXML
    private Button browseButton;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);

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

        // TODO : validate fields

        // Create project and import into workspace
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("location", locationTextField.getText());
        Project project = this.getProjectGenerator().generate(nameTextField.getText(), attributes);
        try {
            ApplicationContext.getWorkspace().openProject(project);
        } catch (IOException ex) {
            this.displayErrorDialog(ex);
        }

        // Close Window
        this.close();
    }
}
