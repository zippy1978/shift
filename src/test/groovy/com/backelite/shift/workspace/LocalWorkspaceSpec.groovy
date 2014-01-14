/*
 * #%L
 * LocalWorkspaceSpec.groovy - Shift - 2013
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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.backelite.shift.workspace

import spock.lang.*
import com.github.goldin.spock.extensions.tempdir.*
import com.backelite.shift.workspace.artifact.Project
import com.backelite.shift.workspace.artifact.FileSystemProject
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference
/**
 *
 * @author ggrousset
 */
class LocalWorkspaceSpec extends Specification {
	
    @Shared @TempDir File projectDir
    @Shared Project project
    @Shared File subDirFile
    @Shared File documentFile
    
    def setupSpec() {
        
        // Setup test project and load it
        subDirFile = new File(projectDir, 'subDir')
        subDirFile.mkdirs()
        documentFile = new File(subDirFile, 'document.txt')
        documentFile.withWriter{it << 'Test doc'} 
        project = new FileSystemProject(projectDir)
        
    }
    
    def "workspace is not leaking when closing project"() {
        
        when:
        LocalWorkspace workspace = new LocalWorkspace()
        FileSystemProject projectRef = new FileSystemProject(projectDir)
        projectRef.load()
        workspace.openProject(projectRef)
        workspace.closeProject(projectRef)
        ReferenceQueue queue = new ReferenceQueue()
        WeakReference ref = new WeakReference(projectRef, queue)
        projectRef = null
        System.gc()
        
        then:
        ref.isEnqueued()
    }
    
    def "find artifact by workspace path"() {
        
        when:
        LocalWorkspace workspace = new LocalWorkspace()
        FileSystemProject project = new FileSystemProject(projectDir)
        project.load()
        workspace.openProject(project)
        
        then:
        workspace.findArtifactByWorkspacePath("/${projectDir.name}/subDir/document.txt") != null
        workspace.findArtifactByWorkspacePath("/${projectDir.name}/subDir/notfound.txt") == null
    }
}

