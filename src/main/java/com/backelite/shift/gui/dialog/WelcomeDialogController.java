package com.backelite.shift.gui.dialog;

/*
 * #%L
 * WelcomeDialogController.java - Shift - 2013
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
import com.backelite.shift.gui.control.CodeEditor;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class WelcomeDialogController extends AbstractDialogController {

    @FXML
    private WebView webView;
    @FXML
    private Button closeButton;
    @FXML 
    private Label infoLabel;
    private EventHandler<ActionEvent> closeButtonActionEventHandler;
    private ChangeListener<Worker.State> webViewStateChangeListener;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);

        webView.getEngine().load(getClass().getResource("/welcome.html").toExternalForm());

        // Close button click
        closeButtonActionEventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                close();
            }
        };
        closeButton.setOnAction(new WeakEventHandler<>(closeButtonActionEventHandler));


        final String versionName = (String) ApplicationContext.getProperties().get("application.version.name");
        final String buildNumber = (String) ApplicationContext.getProperties().get("application.build.number");
        // Wait for the HTML to load
        webViewStateChangeListener = new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState) {


                if (newState == Worker.State.SUCCEEDED) {

                    // Display warning note if application is SNAPSHOT
                    if (versionName.contains("SNAPSHOT")) {
                        webView.getEngine().executeScript("document.getElementById('snapshot_warning').style.display = 'block'");
                    }
                }
            }
        };
        webView.getEngine().getLoadWorker().stateProperty().addListener(new WeakChangeListener<>(webViewStateChangeListener));

        // Set version info
        infoLabel.setText(String.format(getResourceBundle().getString("welcome.info"), versionName, buildNumber));
    }
}
