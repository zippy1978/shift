/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.backelite.shift.gui.preview.wope;

/*
 * #%L
 * WOPEPreviewController.java - Shift - 2013
 * %%
 * Copyright (C) 2013 - 2014 Backelite
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
import com.backelite.shift.gui.preview.html.HTMLPreviewController;
import com.backelite.shift.util.MemoryUtils;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

/**
 * WOPE preview controller.
 *
 * @author ggrousset
 */
public class WOPEPreviewController extends HTMLPreviewController {

    @FXML
    protected ChoiceBox<String> runtimeChoice;
    
    protected ChangeListener<String> runtimeChoiceChangeListener;
    
    /**
     * Holds the previous selected runtime.
     */
    protected WOPERuntime previousRuntime;
    
    /**
     * Holds the currently selected WOPE runtime.
     */
    protected WOPERuntime currentRuntime;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        
        super.initialize(url, rb);

        // Populate runtime choice
        this.populateRuntimes();
        runtimeChoiceChangeListener = (ObservableValue<? extends String> ov, String t, String t1) -> {
            
            previousRuntime = WOPERuntimeManager.getInstance().getRuntimeByName(t);
            currentRuntime = WOPERuntimeManager.getInstance().getRuntimeByName(t1);
            
            applyRuntime();
        };
        runtimeChoice.valueProperty().addListener(new WeakChangeListener<>(runtimeChoiceChangeListener));

        if (runtimeChoice.getItems().size() == 0) {
            // No runtime configured : raise warning message
            displayInfoDialog(getResourceBundle().getString("builtin.plugin.preview.wope.title"), getResourceBundle().getString("builtin.plugin.preview.wope.not_configured.text"));
            Platform.runLater(() -> {
                close();
            });
        }

        // Later ...
        Platform.runLater(() -> {
            if (getStage() != null) {
                getStage().setTitle(getResourceBundle().getString("builtin.plugin.preview.wope.title"));
            }
        });

    }

    private void populateRuntimes() {

        runtimeChoice.getItems().clear();

        List<WOPERuntime> runtimes = WOPERuntimeManager.getInstance().getRuntimes();
        for (WOPERuntime runtime : runtimes) {
            runtimeChoice.getItems().add(runtime.getName());
        }
    }
    
    private void applyRuntime() {
        
        if (previousRuntime != null) {
            WOPERuntimeManager.getInstance().stopRuntime(previousRuntime);
        }
        
        WOPERuntimeManager.getInstance().startRuntime(currentRuntime);
        
        this.refresh();
    }
    
     @Override
    protected void refresh() {

        synchronized (document) {

            // Update webview
            if (currentRuntime != null) {
                webView.getEngine().load("http://localhost:" + currentRuntime.getPort() + document.getWorkspacePath());
            }
        }
    }
    
    @Override
    public void setStage(Stage parentStage) {
        super.setStage(parentStage);

        // Apply first runtime
        runtimeChoice.getSelectionModel().select(0);
    }
    
     @Override
    public void close() {
        super.close();
        
        if (currentRuntime != null) {
            WOPERuntimeManager.getInstance().stopRuntime(currentRuntime);
        }
        
    }

}
