package com.backelite.shift.gui.statusbar;

/*
 * #%L
 * StatusBarController.java - shift - 2013
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
import com.backelite.shift.gui.AbstractController;
import com.backelite.shift.gui.editor.CursorPosition;
import com.backelite.shift.task.TaskManagerListener;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TimelineBuilder;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class StatusBarController extends AbstractController implements TaskManagerListener {

    @FXML
    private HBox taskBox;
    @FXML
    private Label taskTitleLabel;
    @FXML
    private ProgressBar taskProgressBar;
    @FXML
    private Label cursorPositionLabel;
    
    private double taskBoxInitialWidth = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);

        // Register as task manager listener
        ApplicationContext.getTaskManager().addListener(this);

        taskBox.setVisible(false);
        cursorPositionLabel.setVisible(false);
    }

    @Override
    public void onTaskStarted(Task task) {
        
        // Store initial width
        if (taskBoxInitialWidth == 0) {
            taskBoxInitialWidth = taskBox.getWidth();
        }
        
        taskBox.setPrefWidth(0);
        
        this.taskBoxSlideInAnimation();
        
        taskProgressBar.progressProperty().bind(task.progressProperty());
        taskTitleLabel.textProperty().bind(task.titleProperty());
    }

    @Override
    public void onTaskFailed(Task task) {
        this.taskBoxSlideOutAnimation();
        this.displayErrorDialog(task.getException());
    }

    @Override
    public void onTaskSucceeded(Task task) {
        this.taskBoxSlideOutAnimation();
    }

    public void setCursorPosition(CursorPosition cursorPosition) {

        if (cursorPosition != null) {
            cursorPositionLabel.setVisible(true);
            cursorPositionLabel.setText(String.format("%d | %d", cursorPosition.getLine(), cursorPosition.getCh()));
        } else {
            cursorPositionLabel.setVisible(false);
        }
    }
    
    private void taskBoxSlideInAnimation() {
        
        TimelineBuilder.create().keyFrames(
                new KeyFrame(
                Duration.millis(500),
                new KeyValue(taskBox.prefWidthProperty(), taskBoxInitialWidth)))
                .build().play();


        taskBox.setVisible(true);
    }
    
    private void taskBoxSlideOutAnimation() {
        
        Timeline timeline = TimelineBuilder.create().keyFrames(
                new KeyFrame(
                Duration.millis(500),
                new KeyValue(taskBox.prefWidthProperty(), 0)))
                .build();
        
        timeline.setOnFinished((ActionEvent t) -> {
            taskBox.setVisible(false);
        });
        
        timeline.play();
    }
}
