/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.backelite.shift.gui.dialog;

/*
 * #%L
 * DialogController.java - shift - 2013
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

import com.backelite.shift.gui.Controller;
import javafx.stage.Stage;

/**
 *
 * @author ggrousset
 */
public interface DialogController extends Controller {
    
    public Stage getStage();
    public void setStage(Stage stage);
    @Override
    public void close();
    public void setUserData(Object userData);
    public Object getUserData();
}
