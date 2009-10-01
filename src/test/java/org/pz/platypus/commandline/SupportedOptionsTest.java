/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.commandline;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.pz.platypus.*;

/**
 * @author alb
 */
public class SupportedOptionsTest 
{
    private SupportedOptions so;
    public SupportedOptionsTest() {
    }

    @Before
    public void setUp() 
    {
        so = new SupportedOptions();
    }
    
    @Test
    public void testConstructor()
    {
        assertTrue( so.containsArg( "-help" ));
        assertEquals( 1, so.getArgCount( "-config" ));
    }

    @Test
    public void testInvalidAddNull()
    {
        assertEquals( Status.INVALID_PARAM_NULL, so.add( null, 0 ));
    }

    @Test
    public void testValidAdd()
    {
        final String vo = "-validOption";
        
        assertEquals( Status.OK, so.add( vo, 2 ));
        assertTrue( so.containsArg( vo ));
        assertEquals( 2, so.getArgCount( vo ));
    }    
}