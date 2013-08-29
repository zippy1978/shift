package com.backelite.shift.preferences;

/*
 * #%L
 * LocalPreferencesManager.java - shift - 2013
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
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class LocalPreferencesManager implements PreferencesManager {

    private static final Logger log = LoggerFactory.getLogger(LocalPreferencesManager.class);
    private static final String FILENAME = "preferences.json";
    private File rootDirectory;
    private Map<String, Object> loadedValues = new HashMap<String, Object>();

    public LocalPreferencesManager(File rootDirectory) {
        super();
        this.rootDirectory = rootDirectory;
        try {
            this.load();
        } catch (PreferencesException ex) {
            log.error("Failed to load preferences", ex);
        }
    }

    public void setInitialValue(String key, Object value) throws PreferencesException {

        Object existingValue = this.getValue(key);
        if (existingValue == null) {
            this.setValue(key, value);
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

    public Object getValue(String key){
        return loadedValues.get(key);
    }

    public void commit() throws PreferencesException {

        File file = new File(rootDirectory, FILENAME);
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(file, loadedValues);
        } catch (Exception ex) {
            throw new PreferencesException(ex);
        }
    }

    public void rollback() throws PreferencesException {
        this.load();
    }

    private void load() throws PreferencesException {

        File file = new File(rootDirectory, FILENAME);
        if (file.exists()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                loadedValues = mapper.readValue(file, Map.class);
            } catch (Exception ex) {
                throw new PreferencesException(ex);
            }
        }
    }
}
