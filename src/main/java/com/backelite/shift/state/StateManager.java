/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.backelite.shift.state;

/*
 * #%L
 * StateManager.java - shift - 2013
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
 * StateManager interface.
 * @author ggrousset
 */
public interface StateManager {
    
    /**
     * Save a PersistableState object.
     * @param object Object to save
     * @throws StateException 
     */
    public void save(PersistableState object) throws StateException;
    
    /**
     * Restore a PersistableState object from a given location.
     * @param object Object to restore
     * @throws StateException 
     */
    public void restore(PersistableState object) throws StateException;
}
