package com.backelite.shift.gui.statusbar;

/*
 * #%L
 * StatusBarController.java - shift - 2013
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
        
        timeline.setOnFinished(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                taskBox.setVisible(false);
            }
        });
        
        timeline.play();
    }
}
