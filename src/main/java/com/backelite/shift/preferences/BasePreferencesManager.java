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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.tools.ant.types.Mapper;
import org.codehaus.jackson.annotate.JsonValue;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Base implementation of PreferencesManager
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public abstract class BasePreferencesManager implements PreferencesManager {

    protected Map<String, Object> loadedValues = new HashMap<String, Object>();
    protected ObjectMapper mapper = new ObjectMapper();

    @Override
    public void setInitialValue(String key, Object value) throws PreferencesException {

        Object existingValue = this.getValue(key);
        if (existingValue == null) {
            this.setValue(key, value);
        }
    }

    @Override
    public void mergeListValue(String key, List value) throws PreferencesException {
        
        Object existingValue = this.getValue(key);
        
        if (existingValue != null) {
            
            if (existingValue instanceof List) {
                
                try {
                   List existingList = (List) existingValue;
                   
                   // Convert items to JSON string
                   List<String> existingListJSON = new ArrayList<>();
                   List<Object> duplicateEntries = new ArrayList<>();
                   for (Object item : existingList) {
                       String jsonValue = mapper.writeValueAsString(item);
                       if (existingListJSON.contains(jsonValue)) {
                           // Value already in list : remove (an item is unique in a list)
                           duplicateEntries.add(item);
                       } else {
                            existingListJSON.add(jsonValue);
                       }
                   }
                   
                   // Remove duplicates in existing list
                   existingList.removeAll(duplicateEntries);
                   
                   // Compare
                   for (Object item : value) {
                       if (!existingListJSON.contains(mapper.writeValueAsString(item))) {   
                           existingList.add(item);
                       }
                   }
                   
                } catch(Exception e) {
                    throw new PreferencesException(String.format("%s preference cannot be read", key));
                }
                   
            // Wrong existing value
            } else {
                throw new PreferencesException(String.format("%s preference is not a List", key));
            }
            
        // No value set yet ...
        } else {
            this.setValue(key, value);
        }
        
    }

    @Override
    public void setInitialValues(Map<String, Object> values) throws PreferencesException {

        for (String key : values.keySet()) {
            this.setInitialValue(key, values.get(key));
        }
    }

    @Override
    public void setValues(Map<String, Object> values) throws PreferencesException {

        for (String key : values.keySet()) {
            this.setValue(key, values.get(key));
        }
    }

    @Override
    public void setValue(String key, Object value) throws PreferencesException {
        loadedValues.put(key, value);
    }

    @Override
    public Object getValue(String key) {
        return loadedValues.get(key);
    }
}
