/*
 * #%L
 * PluginSchema.groovy - Shift - 2013
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
import groovytools.builder.MetaBuilder

def metaBuilder = new MetaBuilder(getClass().getClassLoader())
			
metaBuilder.define {
    plugin(factory: org.shiftedit.plugin.Plugin) {
        properties {
            uid(req: true)
            name(req: true)
            description(req: false)
            author(req:false)
            versionCode(req: true)
            versionName(req: true)
            i18nBundle(req:false)
            lifecycle(req: false, schema: 'lifecycle')
        }
        collections {
            editorFactories {
                editorFactory(schema: 'editorFactory')
            }
            previewFactories {
                previewFactory(schema: 'previewFactory')
            }
            preferencesPanelFactories {
                preferencesPanelFactory(schema: 'preferencesPanelFactory')
            }
            projectWizardFactories {
                projectWizardFactory(schema: 'projectWizardFactory')
            }
        }
    }
    
    editorFactory(factory: org.shiftedit.plugin.GroovyEditorFactory) {
        properties {
            name(req: true)
            description(req: true)
            supportedExtensions(req: true)
            code(req: true)
        }
    }
    
    previewFactory(factory: org.shiftedit.plugin.GroovyPreviewFactory) {
        properties {
            name(req: true)
            description(req: true)
            supportedExtensions(req: true)
            code(req: true)
        }
    }
    
    preferencesPanelFactory(factory: org.shiftedit.plugin.GroovyPreferencesPanelFactory) {
        properties {
            path(req: true)
            code(req: true)
        }
    }
    
    projectWizardFactory(factory: org.shiftedit.plugin.GroovyProjectWizardFactory) {
        properties {
            name(req: true)
            description(req: true)
            code(req: true)
            projectGenerator(req: true, schema: 'projectGenerator')
        }
    }
    
    projectGenerator(factory: org.shiftedit.plugin.GroovyProjectGenerator) {
        properties {
            code(req: true)
        }
    }
    
    lifecycle(factory: org.shiftedit.plugin.GroovyPluginLifecycle) {
        properties {
            onLoad(req: false)
            onUnload(req: false)
        }
    }
}

return metaBuilder
