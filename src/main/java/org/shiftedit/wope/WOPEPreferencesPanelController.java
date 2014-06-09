package org.shiftedit.wope;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.commons.lang.ArrayUtils;
import org.apache.tools.ant.util.CollectionUtils;
import org.shiftedit.ApplicationContext;
import org.shiftedit.gui.AbstractController;
import org.shiftedit.gui.control.ValidatedTextField;
import org.shiftedit.gui.dialog.ConfirmDialogController;
import org.shiftedit.gui.preferences.panel.AbstractPreferencesPanelController;
import org.shiftedit.gui.preferences.panel.PreferencesPanelController;
import org.shiftedit.gui.validation.CompoundValidator;
import org.shiftedit.gui.validation.FilenameValidator;
import org.shiftedit.gui.validation.NotBlankValidator;
import org.shiftedit.preferences.PreferencesException;

/*
 * #%L
 * WOPEPreferencesPanelController.java - Shift - 2013
 * %%
 * Copyright (C) 2013 - 2014 Shift
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
 * @author ggrousset
 */
public class WOPEPreferencesPanelController extends AbstractPreferencesPanelController {

    public static final String LICENSE_PATH_PREF_KEY = "preview.wope.licensePath";

    @FXML
    private ValidatedTextField licenseTextField;
    
    @FXML
    protected Label licenseErrorLabel;

    @FXML
    private Button browseLicenseButton;

    private EventHandler<ActionEvent> browseLicenseButtonActionEventHandler;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        
        // Set validators on text field
        licenseTextField.setValidator(new NotBlankValidator());
        
        // Listen to input validity
        licenseErrorLabel.setVisible(false);
        licenseTextField.validProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            
            if (licenseTextField.isValid()) {
                licenseErrorLabel.setVisible(false);
            } else {
                licenseErrorLabel.setVisible(true);
                licenseErrorLabel.setText(getResourceBundle().getString(licenseTextField.getLastValidatorResult().getErrorMessages().get(0)));
            }
        });
        
        // Init existing values
        licenseTextField.setText((String) ApplicationContext.getPreferencesManager().getValue(LICENSE_PATH_PREF_KEY));
        

        // Handle license browse button
        browseLicenseButtonActionEventHandler = (ActionEvent t) -> {
            handleBrowseLicenseButtonAction();
        };
        browseLicenseButton.setOnAction(new WeakEventHandler<>(browseLicenseButtonActionEventHandler));

    }

    private void handleBrowseLicenseButtonAction() {

        FileChooser fileChooser = new FileChooser();
        List<String> extensions = new ArrayList<>();
        extensions.add("*.lic");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(this.getResourceBundle().getString("preferences.wope.license_extension.text"), extensions));

        File selectedFile = fileChooser.showOpenDialog(this.getStage());

        if (selectedFile != null) {
            licenseTextField.setText(selectedFile.getAbsolutePath());
        }

    }

    @Override
    public boolean applyChanges() {
        
        if (licenseTextField.isValid()) {

            try {

                // Save into preferences
                ApplicationContext.getPreferencesManager().setValue(LICENSE_PATH_PREF_KEY, licenseTextField.getText());

                return true;

            } catch (PreferencesException ex) {
                this.displayErrorDialog(ex);
                return false;
            }
        
        } else {
            this.displayInvalidDataDialog();
            return false;
        }
    }

}
