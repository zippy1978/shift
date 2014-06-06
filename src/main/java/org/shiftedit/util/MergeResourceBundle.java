/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shiftedit.util;

/*
 * #%L
 * MergeResourceBundle.java - Shift - 2013
 * %%
 * Copyright (C) 2013 - 2014 Shift
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * ResourceBundle implementaiton able to merge multiple other bundles.
 * Borrowed from https://raw.githubusercontent.com/tatsu-no-otoshigo/javafx-modules/master/src/main/java/proj/green/javafx/modules/fxml/MergeResourceBundle.java
 * @author ggrousset
 */
public class MergeResourceBundle extends ResourceBundle {
    
  
    protected Map<String, Object> resourceMap = new HashMap<>();
    
    protected List<ResourceBundle> resources = new ArrayList<>();
    
    /**
     * 
     * @param resource
     */
    public void addResource(final ResourceBundle resource) {
        if(resource == null) {
            return;
        }
        
        resources.add(resource);
        resource.keySet().stream().forEach((key) -> {
            resourceMap.put(key, resource.getObject(key));
        });
    }
    
    /**
     * {@inheritDoc}
     * @return 
     */
    @Override
    public boolean containsKey(final String key) {
        return resourceMap.containsKey(key);
    }
    
    /**
     * {@inheritDoc}
     * @return 
     */
    @Override
    protected Object handleGetObject(final String key) {
        return resourceMap.get(key);
    }
    
    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(resourceMap.keySet());
    }
    
    /**
     * @return
     */
    public boolean isEmptyResources() {
        return resources.isEmpty();
    }
}
