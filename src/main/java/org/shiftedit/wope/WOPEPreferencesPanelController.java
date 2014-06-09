package org.shiftedit.wope;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.shiftedit.ApplicationContext;
import org.shiftedit.gui.control.ValidatedTextField;
import org.shiftedit.gui.dialog.ConfirmDialogController;
import org.shiftedit.gui.preferences.panel.AbstractPreferencesPanelController;
import org.shiftedit.gui.validation.NotBlankValidator;
import org.shiftedit.preferences.PreferencesException;
import org.shiftedit.util.MemoryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger log = LoggerFactory.getLogger(WOPEPreferencesPanelController.class);

    public static final String LICENSE_PATH_PREF_KEY = "preview.wope.licensePath";
    public static final String RUNTIMES_PREF_KEY = "preview.wope.runtimes";

    @FXML
    private ValidatedTextField licenseTextField;

    @FXML
    protected Label licenseErrorLabel;

    @FXML
    private Button browseLicenseButton;

    @FXML
    private Button addButton;

    @FXML
    private Button removeButton;

    @FXML
    private TableView runtimeTable;
    private final ObservableList<WOPERuntime> tableModel = FXCollections.observableArrayList();

    private EventHandler<ActionEvent> browseLicenseButtonActionEventHandler;
    private EventHandler<ActionEvent> removeButtonActionEventHandler;
    private EventHandler<ActionEvent> addButtonActionEventHandler;

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

        // Add button
        addButtonActionEventHandler = (ActionEvent t) -> {
            handleAddRuntime();
        };
        addButton.setOnAction(new WeakEventHandler<>(addButtonActionEventHandler));

        // Remove button
        removeButtonActionEventHandler = (ActionEvent t) -> {
            handleRemoveRuntime();
        };
        removeButton.setOnAction(new WeakEventHandler<>(removeButtonActionEventHandler));
        removeButton.disableProperty().bind(runtimeTable.getSelectionModel().selectedItemProperty().isNull());

        // Init existing values
        licenseTextField.setText((String) ApplicationContext.getPreferencesManager().getValue(LICENSE_PATH_PREF_KEY));

        // Handle license browse button
        browseLicenseButtonActionEventHandler = (ActionEvent t) -> {
            handleBrowseLicenseButtonAction();
        };
        browseLicenseButton.setOnAction(new WeakEventHandler<>(browseLicenseButtonActionEventHandler));

        // Runtime table setup
        this.setupRuntimeTable();
    }

    private void setupRuntimeTable() {

        // Cell factory
        Callback<TableColumn, TableCell> cellFactory
                = (TableColumn p) -> {
                    TextFieldTableCell cell = new TextFieldTableCell();
                    return cell;
                };

        // Name
        TableColumn nameCol = new TableColumn(getResourceBundle().getString("preferences.wope.runtimes.name"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setCellFactory(cellFactory);
        nameCol.prefWidthProperty().bind(runtimeTable.widthProperty().divide(4).subtract(3));
        runtimeTable.getColumns().add(nameCol);

        // Path
        TableColumn pathCol = new TableColumn(getResourceBundle().getString("preferences.wope.runtimes.path"));
        pathCol.setCellValueFactory(new PropertyValueFactory<>("path"));
        pathCol.setCellFactory(cellFactory);
        pathCol.prefWidthProperty().bind(runtimeTable.widthProperty().divide(4).multiply(3));
        runtimeTable.getColumns().add(pathCol);

        runtimeTable.setPlaceholder(new Label(getResourceBundle().getString("preferences.wope.runtimes.no_runtime")));

        // Populate
        tableModel.addAll(WOPERuntimeManager.getInstance().getRuntimes());

        runtimeTable.setItems(tableModel);

    }

    private void handleAddRuntime() {

        FileChooser fileChooser = new FileChooser();
        List<String> extensions = new ArrayList<>();
        extensions.add("*.war");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(this.getResourceBundle().getString("preferences.wope.runtime_extension.text"), extensions));

        File selectedFile = fileChooser.showOpenDialog(this.getStage());

        if (selectedFile != null) {

            // Check if selected file is a valide runtime
            Manifest manifest = getRuntimeManifest(selectedFile);
            if (manifest == null) {
                this.displayErrorDialog(getResourceBundle().getString("preferences.title"), getResourceBundle().getString("preferences.wope.runtime.invalid_runtime.text"), null);
            } else {
                
                Attributes attrs = (Attributes) manifest.getMainAttributes();
                
                boolean isPackageValid = attrs.getValue("Implementation-Title").contains("WOPE");
                String version = attrs.getValue("Implementation-Version");
                boolean isVersionValid = !version.startsWith("5.") && !version.equals("6.0.0") && !version.equals("6.0.1") && !version.equals("6.0.2");
                
                if (!isPackageValid || !isVersionValid) {
                    this.displayErrorDialog(getResourceBundle().getString("preferences.title"), getResourceBundle().getString("preferences.wope.runtime.invalid_runtime.text"), null);
                } else {
                    tableModel.add(new WOPERuntime(String.format("WOPE %s", version), selectedFile.getAbsolutePath()));    
                }
                
            }
            
        }
    }

    private Manifest getRuntimeManifest(File runtime) {

        try {
            JarFile jar = new JarFile(runtime);
            return jar.getManifest();
            
        } catch (IOException ex) {
            log.error("Failed to retrieve manifest data", ex);
            return null;
        }

    }

    private void handleRemoveRuntime() {
        this.displayConfirmDialog(getResourceBundle().getString("preferences.title"), getResourceBundle().getString("preferences.wope.runtimes.remove_confirmation.text"), (ConfirmDialogController.ChoiceEvent t) -> {
            if (t.getChoice() == ConfirmDialogController.Choice.POSITIVE) {
                int index = runtimeTable.getSelectionModel().getSelectedIndex();
                tableModel.remove(index);
            }
        });
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
                // License
                ApplicationContext.getPreferencesManager().setValue(LICENSE_PATH_PREF_KEY, licenseTextField.getText());

                // Runtimes
                List<Map<String, Object>> runtimes = new ArrayList<>();
                for (WOPERuntime runtime : tableModel) {
                    Map<String, Object> runtimeData = new HashMap<>();
                    runtimeData.put("name", runtime.getName());
                    runtimeData.put("path", runtime.getPath());

                    runtimes.add(runtimeData);
                }
                ApplicationContext.getPreferencesManager().setValue(RUNTIMES_PREF_KEY, runtimes);

                // Force runtime manager reload
                WOPERuntimeManager.getInstance().reload();

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

    @Override
    public void close() {
        super.close();

        // Table clean up (if cell factory is not removed = memory leak)
        MemoryUtils.cleanUpTableView(runtimeTable);
    }

}
