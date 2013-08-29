
package com.backelite.shift.gui.projectwizard;

import com.backelite.shift.gui.dialog.DialogController;
import com.backelite.shift.plugin.ProjectGenerator;
import javafx.stage.Stage;

/*
 * #%L
 * ProjectWizardController.java - shift - 2013
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

/**
 *
 * @author ggrousset
 */
public interface ProjectWizardController extends DialogController {
    
    public void setProjectGenerator(ProjectGenerator projectGenerator);
}
