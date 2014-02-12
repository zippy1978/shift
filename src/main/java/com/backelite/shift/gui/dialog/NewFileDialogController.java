package com.backelite.shift.gui.dialog;

/*
 * #%L
 * NewFileDialogController.java - shift - 2013
 * %%
 * Copyright (C) 2013 Gilles Grousset
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
import com.backelite.shift.workspace.artifact.Folder;
import java.io.IOException;
import javafx.concurrent.Task;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class NewFileDialogController extends AbstractNewArtifactDialogController {

    @Override
    protected void handleOKButtonAction() {

        // Async save
        ApplicationContext.getTaskManager().addTask(new Task() {
            @Override
            protected Object call() throws Exception {
                
                updateTitle(String.format(getResourceBundle().getString("task.creating_file"), nameTextField.getText()));

                // Create new file
                Folder folder = (Folder) getUserData();
                try {
                    folder.createDocument(nameTextField.getText());
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
