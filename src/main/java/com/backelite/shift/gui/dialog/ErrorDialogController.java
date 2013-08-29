package com.backelite.shift.gui.dialog;

/*
 * #%L
 * ErrorDialogController.java - shift - 2013
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

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Error dialog.
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class ErrorDialogController extends AbstractDialogController {

    @FXML
    private Label messageLabel;
    @FXML
    private Button closeButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        
        // Close dialog on close button click
        closeButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent t) {
                close();
            }
        });
    }
    
    
    
    public void setMessage(String message) {
        this.messageLabel.setText(message);
    }
}
