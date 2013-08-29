package com.backelite.shift.gui.dialog;

/*
 * #%L
 * NewFolderDialogController.java - shift - 2013
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
import com.backelite.shift.workspace.artifact.Folder;
import java.io.IOException;
import javafx.concurrent.Task;

/**
 * New folder dialog controller.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class NewFolderDialogController extends AbstractNewArtifactDialogController {

    @Override
    protected void handleOKButtonAction() {

        // Async save
        ApplicationContext.getTaskManager().addTask(new Task() {
            @Override
            protected Object call() throws Exception {
                
                updateTitle(String.format(getResourceBundle().getString("task.creating_folder"), nameTextField.getText()));

                // Create new folder
                Folder folder = (Folder) getUserData();
                try {
                    folder.createSubFolder(nameTextField.getText());
                } catch (IOException ex) {
                    displayErrorDialog(ex);
                }

                return true;
            }
        });

        // Close
        this.close();

    }
}
