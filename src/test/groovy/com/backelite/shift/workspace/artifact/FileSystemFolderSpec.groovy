/*
 * #%L
 * FileSystemFolderSpec.groovy - Shift - 2013
 * %%
 * Copyright (C) 2013 - 2014 Gilles Grousset
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
package com.backelite.shift.workspace.artifact

import spock.lang.*
import com.github.goldin.spock.extensions.tempdir.*

/**
 *
 * @author ggrousset
 */
class FileSystemFolderSpec extends Specification {
	
    @Shared @TempDir File projectDir
    @Shared Project project
    @Shared File folderDir
    @Shared Folder folder
    
    def setupSpec() {
        
        // Setup test project and load it
        folderDir = new File(projectDir, 'folder')
        folderDir.mkdir()
        project = new FileSystemProject(projectDir)
        project.load()
        folder = project.getSubFolders().get(0)
    }
    
    def "folder structure is updated on refresh when it changed outside the workspace"() {
        
        when:
        def subFolder = new File(folderDir, 'subfolder')
        subFolder.mkdir()
        folder.refresh()
        
        then:
        folder.getSubFolders().size() == 1
        
    }
}

