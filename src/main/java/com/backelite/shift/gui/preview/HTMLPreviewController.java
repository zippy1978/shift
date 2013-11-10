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
import com.backelite.shift.workspace.artifact.Document;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
public class HTMLPreviewController extends AbstractPreviewController implements Observer {

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
    private WebView webView;
    
    @FXML
    private Button orientationButton;
    
    @FXML
    private ComboBox<String> presetCombo;
    
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
        presetCombo.valueProperty().addListener(new ChangeListener<String>() {

            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                
                // Apply new preset
                applyPreset(presets.get(presetCombo.getSelectionModel().getSelectedIndex()));
            }
        });
        
        // Handle orientation change
        orientationButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent t) {

                // Reverse view
                double width = getParentStage().getWidth();
                double height = getParentStage().getHeight();
                getParentStage().setWidth(height);
                getParentStage().setHeight(width);
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
    public void setDocument(Document document) {

        super.setDocument(document);
        document.getProject().addObserver(this);

        this.refresh();
    }

    private void refresh() {

       synchronized(document) {
             
        // Update webview
        webView.getEngine().load("http://localhost:" + ApplicationContext.getHTTPWorkspaceProxyServer().getPort() + document.getWorkspacePath());
       }
    }

    public void update(Observable o, Object arg) {

        // Something in the project was updated
        // Refresh view
        this.refresh();
        
    }
    
    private void populatePresetCombo() {
     
        presets = (List<Map<String, Object>>)ApplicationContext.getPreferencesManager().getValue("preview.html.presets");
        
        presetCombo.getItems().clear();
        for (Map<String, Object> preset : presets) {
            presetCombo.getItems().add((String)preset.get("name"));
        }
        
    }
    
    /**
     * Apply preset settings to the preview.
     * @param preset 
     */
    private void applyPreset(Map<String, Object> preset) {
        
        this.getParentStage().setWidth((Integer)preset.get("width"));
        this.getParentStage().setHeight((Integer)preset.get("height"));
    }

    @Override
    public void setParentStage(Stage parentStage) {
        super.setParentStage(parentStage);
        
        // Apply first preset
        presetCombo.getSelectionModel().select(0);
    }
    
    
}
