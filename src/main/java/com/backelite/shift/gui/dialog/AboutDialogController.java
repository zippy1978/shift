package com.backelite.shift.gui.dialog;

import com.backelite.shift.ApplicationContext;
import com.backelite.shift.Constants;
import java.net.URI;
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
        
        webView.getEngine().load(getClass().getResource("/about.html").toExternalForm());
        
        // Open external links in native browser
        locationChangeListener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                
                try {
                    
                    URI address = new URI(ov.getValue());
                    if (address.toString().startsWith("http")) {
                        ApplicationContext.getHostServices().showDocument(address.toString());
                        close();
                    }
                } catch (Exception ex) {
                    displayErrorDialog(ex);
                }
                
            }
        };
        webView.getEngine().locationProperty().addListener(new WeakChangeListener<>(locationChangeListener));

        // Set version info
        String versionName = ApplicationContext.getProperties().getProperty(Constants.PROPERTY_APPLICATION_VERSION_NAME);
        String buildNumber = ApplicationContext.getProperties().getProperty(Constants.PROPERTY_APPLICATION_BUILD_NUMBER);
        infoLabel.setText(String.format(getResourceBundle().getString("welcome.info"), versionName, buildNumber));
    }
}
