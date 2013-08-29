package com.backelite.shift.gui.validation;

/*
 * #%L
 * UnusedArtifactNameValidator.java - shift - 2013
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
import com.backelite.shift.workspace.artifact.Artifact;
import com.backelite.shift.workspace.artifact.Folder;

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
