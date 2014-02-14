package com.backelite.shift.plugin;

/*
 * #%L
 * AbstractGroovyPluginRegistryImpl.java - shift - 2013
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

import com.backelite.shift.util.FileUtils;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import groovytools.builder.MetaBuilder;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public abstract class AbstractGroovyPluginRegistry extends AbstractPluginRegistry {

    private static final Logger log = LoggerFactory.getLogger(AbstractGroovyPluginRegistry.class);
    
    private static MetaBuilder METABUILDER_INSTANCE;

    private synchronized MetaBuilder getMetaBuilder() {

        if (METABUILDER_INSTANCE == null) {

            // Creates the builder / schema from a Groovy file

            CompilerConfiguration config = new CompilerConfiguration();
            config.setScriptBaseClass(Script.class.getName());
            
            ImportCustomizer imports = new ImportCustomizer();
            config.addCompilationCustomizers(imports);

            GroovyShell shell = new GroovyShell(config);
            METABUILDER_INSTANCE = (MetaBuilder) shell.evaluate(FileUtils.getFileContentAsStringFromClasspathResource("/PluginSchema.groovy"));
        }

        return METABUILDER_INSTANCE;
    }

    @Override
    public void loadPlugins() throws PluginException {
        
        // Load built in plugin
        this.loadPlugin(FileUtils.getFileContentAsStringFromClasspathResource("/BuiltinPlugin.groovy"));
    }

    @Override
    public void unloadPlugins() throws PluginException {
        
        // Call unload on every plugin
        for (Plugin plugin : plugins) {
            if (plugin.getLifecycle() != null) {
                plugin.getLifecycle().unload();
            }
        }
        
    }
    
    

    protected void loadPlugin(String groovyScript) throws PluginException {

        // Run plugin script
        CompilerConfiguration config = new CompilerConfiguration();
        config.setScriptBaseClass(Script.class.getName());

        GroovyShell shell = new GroovyShell(config);
        Plugin plugin = (Plugin) this.getMetaBuilder().build(shell.parse(groovyScript));
        
        // Call load
        if (plugin.getLifecycle() != null) {
            plugin.getLifecycle().load();
        }
        
        // Add editors to extension map
        for (EditorFactory editorFactory : plugin.getEditorFactories()) {
            for (String ext : editorFactory.getSupportedExtensions()) {
                this.addEditorFactoryToExtension(ext, editorFactory);
            }
        }
        
        // Add previews to extention map
        for (PreviewFactory previewFactory : plugin.getPreviewFactories()) {
            for (String ext : previewFactory.getSupportedExtensions()) {
                this.addPreviewFactoryToExtension(ext, previewFactory);
            }
        }
        
        // Add plugin to list of loaded plugins
        plugins.add(plugin);
        
        log.debug(String.format("%s %s loaded. %d editor(s), %d preview(s), %d project wizard(s)", plugin.getName(), plugin.getVersionName(), plugin.getEditorFactories().size(), plugin.getPreviewFactories().size(), plugin.getProjectWizardFactories().size()));
        
    }

}
