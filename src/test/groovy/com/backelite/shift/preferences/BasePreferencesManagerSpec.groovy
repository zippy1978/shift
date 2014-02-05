/*
 * #%L
 * BasePreferencesManagerSpec.groovy - Shift - 2013
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
    
}

