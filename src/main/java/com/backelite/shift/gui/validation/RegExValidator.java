package com.backelite.shift.gui.validation;

/*
 * #%L
 * RegExValidator.java - shift - 2013
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

/**
 * Generic regex validator.
 * Input must be a String object.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class RegExValidator implements Validator {

    protected String match = ".";

    public RegExValidator(String match) {
        this.match = match;
    }

    @Override
    public ValidatorResult validate(Object input) {
        
       ValidatorResult result = new ValidatorResult();
        
        if (input != null && input instanceof String) {
            String string = (String)input;
            if (!string.matches(match)) {
                result.setValid(false);
                result.getErrorMessages().add("validator.regex");
            }
        } 
        
        return result;
    }
    
    
}
