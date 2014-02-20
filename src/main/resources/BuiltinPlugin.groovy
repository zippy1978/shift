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
import com.backelite.shift.preferences.PreferencesManager
import com.backelite.shift.workspace.artifact.FileSystemProject
import com.backelite.shift.workspace.artifact.Project
import com.backelite.shift.workspace.artifact.Document
import com.backelite.shift.workspace.artifact.Folder
import com.backelite.shift.util.FileUtils
import com.backelite.shift.gui.preview.wope.WOPERuntimeManager

plugin {
    uid = "com.backelite.shift.plugin.builtin"
    name = "Builtin plugin"
    versionCode = ${versionCode}
    versionName = "${versionName}"
    
    lifecycle {
        onLoad = {
            
            PreferencesManager preferencesManager = ApplicationContext.getPreferencesManager()
            
            // HTML Preview presets
            // Source : http://en.wikipedia.org/wiki/List_of_displays_by_pixel_density
            preferencesManager.mergeListValue(
                    'preview.html.presets', [
                        ['name' : 'Apple iPad Retina (2048x1536@2x)',
                        'width' : 1024,
                        'height' : 768,
                        'pixelRatio' : 2,
                        'userAgent' : 'Mozilla/5.0 (iPad; CPU OS 5_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9B176 Safari/7534.48.3'],
                        ['name' : 'Apple iPhone 4 (640x960@2x)',
                        'width' : 320,
                        'height' : 480,
                        'pixelRatio' : 2,
                        'userAgent' : 'Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_0 like Mac OS X; en-us) AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8A293 Safari/6531.22.7'],
                        ['name' : 'Apple iPhone 5 (640x1136@2x)',
                        'width' : 320,
                        'height' : 568,
                        'pixelRatio' : 2,
                        'userAgent' : 'Mozilla/5.0 (iPhone; CPU iPhone OS 6_0_2 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A551 Safari/8536.25'],
                        ['name' : 'LG Nexus 4 (768x1280@2x)',
                        'width' : 384,
                        'height' : 640,
                        'pixelRatio' : 2,
                        'userAgent' : 'Mozilla/5.0 (Linux; Android 4.2; Nexus 4 Build/JVP15Q) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19'],
                        ['name' : 'BlackBerry PlayBook (1024x600@1x)',
                        'width' : 1024,
                        'height' : 600,
                        'pixelRatio' : 1,
                        'userAgent' : 'Mozilla/5.0 (PlayBook; U; RIM Tablet OS 2.0.1; en-US) AppleWebKit/535.8+ (KHTML, like Gecko) Version/7.2.0.1 Safari/535.8+'],
                        ['name' : 'BlackBerry Z10 (768x1280@2x)',
                        'width' : 384,
                        'height' : 640,
                        'pixelRatio' : 2,
                        'userAgent' : 'Mozilla/5.0 (BB10; Touch) AppleWebKit/537.10+ (KHTML, like Gecko) Version/10.0.9.2372 Mobile Safari/537.10+'],
                        ['name' : 'BlackBerry Bold 9900 (480x640@1x)',
                        'width' : 480,
                        'height' : 640,
                        'pixelRatio' : 1,
                        'userAgent' : 'Mozilla/5.0 (BlackBerry; U; BlackBerry 9900; en-US) AppleWebKit/534.11+ (KHTML, like Gecko) Version/7.0.0 Mobile Safari/534.11+'],
                        ['name' : 'HTC Wildfire (240x320@1x)',
                        'width' : 240,
                        'height' : 320,
                        'pixelRatio' : 1,
                        'userAgent' : 'Mozilla/5.0 (Linux; U; Android 2.3.3; en-us; HTC Wildfire S A510e Build/GRI40) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1'],
                        ['name' : 'HTC One X (720x1280@2x)',
                        'width' : 360,
                        'height' : 640,
                        'pixelRatio' : 2,
                        'userAgent' : 'Mozilla/5.0 (Linux; Android 4.0.3; HTC One X Build/IML74K) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19'],
                        ['name' : 'HTC Desire (480x800@1x)',
                        'width' : 480,
                        'height' : 800,
                        'pixelRatio' : 1,
                        'userAgent' : 'Mozilla/5.0 (Linux; U; Android 2.2; en-us; Desire_A8181 Build/ERE27) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17'],
                        ['name' : 'LG Optimus G (768x1280@2x)',
                        'width' : 384,
                        'height' : 640,
                        'pixelRatio' : 2,
                        'userAgent' : 'Mozilla/5.0 (Linux; Android 4.0; LG-E975 Build/IMM76L) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19'],
                        ['name' : 'Nokia N95 (240x320@1x)',
                        'width' : 240,
                        'height' : 320,
                        'pixelRatio' : 1,
                        'userAgent' : 'Mozilla/5.0 (SymbianOS/9.2; U; Series60/3.1 NokiaN95/10.0.010; Profile/MIDP-2.0 Configuration/CLDC-1.1 ) AppleWebKit/413 (KHTML, like Gecko) Safari/413 (383; MSIE 7.0; Windows NT 5.1; .NET CLR 2.0.507'],
                        ['name' : 'Samsung Galaxy Ace (320x480@1x)',
                        'width' : 320,
                        'height' : 480,
                        'pixelRatio' : 1,
                        'userAgent' : 'Mozilla/5.0 (Linux U Android 2.2 en-us GT-S5830 Build/FROYO) AppleWebKit/533.1(KHTML, like Gecko) Version/4.0 Mobile Safari/533.1'],
                        ['name' : 'Samsung Galaxy S II (480x800@1.5x)',
                        'width' : 320,
                        'height' : 534,
                        'pixelRatio' : 1.5,
                        'userAgent' : 'Mozilla/5.0 (Linux; U; Android 2.3; en-us; GT-I9100 Build/GRH78) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1'],
                        ['name' : 'Samsung Galaxy S III (720x1280@2x)',
                        'width' : 360,
                        'height' : 640,
                        'pixelRatio' : 2,
                        'userAgent' : 'Mozilla/5.0 (Linux; U; Android 4.0.4; en-us; GT-I9300 Build/IMM76D) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30'],
                        ['name' : 'Samsung Galaxy S4 (1080x1920@3x)',
                        'width' : 360,
                        'height' : 640,
                        'pixelRatio' : 3,
                        'userAgent' : 'Mozilla/5.0 (Linux; Android 4.2.2; en-us; SAMSUNG GT-I9195 Build/JDQ39) AppleWebKit/535.19 (KHTML, like Gecko) Version/1.0 Chrome/18.0.1025.308 Mobile Safari/535.19'],
                        ['name' : 'Samsung Galaxy Note (800x1280@2x)',
                        'width' : 400,
                        'height' : 640,
                        'pixelRatio' : 2,
                        'userAgent' : 'Mozilla/5.0 (Linux; U; Android 2.3; xx-xx; GT-N7000 Build/GINGERBREAD) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1'],
                        ['name' : 'Samsung Galaxy Tab 10.1 (1280x800@1x)',
                        'width' : 1280,
                        'height' : 800,
                        'pixelRatio' : 1,
                        'userAgent' : 'Mozilla/5.0 (Linux; U; Android 3.0; xx-xx; GT-P7100 Build/HRI83) AppleWebkit/534.13 (KHTML, like Gecko) Version/4.0 Safari/534.13'],
                        ['name' : 'Samsung Nexus 10 (2560x1600@2x)',
                        'width' : 1280,
                        'height' : 800,
                        'pixelRatio' : 2,
                        'userAgent' : 'Mozilla/5.0 (Linux; U; Android 4.2; en-us; Nexus 10 Build/JOP12D) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Safari/534.30'],
                        ['name' : 'Sony Xperia Z (1080x1920@3x)',
                        'width' : 360,
                        'height' : 640,
                        'pixelRatio' : 3,
                        'userAgent' : 'Mozilla/5.0 (Linux; U; Android 4.1; en-us; SonyEricssonC6603 Build/10.1.A.0.182) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30'],
                        ['name' : 'Sony Ericsson W995 (240x320@1x)',
                        'width' : 240,
                        'height' : 320,
                        'pixelRatio' : 1,
                        'userAgent' : 'SonyEricssonW995/R1DB Browser/NetFront/3.4 Profile/MIDP-2.1 Configuration/CLDC-1.1 JavaPlatform/JP-8.4.1']
                    
                    
                    
                    
                        
                        
                        
                        
                    ].sort {a, b ->
                        a['name'] <=> b['name']
                    }
            )
        
            preferencesManager.commit()
        }
        
        onUnload = {
            
            WOPERuntimeManager.getInstance().shutdownAllRuntimes()
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
        
        // WOPE preview
        previewFactory {
            name = "WOPE Preview"
            description = "Builtin WOPE preview"
            supportedExtensions = ['html']
            code = {loader ->   
                Node node = (Node) loader.load(getClass().getResourceAsStream("/fxml/wope_preview.fxml"))
                return node
            }
        }
        
    }
    
    projectWizardFactories {
        
        // Initializr
        projectWizardFactory {
            name = "Initializr Project"
            description = "Builtin Initializr Project"
            code = {loader ->
                Node node = (Node) loader.load(getClass().getResourceAsStream("/fxml/initializr_projectwizard.fxml"))
                return node
            }
            projectGenerator{
                code = {name, attributes ->   
                    
                    // Create temp dir for download
                    File downloadDir = FileUtils.createUniqueDirectory(ApplicationContext.getApplicationTempDirectory())
                    
                    // Determine flavor URL
                    def address = 'http://www.initializr.com/builder?h5bp-content&modernizr&jquerymin&h5bp-iecond&h5bp-chromeframe&h5bp-analytics&h5bp-htaccess&h5bp-favicon&h5bp-appletouchicons&h5bp-scripts&h5bp-robots&h5bp-humans&h5bp-404&h5bp-adobecrossdomain&h5bp-css&h5bp-csshelpers&h5bp-mediaqueryprint&h5bp-mediaqueries'
                    if (attributes.flavor == 'Bootstrap') {
                        address = 'http://www.initializr.com/builder?boot-hero&jquerymin&h5bp-iecond&h5bp-chromeframe&h5bp-analytics&h5bp-favicon&h5bp-appletouchicons&modernizrrespond&izr-emptyscript&boot-css&boot-scripts'
                    }
                    
                    // Download package
                    def file = new File(downloadDir, 'initializr.zip')
                    def fos = new FileOutputStream(file)
                    def out = new BufferedOutputStream(fos)
                    out << new URL(address).openStream()
                    out.close()
                    
                    // Unzip
                    File unzipDir = new File(downloadDir, 'unzip')
                    unzipDir.mkdirs()
                    FileUtils.unzipFile(file, unzipDir)
                    
                    // Import unzipped project
                    Project project = ApplicationContext.getWorkspace().importProjectFromDirectory(new File(unzipDir, 'initializr'), attributes.location, name)
                    
                    // Delete download dir
                    FileUtils.deleteDirectory(downloadDir)
                    
                    return project
                }
            }
        }
        
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
                    
                    // Project
                    Project project = ApplicationContext.getWorkspace().createProject(attributes.location, name)
                    
                    
                    // index.html
                    Document indexDocument = project.createDocument("index.html")
                    indexDocument.setContentAsString("""\
<!DOCTYPE html>
<html>
    <head>
      <meta charset="utf-8">
      <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
      <title>$name</title>
      <meta name="description" content="">
      <meta name="viewport" content="width=device-width">
      <link rel="stylesheet" href="css/main.css">
    </head>
    <body>
      <h1>index.html</h1>
      <p>
        <img class="logo" src="img/logo.png"/>
      </p>
      <p>
        This is the index page of project <b>$name</b>. 
        A simple page to help you get started with your new project.
      </p>
      <p>
        For a more advanced project template consider using the Initializr Wizard.
      </p>
      <p class="bottom-line">Have fun !</p>
    </body>
</html>
""")
                    indexDocument.save()
                    
                    // css folder
                    Folder cssFolder = project.createSubFolder("css")
                    cssFolder.save()
                    
                    // css/main.css
                    Document mainCSSDocument = cssFolder.createDocument("main.css")
                    mainCSSDocument.setContentAsString("""\
html {
  font-family: 'Helvetica';
  font-size: 100%;
  -webkit-text-size-adjust: 100%;
  -ms-text-size-adjust: 100%;
}

h1 {
  font-size: 150%;
  text-align: center;
}

p {
    text-align: center;
}

.logo {
    width: 128px;
    height: 128px;
}

.bottom-line {
    font-weight: bold;
}
""")
                    mainCSSDocument.save()
                    
                    // img folder
                    Folder imgFolder = project.createSubFolder("img")
                    imgFolder.save()
                    
                    // img/logo.png
                    Document logoImage = imgFolder.createDocument('logo.png')
                    ByteArrayOutputStream os = new ByteArrayOutputStream()
                    os << ApplicationContext.class.getResourceAsStream('/images/icon_512x512.png')
                    logoImage.setContent(os.toByteArray())
                    logoImage.save()
                    
                    return project
                }
            }
        }
    }
    
}