/*
 * #%L
 * BuiltinPlugin.groovy - Shift - 2013
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
import javafx.scene.Node
import com.backelite.shift.ApplicationContext
import com.backelite.shift.gui.editor.CodeEditorController
import com.backelite.shift.gui.preview.HTMLPreviewController
import com.backelite.shift.preferences.PreferencesManager
import com.backelite.shift.workspace.artifact.FileSystemProject

plugin {
    uid = "com.backelite.shift.plugin.builtin"
    name = "Builtin plugin"
    versionCode = ${versionCode}
    versionName = "${versionName}"
    
    lifecycle {
        onLoad = {
            
            PreferencesManager preferencesManager = ApplicationContext.getPreferencesManager()
            
            // HTML Preview presets
            preferencesManager.setInitialValues([
                    'preview.html.presets' : [
                        ['name' : 'Apple iPad Retina (1024x768@2x)',
                        'width' : 1024,
                        'height' : 768,
                        'pixelRatio' : 2,
                        'userAgent' : 'Mozilla/5.0 (iPad; CPU OS 5_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9B176 Safari/7534.48.3'],
                        ['name' : 'Apple iPhone 4 (320x480@2x)',
                        'width' : 320,
                        'height' : 480,
                        'pixelRatio' : 2,
                        'userAgent' : 'Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_0 like Mac OS X; en-us) AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8A293 Safari/6531.22.7'],
                        ['name' : 'Apple iPhone 5 (320x568@2x)',
                        'width' : 320,
                        'height' : 568,
                        'pixelRatio' : 2,
                        'userAgent' : 'Mozilla/5.0 (iPhone; CPU iPhone OS 6_0_2 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A551 Safari/8536.25'],
                        ['name' : 'LG Nexus 4 (384x640@2x)',
                        'width' : 384,
                        'height' : 640,
                        'pixelRatio' : 2,
                        'userAgent' : 'Mozilla/5.0 (Linux; Android 4.2; Nexus 4 Build/JVP15Q) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19']
                        
                    ]
            ])
        
            preferencesManager.commit()
        }
    }
    
    editorFactories {
        
        // Generic text editor
        editorFactory {
            name = "Generic text editor"
            description = "Builtin Text editor"
            supportedExtensions = ['*']
            code = {document, loader ->   
                Node node = (Node) loader.load(getClass().getResourceAsStream("/fxml/code_editor.fxml"))
                CodeEditorController controller = (CodeEditorController) loader.getController()
                controller.setDocument(document)
                return node
            }
        }
        
        // HTML editor
        editorFactory {
            name = "HTML editor"
            description = "Builtin HTML editor"
            supportedExtensions = ['html']
            code = {document, loader ->   
                Node node = (Node) loader.load(getClass().getResourceAsStream("/fxml/code_editor.fxml"))
                CodeEditorController controller = (CodeEditorController) loader.getController()
                controller.setDocument(document)
                controller.setMode(CodeEditorController.Mode.HTML)
                return node
            }
        }
        
        // Javascript editor
        editorFactory {
            name = "JavaScript editor"
            description = "Builtin JavaScript editor"
            supportedExtensions = ['js','json']
            code = {document, loader ->   
                Node node = (Node) loader.load(getClass().getResourceAsStream("/fxml/code_editor.fxml"))
                CodeEditorController controller = (CodeEditorController) loader.getController()
                controller.setDocument(document)
                controller.setMode(CodeEditorController.Mode.JAVASCRIPT)
                return node
            }
        }
        
        // CSS editor
        editorFactory {
            name = "CSS editor"
            description = "Builtin CSS editor"
            supportedExtensions = ['css']
            code = {document, loader ->   
                Node node = (Node) loader.load(getClass().getResourceAsStream("/fxml/code_editor.fxml"))
                CodeEditorController controller = (CodeEditorController) loader.getController()
                controller.setDocument(document)
                controller.setMode(CodeEditorController.Mode.CSS)
                return node
            }
        }
        
        // Markdown editor
        editorFactory {
            name = "Markdown editor"
            description = "Builtin Markdown editor"
            supportedExtensions = ['md', 'markdown']
            code = {document, loader ->   
                Node node = (Node) loader.load(getClass().getResourceAsStream("/fxml/code_editor.fxml"))
                CodeEditorController controller = (CodeEditorController) loader.getController()
                controller.setDocument(document)
                controller.setMode(CodeEditorController.Mode.MARKDOWN)
                return node
            }
        }
        
        // Groovy editor
        editorFactory {
            name = "Groovy editor"
            description = "Builtin Groovy editor"
            supportedExtensions = ['groovy']
            code = {document, loader ->   
                Node node = (Node) loader.load(getClass().getResourceAsStream("/fxml/code_editor.fxml"))
                CodeEditorController controller = (CodeEditorController) loader.getController()
                controller.setDocument(document)
                controller.setMode(CodeEditorController.Mode.GROOVY)
                return node
            }
        }
        
        // XML editor
        editorFactory {
            name = "XML editor"
            description = "Builtin XML editor"
            supportedExtensions = ['xml']
            code = {document, loader ->   
                Node node = (Node) loader.load(getClass().getResourceAsStream("/fxml/code_editor.fxml"))
                CodeEditorController controller = (CodeEditorController) loader.getController()
                controller.setDocument(document)
                controller.setMode(CodeEditorController.Mode.XML)
                return node
            }
        }
    }
    
    previewFactories {
        
        // HTML preview
        previewFactory {
            name = "HTML Preview"
            description = "Builtin HTML preview"
            supportedExtensions = ['html']
            code = {loader ->   
                Node node = (Node) loader.load(getClass().getResourceAsStream("/fxml/html_preview.fxml"))
                return node
            }
        }
        
        // Remote HTML preview
        previewFactory {
            name = "Remote HTML Preview"
            description = "Builtin Remote HTML preview"
            supportedExtensions = ['html']
            code = {loader ->   
                Node node = (Node) loader.load(getClass().getResourceAsStream("/fxml/remote_html_preview.fxml"))
                return node
            }
        }
    }
    
    projectWizardFactories {
        
        // HTML5 project
        projectWizardFactory {
            name = "HTML5 Project"
            description = "Builtin HTML5 project"
            code = {loader ->
                Node node = (Node) loader.load(getClass().getResourceAsStream("/fxml/basic_projectwizard.fxml"))
                return node
            }
            projectGenerator{
                code = {name, attributes ->   
                    // Project folder
                    File projectFolder = new File(attributes.location, name)
                    projectFolder.mkdirs();
                    
                    // index.html
                    File indexHtml = new File(projectFolder, "index.html")
                    indexHtml << "<html>"
                    indexHtml << "  <head>"
                    indexHtml << "      <title>$name</title>"
                    indexHtml << "  </head>"
                    indexHtml << "  <body>"
                    indexHtml << "      <p>Hello World !</p>"
                    indexHtml << "  </body>"
                    indexHtml << "</html>"

                    // Return project
                    return new FileSystemProject(projectFolder)
                }
            }
        }
    }
    
}