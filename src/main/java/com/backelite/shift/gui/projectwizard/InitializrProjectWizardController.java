package com.backelite.shift.gui.projectwizard;

/*
 * #%L
 * InitializrProjectWizardController.java - Shift - 2013
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

import com.backelite.shift.ApplicationContext;
import com.backelite.shift.workspace.artifact.Project;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class InitializrProjectWizardController extends BasicProjectWizardController {

    @FXML
    private ChoiceBox flavorChoice;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        
        // Default selection
        flavorChoice.getSelectionModel().select(0);
    }
    
    @Override
    protected void handleOKButtonAction() {
        
        // Create project and import into workspace
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put("location", locationTextField.getText());
        attributes.put("flavor", flavorChoice.getSelectionModel().getSelectedItem().toString());
        ApplicationContext.getTaskManager().addTask(new Task() {

            @Override
            protected Object call() throws Exception {
                
                updateTitle(String.format(getResourceBundle().getString("task.generating_project"), nameTextField.getText()));
                Project project = getProjectGenerator().generate(nameTextField.getText(), attributes);
                ApplicationContext.getWorkspace().openProject(project);
                updateProgress(1, 1);
                
                return true;
            }
            
            
        });
        
        // Close Window
        this.close();
    }
}
