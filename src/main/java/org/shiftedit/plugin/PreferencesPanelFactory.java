package org.shiftedit.plugin;

/*
 * #%L
 * PreferencesPanelFactory.java - Shift - 2013
 * %%
 * Copyright (C) 2013 - 2014 Shift
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import groovy.lang.Closure;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

/**
 *
 * @author ggrousset
 */
public interface PreferencesPanelFactory {

    /**
     * Return path of the panel in the preferences tree. For instance :
     * section/sub-section/mypanel
     *
     * @return Panel path in the preferences tree.
     */
    public String getPath();

    /**
     * Builds a new preferences panel.
     *
     * @param loader FXML loader
     * @return Root node of the created panel
     */
    public Node newPreferencesPanel(FXMLLoader loader);
}
