package com.backelite.shift.state;

import java.util.Map;

/*
 * #%L
 * PersistableState.java - shift - 2013
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
 * PersistableState interface.
 * Used to handle state of an object.
 * @author ggrousset
 */
public interface PersistableState {

    /**
     * Save object state.
     * @param state State to populate.
     */
    public void saveState(Map<String, Object> state) throws StateException;
    
    /**
     * Restore object state.
     * @param state State to restore.
     */
    public void restoreState(Map<String, Object> state) throws StateException;
    
    /**
     * Return the instance identifier.
     * If empty or null : no instance identifier will be used, only one state will be used for every instance.
     * @return String or null.
     */
    public String getInstanceIdentifier();
}
