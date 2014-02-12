/*
 * #%L
 * LocalWorkspaceSpec.groovy - Shift - 2013
 * %%
 * Copyright (C) 2013 - 2014 Gilles Grousset
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

