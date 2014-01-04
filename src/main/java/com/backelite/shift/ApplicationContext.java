package com.backelite.shift;

import com.backelite.shift.gui.theme.DefaultThemeManager;
import com.backelite.shift.gui.theme.ThemeManager;
import com.backelite.shift.workspace.HTTPWorkspaceProxyServer;
import com.backelite.shift.workspace.LocalWorkspace;
import com.backelite.shift.workspace.Workspace;
import com.backelite.shift.plugin.LocalPluginRegistry;
import com.backelite.shift.plugin.PluginRegistry;
import com.backelite.shift.preferences.LocalPreferencesManager;
import com.backelite.shift.preferences.PreferencesManager;
import com.backelite.shift.state.LocalStateManager;
import com.backelite.shift.state.StateManager;
import com.backelite.shift.task.LocalTaskManager;
import com.backelite.shift.task.TaskManager;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
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

        WORKSPACE_INSTANCE = null;
        PLUGIN_REGISTRY_INSTANCE = null;
        STATE_MANAGER_INSTANCE = null;
        PREFERENCES_MANAGER_INSTANCE = null;
        THEME_MANAGER_INSTANCE = null;
        HOST_SERVICES = null;

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
     *
     * @return
     */
    public static File getApplicationDataDirectory() {

        String userHome = System.getProperty("user.home");
        File applicationDataDirectory = new File(userHome, ".shift");

        if (!applicationDataDirectory.exists()) {
            applicationDataDirectory.mkdirs();
            
            // First launch detected
            FIRST_LAUNCH = true;
        }

        return applicationDataDirectory;
    }

    public static synchronized Properties getProperties() {

        if (PROPERTIES_INSTANCE == null) {
            try {
                PROPERTIES_INSTANCE = new Properties();
                InputStream in = ApplicationContext.class.getResourceAsStream("/application.properties");
                PROPERTIES_INSTANCE.load(in);
                in.close();
            } catch (IOException ex) {
                log.error(String.format("Unable to read application.properties : %s", ex.getMessage()));
            }
        }

        return PROPERTIES_INSTANCE;
    }
    
    public static boolean isFirstLaunch() {
        return FIRST_LAUNCH;
    }
}
