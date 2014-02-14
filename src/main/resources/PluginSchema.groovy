/*
 * #%L
 * PluginSchema.groovy - Shift - 2013
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
import groovytools.builder.MetaBuilder

def metaBuilder = new MetaBuilder(getClass().getClassLoader())
			
metaBuilder.define {
    plugin(factory: com.backelite.shift.plugin.Plugin) {
        properties {
            uid(req: true)
            name(req: true)
            description(req: false)
            author(req:false)
            versionCode(req: true)
            versionName(req: true)
            lifecycle(req: false, schema: 'lifecycle')
        }
        collections {
            editorFactories {
                editorFactory(schema: 'editorFactory')
            }
            previewFactories {
                previewFactory(schema: 'previewFactory')
            }
            projectWizardFactories {
                projectWizardFactory(schema: 'projectWizardFactory')
            }
        }
    }
    
    editorFactory(factory: com.backelite.shift.plugin.GroovyEditorFactory) {
        properties {
            name(req: true)
            description(req: true)
            supportedExtensions(req: true)
            code(req: true)
        }
    }
    
    previewFactory(factory: com.backelite.shift.plugin.GroovyPreviewFactory) {
        properties {
            name(req: true)
            description(req: true)
            supportedExtensions(req: true)
            code(req: true)
        }
    }
    
    projectWizardFactory(factory: com.backelite.shift.plugin.GroovyProjectWizardFactory) {
        properties {
            name(req: true)
            description(req: true)
            code(req: true)
            projectGenerator(req: true, schema: 'projectGenerator')
        }
    }
    
    projectGenerator(factory: com.backelite.shift.plugin.GroovyProjectGenerator) {
        properties {
            code(req: true)
        }
    }
    
    lifecycle(factory: com.backelite.shift.plugin.GroovyPluginLifecycle) {
        properties {
            onLoad(req: false)
            onUnLoad(req: false)
        }
    }
}

return metaBuilder