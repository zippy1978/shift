package com.backelite.shift.gui.dialog;

/*
 * #%L
 * PickerDialogController.java - Shift - 2013
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
        okButtonActionEventHandler = (ActionEvent t) -> {
            if (onSelection != null) {
                onSelection.handle(new SelectionEvent(EventType.ROOT, selectorChoice.getSelectionModel().getSelectedIndex()));
            }
            
            close();
        };
        okButton.setOnAction(new WeakEventHandler<>(okButtonActionEventHandler));
        
        // Handle cancel button click
        cancelButtonActionEventHandler = (ActionEvent t) -> {
            if (onSelection != null) {
                onSelection.handle(new SelectionEvent(EventType.ROOT, -1));
            }
            
            close();
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
