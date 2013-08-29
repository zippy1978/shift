package com.backelite.shift.gui.dialog;

/*
 * #%L
 * ConfirmDialogController.java - shift - 2013
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
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class ConfirmDialogController extends AbstractDialogController {

    @FXML
    private Label messageLabel;
    @FXML
    private Button positiveButton;
    @FXML
    private Button negativeButton;
    
    private EventHandler<ChoiceEvent> onChoice;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        
        // Handle positive button click
        positiveButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent t) {
                if (onChoice != null) {
                    onChoice.handle(new ChoiceEvent(new EventType<ChoiceEvent>(), Choice.POSITIVE));
                }
                
                close();
            }
        });
        
        // Handle negative button click
        negativeButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent t) {
                if (onChoice != null) {
                    onChoice.handle(new ChoiceEvent(new EventType<ChoiceEvent>(), Choice.NEGATIVE));
                }
                
                close();
            }
        });
        
        // Default focus on negative button
        Platform.runLater(new Runnable() {
        @Override
        public void run() {
            negativeButton.requestFocus();
        }
    });
    }
    
    public void setPositiveButtonText(String text) {
        positiveButton.setText(text);
    }
    
    public void setNegativeButtonText(String text) {
        negativeButton.setText(text);
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }
    /**
     * @return the onChoice
     */
    public EventHandler<ChoiceEvent> getOnChoice() {
        return onChoice;
    }

    /**
     * @param onChoice the onChoice to set
     */
    public void setOnChoice(EventHandler<ChoiceEvent> onChoice) {
        this.onChoice = onChoice;
    }
    
    public enum Choice {
        POSITIVE,
        NEGATIVE
    }

    public class ChoiceEvent extends Event {

        private Choice choice;
        
        protected ChoiceEvent(EventType<? extends Event> et) {
            super(et);
        }

        protected ChoiceEvent(EventType<? extends Event> et, Choice choice) {
            super(et);
            this.choice = choice;
        }

        /**
         * @return the choice
         */
        public Choice getChoice() {
            return choice;
        }
    }
}
