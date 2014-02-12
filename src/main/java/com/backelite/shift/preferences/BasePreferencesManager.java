package com.backelite.shift.preferences;

/*
 * #%L
 * BasePreferencesManager.java - Shift - 2013
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
