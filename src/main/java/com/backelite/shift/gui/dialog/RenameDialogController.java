package com.backelite.shift.gui.dialog;

/*
 * #%L
 * RenameDialogController.java - Shift - 2013
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
import com.backelite.shift.gui.validation.CompoundValidator;
import com.backelite.shift.gui.validation.FilenameValidator;
import com.backelite.shift.gui.validation.NotBlankValidator;
import com.backelite.shift.gui.validation.UnusedArtifactNameValidator;
import com.backelite.shift.workspace.artifact.Artifact;
import com.backelite.shift.workspace.artifact.Document;
import com.backelite.shift.workspace.artifact.Folder;
import com.backelite.shift.workspace.artifact.Project;
import java.io.IOException;
import javafx.concurrent.Task;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class RenameDialogController extends AbstractNewArtifactDialogController {

    @Override
    protected void handleOKButtonAction() {

        // Async save
        ApplicationContext.getTaskManager().addTask(new Task() {
            @Override
            protected Object call() throws Exception {

                updateTitle(String.format(getResourceBundle().getString("task.renaming_artifact"), nameTextField.getText()));
                
                // Rename artifact
                Artifact artifact = (Artifact) getUserData();
                try {
                    artifact.rename(nameTextField.getText());
                } catch (IOException ex) {
                    displayErrorDialog(ex);
                }

                return true;
            }
        });

        // Close
        this.close();

    }

    @Override
    public void setUserData(Object userData) {
        super.setUserData(userData);

        Artifact artifact = (Artifact) userData;

        // Set initial value
        nameTextField.setText(artifact.getName());

        // Set validators on text field
        if (artifact instanceof Project) {
            nameTextField.setValidator(new CompoundValidator(new NotBlankValidator(), new FilenameValidator()));
        } else {
            // Parent folder
            Folder folder = null;
            if (artifact instanceof Document) {
                folder = ((Document) artifact).getParentFolder();
            } else if (artifact instanceof Folder) {
                folder = ((Folder) artifact).getParentFolder();
            }
            nameTextField.setValidator(new CompoundValidator(new NotBlankValidator(), new FilenameValidator(), new UnusedArtifactNameValidator(folder)));

        }

    }
}
