/*
 * #%L
 * FileSystemFolderSpec.groovy - Shift - 2013
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
package org.shiftedit.workspace.artifact

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

