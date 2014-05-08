package org.shiftedit.gui.dialog;

/*
 * #%L
 * ConfirmDialogController.java - shift - 2013
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
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.event.WeakEventHandler;
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
    private EventHandler<ActionEvent> positiveButtonActionEventHandler;
    private EventHandler<ActionEvent> negativeButtonActionEventHandler;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        
        // Handle positive button click
        positiveButtonActionEventHandler = (ActionEvent t) -> {
            if (onChoice != null) {
                onChoice.handle(new ChoiceEvent(EventType.ROOT, Choice.POSITIVE));
            }
            
            close();
        };
        positiveButton.setOnAction(new WeakEventHandler<>(positiveButtonActionEventHandler));
        
        // Handle negative button click
        negativeButtonActionEventHandler = (ActionEvent t) -> {
            if (onChoice != null) {
                onChoice.handle(new ChoiceEvent(EventType.ROOT, Choice.NEGATIVE));
            }
            
            close();
        };
        negativeButton.setOnAction(new WeakEventHandler<>(negativeButtonActionEventHandler));
        
        // Default focus on negative button
        Platform.runLater(() -> {
            negativeButton.requestFocus();
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
