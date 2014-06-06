package org.shiftedit.gui.preferences;

/*
 * #%L
 * PreferencesController.java - Shift - 2013
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
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.shiftedit.ApplicationContext;
import org.shiftedit.gui.FXMLLoaderFactory;
import org.shiftedit.gui.dialog.AbstractDialogController;
import org.shiftedit.gui.preferences.panel.PreferencesPanelController;
import org.shiftedit.plugin.PreferencesPanelFactory;
import org.shiftedit.preferences.PreferencesException;

/**
 *
 * @author ggrousset
 */
public class PreferencesDialogController extends AbstractDialogController {

    @FXML
    private TreeView<PreferencesPanelFactory> treeView;
    @FXML
    private AnchorPane containerPane;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    private TreeItem<PreferencesPanelFactory> treeItemRoot;
    private PreferencesPanelController currentPreferencesPanelController;

    private EventHandler<ActionEvent> cancelActionEventHandler;
    private EventHandler<ActionEvent> okActionEventHandler;
    private ChangeListener<TreeItem<PreferencesPanelFactory>> treeViewChangeListener;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);

        // Cancel button click
        cancelActionEventHandler = (ActionEvent t) -> {
            handleCancelButtonAction();
        };
        cancelButton.setOnAction(new WeakEventHandler<>(cancelActionEventHandler));

        // OK button click
        okActionEventHandler = (ActionEvent t) -> {
            handleOKButtonAction();
        };
        okButton.setOnAction(new WeakEventHandler<>(okActionEventHandler));

        // Build root
        treeItemRoot = new TreeItem<>(new PlaceholderPreferencesPanelFactory(""));
        treeView.setRoot(treeItemRoot);
        treeView.setShowRoot(false);
        treeItemRoot.setExpanded(true);
        treeView.setCellFactory((TreeView<PreferencesPanelFactory> treeView) -> {
            PreferencesTreeCell cell = new PreferencesTreeCell();
            // Pass resource bundle for i18n
            cell.setUserData(getResourceBundle());
            return cell;
        });

        // Build tree view
        this.buildTreeView();

        // Listen to selection change
        treeViewChangeListener = (ObservableValue<? extends TreeItem<PreferencesPanelFactory>> ov, TreeItem<PreferencesPanelFactory> t, TreeItem<PreferencesPanelFactory> t1) -> {
            if (t1 != null) {
                try {

                    // Apply changes before leaving current panel
                    boolean canChange = true;
                    if (currentPreferencesPanelController != null) {
                        canChange = currentPreferencesPanelController.applyChanges();
                    }

                    if (canChange) {

                        PreferencesPanelFactory panelFactory = (PreferencesPanelFactory) t1.getValue();
                        containerPane.getChildren().clear();
                        currentPreferencesPanelController = null;
                        if (!(panelFactory instanceof PlaceholderPreferencesPanelFactory)) {
                            FXMLLoader loader = FXMLLoaderFactory.newInstance();
                            Node panelNode = panelFactory.newPreferencesPanel(loader);
                            currentPreferencesPanelController = (PreferencesPanelController) loader.getController();
                            currentPreferencesPanelController.setStage(this.getStage());
                            AnchorPane.setRightAnchor(panelNode, 0.0);
                            AnchorPane.setTopAnchor(panelNode, 0.0);
                            AnchorPane.setLeftAnchor(panelNode, 0.0);
                            AnchorPane.setBottomAnchor(panelNode, 0.0);
                            containerPane.getChildren().add(panelNode);
                        }

                    }
                } catch (Exception e) {
                    displayErrorDialog(e);
                }
            }
        };
        treeView.getSelectionModel().selectedItemProperty().addListener(treeViewChangeListener);

    }

    private void buildTreeView() {

        List<PreferencesPanelFactory> panelFactories = ApplicationContext.getPluginRegistry().getPreferencesPanelFactories();

        for (PreferencesPanelFactory panelFactory : panelFactories) {

            this.addPanel(panelFactory);
        }

    }

    private TreeItem<PreferencesPanelFactory> getItemByPath(String path) {

        TreeItem<PreferencesPanelFactory> item = treeItemRoot;

        String parts[] = path.split("/");

        if (parts.length > 0) {
            for (int i = 0; i < parts.length; i++) {
                item = this.findOrCreateChildItem(item, parts[i]);
            }
        } else {
            item = this.findOrCreateChildItem(item, path);
        }

        return item;
    }

    private TreeItem<PreferencesPanelFactory> findOrCreateChildItem(TreeItem<PreferencesPanelFactory> parentItem, String childName) {

        TreeItem<PreferencesPanelFactory> result = null;

        String childPath = parentItem.getValue().getPath() + "/" + childName;
        if (parentItem.getValue().getPath().isEmpty()) {
            childPath = childName;
        }

        for (TreeItem<PreferencesPanelFactory> childItem : parentItem.getChildren()) {
            if (childItem.getValue().getPath().equals(childPath)) {
                result = childItem;
                break;
            }

        }

        // Not found : create it
        if (result == null) {
            result = new TreeItem<>(new PlaceholderPreferencesPanelFactory(childPath));
            parentItem.getChildren().add(result);
        }

        return result;
    }

    private void addPanel(PreferencesPanelFactory panelFactory) {

        // Determine parent
        TreeItem<PreferencesPanelFactory> parentItem = treeItemRoot;
        if (panelFactory.getPath().contains("/")) {
            String parentPath = panelFactory.getPath().substring(0, panelFactory.getPath().lastIndexOf("/"));
            parentItem = this.getItemByPath(parentPath);
        }

        // Create item
        TreeItem<PreferencesPanelFactory> panel = new TreeItem<>(panelFactory);

        // Add to parent item
        parentItem.getChildren().add(panel);
    }

    private void handleCancelButtonAction() {

        try {
            // Rollback changes
            ApplicationContext.getPreferencesManager().rollback();

        } catch (PreferencesException ex) {
            this.displayErrorDialog(ex);
        }

        // Close anyway...
        this.close();

    }

    private void handleOKButtonAction() {

        // Apply changes
        boolean canCommit = true;
        if (currentPreferencesPanelController != null) {
            canCommit = currentPreferencesPanelController.applyChanges();
        }

        if (canCommit) {

            try {

                // Commit pending changes
                ApplicationContext.getPreferencesManager().commit();

                // Close
                this.close();

            } catch (PreferencesException ex) {
                this.displayErrorDialog(ex);
            }

        }

    }

    @Override
    public void close() {

        // Rollback changes
        try {
            ApplicationContext.getPreferencesManager().rollback();

        } catch (PreferencesException ex) {
            this.displayErrorDialog(ex);
        }

        treeView.getSelectionModel().selectedItemProperty().removeListener(treeViewChangeListener);

        super.close();
    }

}
