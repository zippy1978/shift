package com.backelite.shift.gui;

/*
 * #%L
 * FXMLLoaderFactory.java - shift - 2013
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

import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class FXMLLoaderFactory {
    
    private static final String BUNDLE_I18N = "i18n";

    public static FXMLLoader newInstance() {
        
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(ResourceBundle.getBundle(BUNDLE_I18N, Locale.getDefault()));
        
        return loader;
    }
}
