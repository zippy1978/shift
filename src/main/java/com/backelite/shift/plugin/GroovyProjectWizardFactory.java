package com.backelite.shift.plugin;

/*
 * #%L
 * GroovyProjectWizardFactory.java - shift - 2013
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class GroovyProjectWizardFactory implements ProjectWizardFactory {

    private String name;
    private String description;
    private Closure<Node> code;
    private GroovyProjectGenerator projectGenerator;
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the code
     */
    public Closure<Node> getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(Closure<Node> code) {
        this.code = code;
    }

    /**
     * @return the projectGenerator
     */
    public GroovyProjectGenerator getProjectGenerator() {
        return projectGenerator;
    }

    /**
     * @param projectGenerator the projectGenerator to set
     */
    public void setProjectGenerator(GroovyProjectGenerator projectGenerator) {
        this.projectGenerator = projectGenerator;
    }
   
     public Node newProjectWizard(FXMLLoader loader) {
        return this.getCode().call(loader);
    }
}
