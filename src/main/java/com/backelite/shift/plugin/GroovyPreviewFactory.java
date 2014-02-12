package com.backelite.shift.plugin;

/*
 * #%L
 * GroovyPreviewFactory.java - shift - 2013
 * %%
 * Copyright (C) 2013 Gilles Grousset
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
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class GroovyPreviewFactory implements PreviewFactory {

    private String name;
    private String description;
    private List<String> supportedExtensions;
    private Closure<Node> code;

    /**
     * @return the name
     */
    @Override
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
    @Override
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
     * @return the supportedExtensions
     */
    @Override
    public List<String> getSupportedExtensions() {
        return supportedExtensions;
    }

    /**
     * @param supportedExtensions the supportedExtensions to set
     */
    public void setSupportedExtensions(List<String> supportedExtensions) {
        this.supportedExtensions = supportedExtensions;
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

    @Override
    public Node newPreview(FXMLLoader loader) {
        return getCode().call(loader);
    }
}
