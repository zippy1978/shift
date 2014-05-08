package org.shiftedit.gui.preview.html;

/*
 * #%L
 * HTMLPreviewController.java - shift - 2013
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
import org.shiftedit.gui.preview.AbstractPreviewController;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.eclipse.jetty.server.Server;
import org.slf4j.LoggerFactory;

/**
 * HTML preview. works with a HTTP server (workspace is mounted) All HTML
 * preview instances share the same server.
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class HTMLPreviewController extends AbstractPreviewController {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(HTMLPreviewController.class);

    @FXML
    protected AnchorPane rootPane;
    @FXML
    protected AnchorPane topToolBar;
    @FXML
    protected AnchorPane bottomToolBar;
    @FXML
    protected WebView webView;
    @FXML
    protected ToggleButton orientationToggleButton;
    @FXML
    protected ToggleButton trackActiveFileToggleButton;
    @FXML
    protected Button resetButton;
    @FXML
    protected ChoiceBox<String> presetChoice;
    List<Map<String, Object>> presets;
    
    protected ChangeListener<String> presetChoiceChangeListener;
    protected ChangeListener<Boolean> orientationChangeListener;
    protected ChangeListener<Boolean> trackActiveFileChangeListener;
    protected EventHandler<ActionEvent> resetActionEventHandler;
    
    public HTMLPreviewController() {
        super();

        // Start Workspace proxy (just to be sure...)
        ApplicationContext.getHTTPWorkspaceProxyServer().start();

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);

        // Disable caching
        webView.setCache(false);

        // Populate preset combo
        this.populatePresetCombo();
        presetChoiceChangeListener = (ObservableValue<? extends String> ov, String t, String t1) -> {
            applyPreset(presets.get(presetChoice.getSelectionModel().getSelectedIndex()));
        };
        presetChoice.valueProperty().addListener(new WeakChangeListener<>(presetChoiceChangeListener));

        // Handle orientation change
        orientationChangeListener =  (ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            applyPreset(presets.get(presetChoice.getSelectionModel().getSelectedIndex()));
        };
        orientationToggleButton.selectedProperty().addListener(new WeakChangeListener<>(orientationChangeListener));

        // Bind tracking button state
        trackActiveFileChangeListener = (ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            setActiveDocumentTrackingEnabled(t1);
        };
        trackActiveFileToggleButton.selectedProperty().addListener(new WeakChangeListener<>(trackActiveFileChangeListener));
        trackActiveFileToggleButton.setSelected(true);

        // Handle reset
        resetActionEventHandler = (ActionEvent t) -> {
            refresh();
        };
        resetButton.setOnAction(new WeakEventHandler<>(resetActionEventHandler));

        // Later ...
        Platform.runLater(() -> {
            if (getStage() != null) {
                getStage().setTitle(getResourceBundle().getString("builtin.plugin.preview.html.title"));
            }
        });

    }

    @Override
    protected void refresh() {

        synchronized (document) {

            // Update webview
            webView.getEngine().load("http://localhost:" + ApplicationContext.getHTTPWorkspaceProxyServer().getPort() + document.getWorkspacePath());
        }
    }

    private void populatePresetCombo() {

        // Get presets from preferences
        presets = (List<Map<String, Object>>) ApplicationContext.getPreferencesManager().getValue("preview.html.presets");
        
        presetChoice.getItems().clear();
        for (Map<String, Object> preset : presets) {
            presetChoice.getItems().add((String) preset.get("name"));
        }

    }

    /**
     * Apply preset settings to the preview.
     *
     * @param preset
     */
    private void applyPreset(final Map<String, Object> preset) {

        // User agent
        webView.getEngine().setUserAgent((String) preset.get("userAgent"));

        // Screen size
        int height = (Integer) preset.get("height");
        int width = (Integer) preset.get("width");

        // Reverse if orientation is reversed
        if (orientationToggleButton.isSelected()) {
            height = (Integer) preset.get("width");
            width = (Integer) preset.get("height");
        }

        this.getStage().setWidth(width);
        this.getStage().setHeight(height + topToolBar.getPrefHeight() + bottomToolBar.getPrefHeight());

        this.getStage().setResizable(false);

        // Refresh
        this.refresh();
    }

    @Override
    public void setStage(Stage parentStage) {
        super.setStage(parentStage);

        // Apply first preset
        presetChoice.getSelectionModel().select(0);
    }
}
