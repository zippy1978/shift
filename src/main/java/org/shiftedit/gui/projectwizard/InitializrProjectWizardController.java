package org.shiftedit.gui.projectwizard;

/*
 * #%L
 * InitializrProjectWizardController.java - Shift - 2013
 * %%
 * Copyright (C) 2013 - 2014 Gilles Grousset
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
import org.shiftedit.workspace.artifact.Project;
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
