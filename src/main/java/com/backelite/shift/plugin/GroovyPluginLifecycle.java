package com.backelite.shift.plugin;

/*
 * #%L
 * GroovyPluginLifecycle.java - shift - 2013
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

import groovy.lang.Closure;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class GroovyPluginLifecycle implements PluginLifecycle {

    private Closure onLoad;

    @Override
    public void load() {

        if (onLoad != null) {
            onLoad.call();
        }
    }

    /**
     * @return the onLoad
     */
    public Closure getOnLoad() {
        return onLoad;
    }

    /**
     * @param onLoad the onLoad to set
     */
    public void setOnLoad(Closure onLoad) {
        this.onLoad = onLoad;
    }
}
