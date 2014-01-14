/*
 * #%L
 * FileSystemProjectSpec.groovy - Shift - 2013
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
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

/**
 *
 * @author ggrousset
 */
class FileSystemProjectSpec extends Specification {
	
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
    
    def "project is loaded"() {
        
        when:
        project.load()
        
        then:
        project.subFolders.size() == 1
        project.subFolders.get(0).documents.size() == 1
    }
    
    def "project is not leaking"() {
    
        when:
        FileSystemProject projectRef = new FileSystemProject(projectDir)
        projectRef.load()
        ReferenceQueue queue = new ReferenceQueue()
        WeakReference ref = new WeakReference(projectRef, queue)
        projectRef = null
        System.gc()
        
        then:
        ref.isEnqueued()

    }
}

