/*
 * #%L
 * FileSystemDocumentSpec.groovy - Shift - 2013
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
    
    def "document content is updated on refresh when modified from outside the workspace"() {
        
        when:
        textDocument.open()
        textFile.withWriter{it << 'MODIFIED'}
        textDocument.refresh()
        
        then:
        textDocument.getContentAsString() == 'MODIFIED'
        
        cleanup:
        textDocument.close()
    }
}

