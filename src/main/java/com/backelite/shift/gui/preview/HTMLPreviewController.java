package com.backelite.shift.gui.preview;

/*
 * #%L
 * HTMLPreviewController.java - shift - 2013
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
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
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
    /**
     * Shared HTML server.
     */
    private static Server HTML_SERVER;
    /**
     * HTML server port : starts at 9000 until a free port is found.
     */
    private static int HTML_SERVER_PORT = 9000;
    @FXML
    private AnchorPane rootPane;
    @FXML 
    private AnchorPane topToolBar;
    @FXML 
    private AnchorPane bottomToolBar;
    @FXML
    private WebView webView;
    @FXML
    private ToggleButton orientationToggleButton;
    @FXML
    private ToggleButton trackActiveFileToggleButton;
    @FXML
    private Button resetButton;
    @FXML
    private ChoiceBox<String> presetChoice;
    List<Map<String, Object>> presets;

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
        presetChoice.valueProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {

                // Apply new preset
                applyPreset(presets.get(presetChoice.getSelectionModel().getSelectedIndex()));
            }
        });

        // Handle orientation change
        orientationToggleButton.selectedProperty().addListener(new ChangeListener<Boolean>() {

            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                // Apply preset
                applyPreset(presets.get(presetChoice.getSelectionModel().getSelectedIndex()));
            }
        });
        
        // Bind tracking button state
        trackActiveFileToggleButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                setActiveDocumentTrackingEnabled(t1);
            }
        });
        trackActiveFileToggleButton.setSelected(true);
        
        // Handle reset
        resetButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent t) {
                refresh();
            }
        });

        // Later ...
        Platform.runLater(new Runnable() {
            public void run() {

                // Title
                parentStage.setTitle(getResourceBundle().getString("builtin.plugin.preview.html.title"));
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

        int height = (Integer) preset.get("height");
        int width = (Integer) preset.get("width");
        
        // Reverse if orientation is reversed
        if (orientationToggleButton.isSelected()) {
            height = (Integer) preset.get("width");
            width = (Integer) preset.get("height");
        }
        
        this.getParentStage().setWidth(width);
        this.getParentStage().setHeight(height + topToolBar.getPrefHeight() + bottomToolBar.getPrefHeight());
       
        this.getParentStage().setResizable(false);
    }

    @Override
    public void setParentStage(Stage parentStage) {
        super.setParentStage(parentStage);

        // Apply first preset
        presetChoice.getSelectionModel().select(0);
    }
}
