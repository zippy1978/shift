package com.backelite.shift.preferences;

/*
 * #%L
 * PreferencesManager.java - shift - 2013
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

import java.util.List;
import java.util.Map;

/**
 *
 * @author ggrousset
 */
public interface PreferencesManager {

    /**
     * Set initial value. If key already exists : does nothing, otherwise add
     * the value
     *
     * @param key
     * @param value
     * @throws PreferencesException
     */
    public void setInitialValue(String key, Object value) throws PreferencesException;
    
    public void setInitialValues(Map<String, Object> values) throws PreferencesException;

    public void setValue(String key, Object value) throws PreferencesException;
    
    public void setValues(Map<String, Object> values) throws PreferencesException;
    
    /**
     * Merge list with an existing list value.
     * @param key
     * @param value
     * @throws PreferencesException 
     */
    public void mergeListValue(String key, List value) throws PreferencesException;

    public Object getValue(String key);

    public void commit() throws PreferencesException;

    public void rollback() throws PreferencesException;
}
