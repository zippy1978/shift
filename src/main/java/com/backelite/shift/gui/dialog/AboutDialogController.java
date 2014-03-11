package com.backelite.shift.gui.dialog;

import com.backelite.shift.ApplicationContext;
import com.backelite.shift.Constants;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;

/*
 * #%L
 * AboutDialogController.java - Shift - 2013
 * %%
 * Copyright (C) 2013 - 2014 Gilles Grousset
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
/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class AboutDialogController extends AbstractDialogController {
    
    @FXML
    private WebView webView;
    @FXML
    private Label infoLabel;
    private ChangeListener<String> locationChangeListener;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        
        webView.getEngine().load(getClass().getResource("/webcontent/about.html").toExternalForm());
        webView.setContextMenuEnabled(false);
        
        // Open external links in native browser
        locationChangeListener = (ObservableValue<? extends String> ov, String t, String t1) -> {
            try {
                
                URI address = new URI(ov.getValue());
                if (address.toString().startsWith("http")) {
                    ApplicationContext.getHostServices().showDocument(address.toString());
                    close();
                }
            } catch (URISyntaxException ex) {                
                displayErrorDialog(ex);
            }
        };
        webView.getEngine().locationProperty().addListener(new WeakChangeListener<>(locationChangeListener));

        // Set version info
        String versionName = ApplicationContext.getProperties().getProperty(Constants.PROPERTY_APPLICATION_VERSION_NAME);
        String buildNumber = ApplicationContext.getProperties().getProperty(Constants.PROPERTY_APPLICATION_BUILD_NUMBER);
        infoLabel.setText(String.format(getResourceBundle().getString("welcome.info"), versionName, buildNumber));
    }
}
