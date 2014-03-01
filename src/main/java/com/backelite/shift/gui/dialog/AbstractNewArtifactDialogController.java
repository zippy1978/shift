package com.backelite.shift.gui.dialog;

/*
 * #%L
 * AbstractNewArtifactDialogController.java - shift - 2013
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
        nameChangeListener = (ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            okButton.setDisable(!nameTextField.isValid());
            
            if (nameTextField.isValid()) {
                nameErrorLabel.setVisible(false);
            } else {
                nameErrorLabel.setVisible(true);
                nameErrorLabel.setText(getResourceBundle().getString(nameTextField.getLastValidatorResult().getErrorMessages().get(0)));
            }
        };
        nameTextField.validProperty().addListener(new WeakChangeListener<>(nameChangeListener));

        // Cancel button click
        cancelActionEventHandler = (ActionEvent t) -> {
            handleCancelButtonAction();
        };
        cancelButton.setOnAction(new WeakEventHandler<>(cancelActionEventHandler));
        

        // OK button click
        okActionEventHandler = (ActionEvent t) -> {
            handleOKButtonAction();
        };
        okButton.setOnAction(new WeakEventHandler<>(okActionEventHandler));

    }
    
    

    @Override
    public void setUserData(Object userData) {
        super.setUserData(userData); 
        
        // Set validators on text field
        if (getUserData() != null && getUserData() instanceof Folder) {
            Folder folder = (Folder) getUserData();
            nameTextField.setValidator(new CompoundValidator(new NotBlankValidator(), new FilenameValidator(), new UnusedArtifactNameValidator(folder)));
        }
    }
 

    protected void handleCancelButtonAction() {
        this.close();
    }

    protected abstract void handleOKButtonAction();
}
