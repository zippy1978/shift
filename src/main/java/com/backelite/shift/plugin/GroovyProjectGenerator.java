package com.backelite.shift.plugin;

/*
 * #%L
 * GroovyProjectGenerator.java - shift - 2013
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

import com.backelite.shift.workspace.artifact.Project;
import groovy.lang.Closure;
import java.util.Map;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class GroovyProjectGenerator implements ProjectGenerator {

    private Closure<Project> code;
    
    public Project generate(String name, Map<String, Object> attributes) {
        return this.getCode().call(name, attributes);
    }

    /**
     * @return the code
     */
    public Closure<Project> getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(Closure<Project> code) {
        this.code = code;
    }

    
            
}
