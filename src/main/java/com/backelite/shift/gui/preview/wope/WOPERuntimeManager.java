package com.backelite.shift.gui.preview.wope;

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
