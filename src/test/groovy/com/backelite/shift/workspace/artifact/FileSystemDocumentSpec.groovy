/*
 * #%L
 * FileSystemDocumentSpec.groovy - Shift - 2013
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
package com.backelite.shift.workspace.artifact

import spock.lang.*
import com.github.goldin.spock.extensions.tempdir.*

/**
 *
 * @author ggrousset
 */
class FileSystemDocumentSpec extends Specification{
    
    @Shared @TempDir File projectDir
    @Shared Project project
    @Shared File textFile
    @Shared File binFile
    @Shared Document textDocument
    @Shared Document binDocument
    
    def setupSpec() {
        
        // Setup test project and load it
        textFile = new File(projectDir, '001.txt')
        textFile.withWriter{it << 'File content'} 
        binFile = new File(projectDir, '002.png')
        binFile.withOutputStream(){it << this.getClass().getResourceAsStream('/images/icon_512x512.png')}
        project = new FileSystemProject(projectDir)
        project.load()
        textDocument = project.getDocuments().get(0)
        binDocument = project.getDocuments().get(1)
    }
     
    def "content is loaded on opening"() {
        
        when:
        textDocument.open()
        
        then:
        textDocument.getContentAsString() == 'File content'
        
        cleanup:
        textDocument.close()
        
    }
    
    def "modification detected when content is updated"() {
        
        when:
        textDocument.open()
        textDocument.setContentAsString('Modified')
        
        then:
        textDocument.isModified()
        
        cleanup:
        textDocument.close()
    }
}

