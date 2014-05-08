package org.shiftedit.gui.validation;

/*
 * #%L
 * UnusedArtifactNameValidator.java - shift - 2013
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

import org.shiftedit.ApplicationContext;
import org.shiftedit.workspace.artifact.Artifact;
import org.shiftedit.workspace.artifact.Folder;

/**
 * Validator checking if a given artifact name is unused in a particular Folder.
 * Input must be a String object.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class UnusedArtifactNameValidator implements Validator {

    private Folder parentFolder;
    public UnusedArtifactNameValidator(Folder parentFolder) {
        this.parentFolder = parentFolder;
    }
    
    @Override
    public ValidatorResult validate(Object input) {
        
        ValidatorResult result = new ValidatorResult();
        
        if (input != null && input instanceof String) {
            String string = (String)input;
            Artifact found = ApplicationContext.getWorkspace().findArtifactByWorkspacePath(String.format("%s/%s", parentFolder.getWorkspacePath(), string));
            if (found != null) {
                result.setValid(false);
                result.getErrorMessages().add("validator.unused.artifact.name");
            }
        } 
        
        return result;
        
    }

    
}
