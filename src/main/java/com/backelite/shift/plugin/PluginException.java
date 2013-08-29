package com.backelite.shift.plugin;

/*
 * #%L
 * PluginException.java - shift - 2013
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
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class PluginException extends Exception {

    public PluginException() {
        super();
    }
    
    public PluginException(String message) {
        super(message);
    }
    
    public PluginException(Throwable cause) {
        super(cause);
    }
    
    public PluginException(String message, Throwable throwable){
        super(message, throwable);
    }
}
