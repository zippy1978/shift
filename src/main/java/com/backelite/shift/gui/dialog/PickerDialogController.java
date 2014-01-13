package com.backelite.shift.gui.dialog;

/*
 * #%L
 * PickerDialogController.java - Shift - 2013
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
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class PickerDialogController extends AbstractDialogController {

    private EventHandler<SelectionEvent> onSelection;
    
    @FXML
    private Label messageLabel;
    
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    
    @FXML
    private ChoiceBox selectorChoice;
    
    private EventHandler<ActionEvent> okButtonActionEventHandler;
    private EventHandler<ActionEvent> cancelButtonActionEventHandler;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        
         // Handle ok button click
        okButtonActionEventHandler = new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                if (onSelection != null) {
                    onSelection.handle(new SelectionEvent(EventType.ROOT, selectorChoice.getSelectionModel().getSelectedIndex()));
                }
                
                close();
            }
        };
        okButton.setOnAction(new WeakEventHandler<>(okButtonActionEventHandler));
        
        // Handle cancel button click
        cancelButtonActionEventHandler = new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                if (onSelection != null) {
                    onSelection.handle(new SelectionEvent(EventType.ROOT, -1));
                }
                
                close();
            }
        };
        cancelButton.setOnAction(new WeakEventHandler<>(cancelButtonActionEventHandler));
        
    }
 
    /**
     * @return the onSelection
     */
    public EventHandler<SelectionEvent> getOnSelection() {
        return onSelection;
    }
    
    public void setMessage(String message) {
        messageLabel.setText(message);
    }
     
    public void setOptions(List<String> options) {
         
        selectorChoice.getItems().clear();
        selectorChoice.getItems().addAll(options);
        
        // Select first item
        if (options.size() > 0) {
            selectorChoice.getSelectionModel().select(0);
        }
    }

    /**
     * @param onSelection the onSelection to set
     */
    public void setOnSelection(EventHandler<SelectionEvent> onSelection) {
        this.onSelection = onSelection;
    }

    public class SelectionEvent extends Event {

        private int position;
        
        protected SelectionEvent(EventType<? extends Event> et) {
            super(et);
        }

        protected SelectionEvent(EventType<? extends Event> et, int position) {
            super(et);
            this.position = position;
        }

        /**
         * @return the position
         */
        public int getPosition() {
            return position;
        }

    }
    
}
