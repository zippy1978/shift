/*
 * #%L
 * BuiltinPlugin.groovy - Shift - 2013
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
import javafx.scene.Node
import org.shiftedit.ApplicationContext
import org.shiftedit.gui.editor.CodeEditorController
import org.shiftedit.preferences.PreferencesManager
import org.shiftedit.workspace.artifact.FileSystemProject
import org.shiftedit.workspace.artifact.Project
import org.shiftedit.workspace.artifact.Document
import org.shiftedit.workspace.artifact.Folder
import org.shiftedit.util.FileUtils
import org.shiftedit.gui.preview.wope.WOPERuntimeManager

plugin {
    uid = "org.shiftedit.plugin.experimentalbuiltin"
    name = "Experimental Builtin plugin"
    versionCode = ${versionCode}
    versionName = "${versionName}"
    
    lifecycle {
        onLoad = {
            
        }
        
        onUnload = {
            
            WOPERuntimeManager.getInstance().shutdownAllRuntimes()
        }
    }
    
    previewFactories {
        
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
    
    preferencesPanelFactories {
        
        // WOPE panel
        preferencesPanelFactory {
            path = "a/b/c/d/wope"
            code = {loader ->
                Node node = (Node) loader.load(getClass().getResourceAsStream("/fxml/wope_preferences_panel.fxml"))
                return node
            }
        }
        
    }
    
}
