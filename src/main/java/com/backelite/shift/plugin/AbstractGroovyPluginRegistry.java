package com.backelite.shift.plugin;

/*
 * #%L
 * AbstractGroovyPluginRegistryImpl.java - shift - 2013
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

    public void loadPlugins() throws PluginException {
        
        // Load built in plugin
        this.loadPlugin(FileUtils.getFileContentAsStringFromClasspathResource("/BuiltinPlugin.groovy"));
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
