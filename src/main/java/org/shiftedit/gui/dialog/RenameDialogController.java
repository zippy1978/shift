package org.shiftedit.gui.dialog;

/*
 * #%L
 * RenameDialogController.java - Shift - 2013
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
import org.shiftedit.gui.validation.CompoundValidator;
import org.shiftedit.gui.validation.FilenameValidator;
import org.shiftedit.gui.validation.NotBlankValidator;
import org.shiftedit.gui.validation.UnusedArtifactNameValidator;
import org.shiftedit.workspace.artifact.Artifact;
import org.shiftedit.workspace.artifact.Document;
import org.shiftedit.workspace.artifact.Folder;
import org.shiftedit.workspace.artifact.Project;
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
