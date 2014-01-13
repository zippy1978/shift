package com.backelite.shift.gui.dialog;

/*
 * #%L
 * AbstractNewArtifactDialogController.java - shift - 2013
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

import com.backelite.shift.gui.control.ValidatedTextField;
import com.backelite.shift.gui.validation.CompoundValidator;
import com.backelite.shift.gui.validation.FilenameValidator;
import com.backelite.shift.gui.validation.NotBlankValidator;
import com.backelite.shift.gui.validation.UnusedArtifactNameValidator;
import com.backelite.shift.workspace.artifact.Folder;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public abstract class AbstractNewArtifactDialogController extends AbstractDialogController {

    @FXML
    protected ValidatedTextField nameTextField;
    @FXML
    protected Label nameErrorLabel;
    @FXML
    protected Button okButton;
    @FXML
    protected Button cancelButton;
    
    private ChangeListener<Boolean> nameChangeListener;
    private EventHandler<ActionEvent> cancelActionEventHandler;
    private EventHandler<ActionEvent> okActionEventHandler;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
       
        // Listen to input validity
        okButton.setDisable(!nameTextField.isValid());
        nameChangeListener = new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {

                okButton.setDisable(!nameTextField.isValid());
                
                if (nameTextField.isValid()) {
                    nameErrorLabel.setVisible(false);
                } else {
                    nameErrorLabel.setVisible(true);
                    nameErrorLabel.setText(getResourceBundle().getString(nameTextField.getLastValidatorResult().getErrorMessages().get(0)));
                }

            }
        };
        nameTextField.validProperty().addListener(new WeakChangeListener<>(nameChangeListener));

        // Cancel button click
        cancelActionEventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                handleCancelButtonAction();
            }
        };
        cancelButton.setOnAction(new WeakEventHandler<>(cancelActionEventHandler));
        

        // OK button click
        okActionEventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                handleOKButtonAction();
            }
        };
        okButton.setOnAction(new WeakEventHandler<>(okActionEventHandler));

    }
    
    

    @Override
    public void setUserData(Object userData) {
        super.setUserData(userData); 
        
        // Set validators on text field
        Folder folder = (Folder) getUserData();
        nameTextField.setValidator(new CompoundValidator(new NotBlankValidator(), new FilenameValidator(), new UnusedArtifactNameValidator(folder)));

    }
 

    protected void handleCancelButtonAction() {
        this.close();
    }

    protected abstract void handleOKButtonAction();
}
