package org.shiftedit.gui.dialog;

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
import org.shiftedit.ApplicationContext;
import org.shiftedit.Constants;
import java.net.URL;
import java.util.ResourceBundle;
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
    
    private String page;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);

        webView.setContextMenuEnabled(false);

        // Close button click
        closeButtonActionEventHandler = (ActionEvent t) -> {
            close();
        };
        closeButton.setOnAction(new WeakEventHandler<>(closeButtonActionEventHandler));

        String versionName = ApplicationContext.getProperties().getProperty(Constants.PROPERTY_APPLICATION_VERSION_NAME);
        String buildNumber = ApplicationContext.getProperties().getProperty(Constants.PROPERTY_APPLICATION_BUILD_NUMBER);

        // Set version info
        infoLabel.setText(String.format(getResourceBundle().getString("welcome.info"), versionName, buildNumber));
    }

    /**
     * @return the page
     */
    public String getPage() {
        return page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(String page) {
        this.page = page;
        webView.getEngine().load(getClass().getResource(page).toExternalForm());
    }
}
