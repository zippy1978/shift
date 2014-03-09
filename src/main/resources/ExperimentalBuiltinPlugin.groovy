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
    uid = "com.backelite.shift.plugin.experimentalbuiltin"
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
    
}