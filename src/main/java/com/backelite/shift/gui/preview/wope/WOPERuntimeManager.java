package com.backelite.shift.gui.preview.wope;

/*
 * #%L
 * WOPERuntimeManager.java - Shift - 2013
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

import java.util.ArrayList;
import java.util.List;

/**
 * WOPE runtime manager.
 * In charge of starting and stopping WOPE runtimes on demand.
 * @author ggrousset
 */
public class WOPERuntimeManager {
    
    private List<WOPERuntime> runtimes = new ArrayList<WOPERuntime>();
    
    /**
     * Start a runtime.
     * If the runtime is already running, return it directly.
     * @param name Runtime name
     * @return The started runtime
     */
    public WOPERuntime startRuntime(String name) {
        return null;
    }
    
    /**
     * Stop a runtime.
     * The runtime in only actually stopped if it is no used anymore.
     * @param name Runtime name
     */
    public void stopRuntime(String name) {
        
    }
    
    /**
     * Force stopping of all running WOPE runtimes.
     */
    public void shutdownAllRuntimes() {
        
    }
}
