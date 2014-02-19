package com.backelite.shift.gui.preview.wope;

import com.backelite.shift.ApplicationContext;
import com.backelite.shift.util.FileUtils;
import com.backelite.shift.util.NetworkUtils;
import com.backelite.shift.workspace.HTTPWorkspaceProxyServer;
import java.io.File;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.LoggerFactory;

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
 *
 * @author ggrousset
 */
public class WOPERuntime {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(WOPERuntime.class);
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
     * Holds the number of 'started' method calls.
     */
    private int startedCount = 0;

    /**
     * HTTP server instance.
     */
    private Server server;

    public WOPERuntime(String name, String path) {
        this.name = name;
        this.path = path;
    }
    
    /**
     * Starts the current instance.
     */
    protected void start() {

        if (!started) {

            try {
                
                started = true;

                // Start Workspace HTTP server (if not done yet)
                HTTPWorkspaceProxyServer workspaceServer = ApplicationContext.getHTTPWorkspaceProxyServer();
                workspaceServer.start();

                // Find free port to start the server on ...
                port = NetworkUtils.findAvailablePort("localhost", workspaceServer.getPort() + 1, workspaceServer.getPort() + 1001);

                // Get wope.ini file
                System.setProperty("wopeConfig", WOPERuntimeManager.getInstance().getInitFilePath());

                
                // FIXME : fucking issue here
                // Seems that Spring 3.2 (used for WOPE) does not support Java 8
                // See : https://jira.springsource.org/browse/SPR-10292?page=com.atlassian.jira.plugin.system.issuetabpanels:all-tabpanel
                
                // Start HTTP server
                server = new Server(port);
                WebAppContext webApp = new WebAppContext();
                webApp.setContextPath("/");
                webApp.setWar(this.path);
                server.setHandler(webApp);

                server.start();

                log.debug(String.format("Starting WOPE runtime %s (port %d)", this.name, this.port));

            } catch (Exception ex) {
                log.error("Failed to start remote HTTP preview server", ex);
            }
        }

        startedCount++;
    }

    /**
     * Stops the current instance.
     */
    protected void stop() {

        if (startedCount > 0) {
            startedCount--;
        }

        // Shutdown on last release
        if (started && startedCount == 0) {

            this.shutdown();
        }

    }
    
    protected void shutdown() {
        
        if (started) {
            if (server != null) {
                try {
                    log.debug(String.format("Stopping WOPE runtime %s", this.name));
                    server.stop();
                    started = false;
                    server = null;
                } catch (Exception ex) {
                    log.error("Failed to stop WOPE runtime", ex);
                }
            }
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
