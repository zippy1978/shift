package com.backelite.shift.preferences;

/*
 * #%L
 * PreferencesManager.java - shift - 2013
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
    
    /**
     * Merge map with an existing map value.
     * @param key
     * @param value
     * @throws PreferencesException 
     */
    public void mergeMapValue(String key, Map value) throws PreferencesException;

    public Object getValue(String key);

    public void commit() throws PreferencesException;

    public void rollback() throws PreferencesException;
}
