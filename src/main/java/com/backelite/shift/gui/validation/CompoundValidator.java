package com.backelite.shift.gui.validation;

/*
 * #%L
 * CompoundValidator.java - shift - 2013
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A validator capable of chaining multiple validators.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class CompoundValidator implements Validator{

    private List<Validator> validators = new ArrayList<Validator>();

    public CompoundValidator(Validator... validators) {
        Collections.addAll(this.validators, validators);
    }

    @Override
    public ValidatorResult validate(Object input) {
        
        ValidatorResult result = new ValidatorResult();
        for (Validator validator : this.validators) {
            ValidatorResult subResult = validator.validate(input);
            if (!subResult.isValid()) {
                result.setValid(false);
                result.getErrorMessages().addAll(subResult.getErrorMessages());
            }
        }
        
        return result;
    }
    
    
}
