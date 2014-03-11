/*
 * #%L
 * BasePreferencesManagerSpec.groovy - Shift - 2013
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
package com.backelite.shift.preferences

import com.backelite.shift.preferences.BasePreferencesManager
import spock.lang.Specification

/**
 * Concrete test implementation for BasePreferencesManager.
 **/
class NullPreferencesManager extends BasePreferencesManager {
    
    public void commit() {
        
    }
    
    public void rollback() {
        
    }
    
    public void load() {
        
    }
    
}

/**
 *
 * @author ggrousset
 */
class BasePreferencesManagerSpec extends Specification {
	
    def "merge list values"() {
        
        setup:
        def manager = new NullPreferencesManager()
        manager.setInitialValue('test.key', ['one', 'two', 'three'])
        
        expect:
        manager.mergeListValue('test.key', input)
        manager.getValue('test.key') == result

        where:
        input                   | result
        ['four', 'five', 'six'] | ['one', 'two', 'three', 'four', 'five', 'six']
        []                      | ['one', 'two', 'three']

    }
    
    def "merge map values"() {
        
        setup:
        def manager = new NullPreferencesManager()
        manager.setInitialValue('test.key', ['one' : '1', 'two' : '2', 'three': '3'])
        
        expect:
        manager.mergeMapValue('test.key', input)
        manager.getValue('test.key') == result
        
        where:
        input                                     | result
        ['four' : '4', 'five' : '5', 'six' : '6'] | ['one' : '1', 'two' : '2', 'three' : '3', 'four' : '4', 'five' : '5', 'six' : '6']
        [:]                                        | ['one' : '1', 'two' : '2', 'three' : '3']
    }
    
}

