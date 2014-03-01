package com.backelite.shift.gui.dialog;

/*
 * #%L
 * WelcomeDialogController.java - Shift - 2013
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
import com.backelite.shift.Constants;
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

        webView.getEngine().load(getClass().getResource("/webcontent/welcome.html").toExternalForm());

        // Close button click
        closeButtonActionEventHandler = (ActionEvent t) -> {
            close();
        };
        closeButton.setOnAction(new WeakEventHandler<>(closeButtonActionEventHandler));


        final String versionName = ApplicationContext.getProperties().getProperty(Constants.PROPERTY_APPLICATION_VERSION_NAME);
        final String buildNumber = ApplicationContext.getProperties().getProperty(Constants.PROPERTY_APPLICATION_BUILD_NUMBER);
        // Wait for the HTML to load
        webViewStateChangeListener = (ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                
                // Display warning note if application is SNAPSHOT
                if (ApplicationContext.isSnapshotRelease()) {
                    webView.getEngine().executeScript("document.getElementById('snapshot_warning').style.display = 'block'");
                }
            }
        };
        webView.getEngine().getLoadWorker().stateProperty().addListener(new WeakChangeListener<>(webViewStateChangeListener));

        // Set version info
        infoLabel.setText(String.format(getResourceBundle().getString("welcome.info"), versionName, buildNumber));
    }
}
