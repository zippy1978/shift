package com.backelite.shift.gui.preview.wope;

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
