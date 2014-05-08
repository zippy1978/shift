/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiftedit.gui.preview;

/*
 * #%L
 * PreviewController.java - shift - 2013
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
import org.shiftedit.gui.dialog.DialogController;
import org.shiftedit.gui.editor.EditorController;
import org.shiftedit.plugin.PreviewFactory;
import org.shiftedit.workspace.artifact.Document;
import javafx.beans.value.ChangeListener;

/**
 * Preview controller interface. All preview controllers must implement this
 * interface.
 *
 * @author ggrousset
 */
public interface PreviewController extends DialogController {

    public void setDocument(Document document);

    public Document getDocument();

    public void setFactory(PreviewFactory factory);
    
    /**
     * Return a change listener to track the active editor.
     * If the preview does not provide the ability to track the active editor, return null.
     * @return A ChangeListener
     */
    public ChangeListener<EditorController> getActiveEditorChangeListener();
    
}
