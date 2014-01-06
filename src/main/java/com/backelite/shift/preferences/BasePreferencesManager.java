package com.backelite.shift.preferences;

/*
 * #%L
 * BasePreferencesManager.java - Shift - 2013
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base implementation of PreferencesManager
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public abstract class BasePreferencesManager implements PreferencesManager {

    protected Map<String, Object> loadedValues = new HashMap<String, Object>();

    public void setInitialValue(String key, Object value) throws PreferencesException {

        Object existingValue = this.getValue(key);
        if (existingValue == null) {
            this.setValue(key, value);
        }
    }

    public void mergeListValue(String key, List value) throws PreferencesException {
        
        Object existingValue = this.getValue(key);
        
        if (existingValue != null) {
            
            if (existingValue instanceof List) {
                
                   List existingList = (List) existingValue;
                   for (Object item : value) {
                       if (!existingList.contains(item)) {
                           existingList.add(item);
                       }
                   }
                   
            // Wrong existing value
            } else {
                throw new PreferencesException(String.format("%s preference is not a List", key));
            }
            
        // No vlaue set yet ...
        } else {
            this.setValue(key, existingValue);
        }
        
    }

    public void setInitialValues(Map<String, Object> values) throws PreferencesException {

        for (String key : values.keySet()) {
            this.setInitialValue(key, values.get(key));
        }
    }

    public void setValues(Map<String, Object> values) throws PreferencesException {

        for (String key : values.keySet()) {
            this.setValue(key, values.get(key));
        }
    }

    public void setValue(String key, Object value) throws PreferencesException {
        loadedValues.put(key, value);
    }

    public Object getValue(String key) {
        return loadedValues.get(key);
    }
}
