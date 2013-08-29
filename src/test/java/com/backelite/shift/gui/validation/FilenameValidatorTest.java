/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.backelite.shift.gui.validation;

/*
 * #%L
 * FilenameValidatorTest.java - shift - 2013
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

import com.backelite.shift.gui.validation.FilenameValidator;
import com.backelite.shift.gui.validation.ValidatorResult;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ggrousset
 */
public class FilenameValidatorTest {
    
    public FilenameValidatorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @org.junit.Test
    public void testValidate() {
        Object input = "valid_name.ext";
        FilenameValidator instance = new FilenameValidator();
        ValidatorResult result = instance.validate(input);
        assertEquals(true, result.isValid());
    }
    
    @org.junit.Test
    public void testValidateWithSemiColons() {
        Object input = "invalid:name.ext";
        FilenameValidator instance = new FilenameValidator();
        ValidatorResult result = instance.validate(input);
        assertEquals(false, result.isValid());
    }
    
    @org.junit.Test
    public void testValidateWithSlash() {
        Object input = "invalid/name.ext";
        FilenameValidator instance = new FilenameValidator();
        ValidatorResult result = instance.validate(input);
        assertEquals(false, result.isValid());
    }
    
    @org.junit.Test
    public void testValidateWithAntiSlash() {
        Object input = "invalid\\name.ext";
        FilenameValidator instance = new FilenameValidator();
        ValidatorResult result = instance.validate(input);
        assertEquals(false, result.isValid());
    }
    
    @org.junit.Test
    public void testValidateWithStar() {
        Object input = "invalid*name.ext";
        FilenameValidator instance = new FilenameValidator();
        ValidatorResult result = instance.validate(input);
        assertEquals(false, result.isValid());
    }
}