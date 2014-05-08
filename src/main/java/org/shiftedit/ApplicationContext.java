package org.shiftedit;

import org.shiftedit.gui.theme.DefaultThemeManager;
import org.shiftedit.gui.theme.ThemeManager;
import org.shiftedit.plugin.LocalPluginRegistry;
import org.shiftedit.plugin.PluginException;
import org.shiftedit.plugin.PluginRegistry;
import org.shiftedit.preferences.LocalPreferencesManager;
import org.shiftedit.preferences.PreferencesException;
import org.shiftedit.preferences.PreferencesManager;
import org.shiftedit.state.LocalStateManager;
import org.shiftedit.state.StateManager;
import org.shiftedit.task.LocalTaskManager;
import org.shiftedit.task.TaskManager;
import org.shiftedit.workspace.HTTPWorkspaceProxyServer;
import org.shiftedit.workspace.LocalWorkspace;
import org.shiftedit.workspace.Workspace;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import javafx.application.HostServices;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * #%L
 * WorkspaceHolder.java - shift - 2013
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
/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class ApplicationContext {

    private static final Logger log = LoggerFactory.getLogger(ApplicationContext.class);
    private static Workspace WORKSPACE_INSTANCE;
    private static PluginRegistry PLUGIN_REGISTRY_INSTANCE;
    private static HTTPWorkspaceProxyServer HTTP_WORKSPACE_PROXY_SERVER_INSTANCE;
    private static StateManager STATE_MANAGER_INSTANCE;
    private static PreferencesManager PREFERENCES_MANAGER_INSTANCE;
    private static TaskManager TASK_MANAGER_INSTANCE;
    private static ThemeManager THEME_MANAGER_INSTANCE;
    private static Properties PROPERTIES_INSTANCE;
    private static HostServices HOST_SERVICES;
    private static boolean FIRST_LAUNCH = false;
    private static boolean UPDATED = false;
    
    /**
     * Holds the main stage.
     */
    private static Stage MAIN_STAGE;

    public static synchronized Workspace getWorkspace() {

        if (WORKSPACE_INSTANCE == null) {
            WORKSPACE_INSTANCE = new LocalWorkspace();
        }

        return WORKSPACE_INSTANCE;
    }

    public static synchronized PluginRegistry getPluginRegistry() {

        if (PLUGIN_REGISTRY_INSTANCE == null) {
            PLUGIN_REGISTRY_INSTANCE = new LocalPluginRegistry();
        }

        return PLUGIN_REGISTRY_INSTANCE;
    }

    public static synchronized HTTPWorkspaceProxyServer getHTTPWorkspaceProxyServer() {

        if (HTTP_WORKSPACE_PROXY_SERVER_INSTANCE == null) {
            HTTP_WORKSPACE_PROXY_SERVER_INSTANCE = new HTTPWorkspaceProxyServer(getWorkspace());
        }

        return HTTP_WORKSPACE_PROXY_SERVER_INSTANCE;
    }

    public static synchronized StateManager getStateManager() {

        if (STATE_MANAGER_INSTANCE == null) {
            STATE_MANAGER_INSTANCE = new LocalStateManager(getApplicationDataDirectory());
        }

        return STATE_MANAGER_INSTANCE;
    }

    public static synchronized PreferencesManager getPreferencesManager() {

        if (PREFERENCES_MANAGER_INSTANCE == null) {
            PREFERENCES_MANAGER_INSTANCE = new LocalPreferencesManager(getApplicationDataDirectory());
            
            // Read currentVersionCode property
            Integer lastVersionCode = (Integer)PREFERENCES_MANAGER_INSTANCE.getValue(Constants.PREFERENCES_KEY_CURRENT_VERSION_CODE);
            Integer newVersionCode = Integer.valueOf(getProperties().getProperty(Constants.PROPERTY_APPLICATION_VERSION_CODE));
            if (lastVersionCode == null || lastVersionCode != newVersionCode) {
                UPDATED = true;
            }
        }

        return PREFERENCES_MANAGER_INSTANCE;
    }

    public static synchronized TaskManager getTaskManager() {

        if (TASK_MANAGER_INSTANCE == null) {
            TASK_MANAGER_INSTANCE = new LocalTaskManager();
        }

        return TASK_MANAGER_INSTANCE;

    }

    public static synchronized ThemeManager getThemeManager() {

        if (THEME_MANAGER_INSTANCE == null) {
            THEME_MANAGER_INSTANCE = new DefaultThemeManager();
        }

        return THEME_MANAGER_INSTANCE;
    }

    /**
     * Destroy all conrext data.
     */
    public static void destroy() {

        log.debug("Destroying application context");
        try {
            // Save current applicaiton version code
            getPreferencesManager().setValue(Constants.PREFERENCES_KEY_CURRENT_VERSION_CODE, Integer.valueOf(getProperties().getProperty(Constants.PROPERTY_APPLICATION_VERSION_CODE)));
            getPreferencesManager().commit();
        } catch (PreferencesException ex) {
            log.error("Failed to persist preferences", ex);
        }

        WORKSPACE_INSTANCE = null;
        STATE_MANAGER_INSTANCE = null;
        PREFERENCES_MANAGER_INSTANCE = null;
        THEME_MANAGER_INSTANCE = null;
        HOST_SERVICES = null;
        
        if (PLUGIN_REGISTRY_INSTANCE != null) {
            try {
                PLUGIN_REGISTRY_INSTANCE.unloadPlugins();
            } catch (PluginException ex) {
                log.error("Failed to unload plugins");
            }
        }
        PLUGIN_REGISTRY_INSTANCE = null;

        if (TASK_MANAGER_INSTANCE != null) {
            TASK_MANAGER_INSTANCE.shutdown();
            TASK_MANAGER_INSTANCE = null;
        }

        MAIN_STAGE = null;

        if (HTTP_WORKSPACE_PROXY_SERVER_INSTANCE != null) {
            HTTP_WORKSPACE_PROXY_SERVER_INSTANCE.stop();
        }

        HTTP_WORKSPACE_PROXY_SERVER_INSTANCE = null;
    }

    /**
     * @return the mainStage
     */
    public static Stage getMainStage() {
        return MAIN_STAGE;
    }

    /**
     * @param aMainStage the mainStage to set
     */
    protected static void setMainStage(Stage aMainStage) {
        MAIN_STAGE = aMainStage;
    }
    
    public static HostServices getHostServices() {
        return HOST_SERVICES;
    }
    
    protected static void setHostServices(HostServices hostServices) {
        HOST_SERVICES = hostServices;
    }

    /**
     * Return the application data directory.
     * Creates the directory if it does not exist on first call.
     * @return Data dir
     */
    public static synchronized File getApplicationDataDirectory() {

        String userHome = System.getProperty("user.home");
        File applicationDataDirectory = new File(userHome, String.format(".%s", getProperties().getProperty(Constants.PROPERTY_APPLICATION_NAME).toLowerCase()));

        if (!applicationDataDirectory.exists()) {
            applicationDataDirectory.mkdirs();
            
            // First launch detected
            FIRST_LAUNCH = true;
        }

        return applicationDataDirectory;
    }
    
    /**
     * Return the application temp directory
     * Creates the directory if it does not exist on first call.
     * @return Temp dir
     */
    public static synchronized File getApplicationTempDirectory() {
        
        String tempDir = System.getProperty("java.io.tmpdir");
        File applicationTempDirectory = new File(tempDir, getProperties().getProperty(Constants.PROPERTY_APPLICATION_NAME).toLowerCase());

        if (!applicationTempDirectory.exists()) {
            applicationTempDirectory.mkdirs();;
        }
        
        return applicationTempDirectory;
    }

    public static synchronized Properties getProperties() {

        if (PROPERTIES_INSTANCE == null) {
            try {
                PROPERTIES_INSTANCE = new Properties();
                try (InputStream in = ApplicationContext.class.getResourceAsStream("/application.properties")) {
                    PROPERTIES_INSTANCE.load(in);
                }
            } catch (IOException ex) {
                log.error(String.format("Unable to read application.properties : %s", ex.getMessage()));
            }
        }

        return PROPERTIES_INSTANCE;
    }
    
    /**
     * Check if it's the first application launch.
     * @return true / false
     */
    public static boolean isFirstLaunch() {
        return FIRST_LAUNCH;
    }
    
    /**
     * Check if it's the first launch after an application update.
     * @return true / false
     */
    public static boolean isUpdated() {
        
        // Make sure preferences are loaded
        getPreferencesManager();
        
        return UPDATED;
    }
    /**
     * Check if the current application release is a SNAPSHOT.
     * A SNAPSHOT is a version number with 4 parts
     * @return true / false
     */
    public static boolean isSnapshotRelease() {
        String buildChannel = getProperties().getProperty(Constants.PROPERTY_APPLICATION_BUILD_CHANNEL);
        return buildChannel.equals("snapshots");
    }
}
