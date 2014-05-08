package org.shiftedit.gui.preview.wope;

/*
 * #%L
 * WOPERuntimeManager.java - Shift - 2013
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



import org.shiftedit.ApplicationContext;
import org.shiftedit.task.LocalTaskManager;
import org.shiftedit.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;

/**
 * WOPE runtime manager.
 * In charge of starting and stopping WOPE runtimes on demand.
 * @author ggrousset
 */
public class WOPERuntimeManager {
    
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(WOPERuntimeManager.class);
    
    /**
     * WOPE licence path.
     */
    private String licensePath;
    
    private static WOPERuntimeManager INSTANCE;
    
    private List<WOPERuntime> runtimes = new ArrayList<>();
    
    public static synchronized WOPERuntimeManager getInstance() {
        
        if (INSTANCE == null) {
            INSTANCE = new WOPERuntimeManager();
            
            // Load preferences
            INSTANCE.loadPreferences();
        }
        
        return INSTANCE;
    }
    
    private void loadPreferences() {
    
        // License
        this.licensePath = (String)ApplicationContext.getPreferencesManager().getValue("preview.wope.licensePath");
        
        // Runtimes
        List<Map<String, Object>> list = (List<Map<String, Object>>) ApplicationContext.getPreferencesManager().getValue("preview.wope.runtimes");
        if (list != null) {
            for(Map<String, Object> entry : list) {
                WOPERuntime runtime = new WOPERuntime((String)entry.get("name"), (String)entry.get("path"));
                this.getRuntimes().add(runtime);
            }
        }
    }
    
    public WOPERuntime getRuntimeByName(String name) {
        
        Optional<WOPERuntime> result = getRuntimes().stream().filter(r -> r.getName().equals(name)).findFirst();
        if (result.isPresent()) {
            return result.get();
        } else {
            return null;
        }
        
    }
    
    /**
     * Start a runtime.
     * If the runtime is already running, return it directly.
     * @param name Runtime name
     * @return The started runtime
     */
    public WOPERuntime startRuntime(String name) {
        
        WOPERuntime runtime = this.getRuntimeByName(name);
        return startRuntime(runtime);
    }
    
    /**
     * Start runtime.
     * If the runtime is already running, return it directly.
     * @param runtime Runtime
     * @return The started runtime
     */
    public WOPERuntime startRuntime(WOPERuntime runtime) {
        
        if (runtime != null) {
            runtime.start();
        }
        
        return runtime;
    }
    
    /**
     * Stop a runtime.
     * The runtime in only actually stopped if it is no used anymore.
     * @param name Runtime name
     */
    public void stopRuntime(String name) {
        
        WOPERuntime runtime = this.getRuntimeByName(name);
        this.stopRuntime(runtime);
        
    }
    
    /**
     * Stop a runtime.
     * The runtime in only actually stopped if it is no used anymore.
     * @param runtime Runtime
     */
    public void stopRuntime(WOPERuntime runtime) {
        
        if (runtime != null) {
            runtime.stop();
        }
        
    }
    
    /**
     * Force stopping of all running WOPE runtimes.
     */
    public void shutdownAllRuntimes() {
        
        // Shutdown all
        for(WOPERuntime runtime : runtimes) {
            runtime.shutdown();
        }
        
        // Delete ini file
        File initFile = new File(this.getInitFilePath());
        initFile.deleteOnExit();
        
    }
    
    /**
     * Get wope.ini file path.
     * File is generated on the fly if it does not exist.
     *
     * @return File path of the generated ini file
     */
    public synchronized String getInitFilePath() {

        File initFile = new File(System.getProperty("java.io.tmpdir") + File.separator + "shift-wope.ini");
        
        if (!initFile.exists()) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("license.file.url=file://%s\n", this.getLicensePath()));
            sb.append("\n");
            sb.append("[default]\n");
            sb.append("target.protocol=http\n");
            sb.append("target.host=localhost\n");
            sb.append(String.format("target.port=%s\n", ApplicationContext.getHTTPWorkspaceProxyServer().getPort()));
            sb.append("target.contextPath=\n");
            sb.append("target.debug=true\n");
            sb.append("template.cache=off\n");
            try {
                FileUtils.saveContentToFile(sb.toString().getBytes(), initFile);
            } catch (IOException ex) {
                log.error(ex.getMessage(), ex);
            }
            
        }
        
        return initFile.getAbsolutePath();
    }

    /**
     * @return the licensePath
     */
    public String getLicensePath() {
        return licensePath;
    }

    /**
     * @return the runtimes
     */
    public List<WOPERuntime> getRuntimes() {
        return runtimes;
    }

}
