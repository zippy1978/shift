/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.backelite.shift.plugin;

/*
 * #%L
 * ProjectGenerator.java - shift - 2013
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
import java.util.Map;

/**
 * Project generator interface.
 * A project generator is in charge of creating initial project files.
 * @author ggrousset
 */
public interface ProjectGenerator {
    
    /**
     * Generate project.
     * @param name Project name
     * @param attributes Project custom attributes (passed by the project wizard)
     * @return The Project generated
     */
    public Project generate(String name, Map<String, Object> attributes);
}
