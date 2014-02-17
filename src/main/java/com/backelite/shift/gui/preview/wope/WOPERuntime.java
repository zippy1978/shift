package com.backelite.shift.gui.preview.wope;

/*
 * #%L
 * WOPERuntime.java - Shift - 2013
 * %%
 * Copyright (C) 2013 - 2014 Backelite
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

/**
 * Represents a WOPE runtime.
 * @author ggrousset
 */
public class WOPERuntime {
    
    /**
     * Instance name.
     */
    private String name;
    
    /**
     * Instance path.
     */
    private String path;
    
    /**
     * Instance HTTP port.
     */
    private int port = 0;
    
    /**
     * Indicates is the instance is running.
     */
    private boolean started = false;
    
    /**
     * Starts the current instance.
     */
    public void start() {
        
        if (!started) {
            
        }
    }
    
    /**
     * Stops the current instance.
     */
    public void stop() {
        
        if (started) {
            
        }
    }

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
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @return the started
     */
    public boolean isStarted() {
        return started;
    }
}
