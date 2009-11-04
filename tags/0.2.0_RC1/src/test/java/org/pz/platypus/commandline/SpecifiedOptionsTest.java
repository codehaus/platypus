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
public class SpecifiedOptionsTest
{
    private SpecifiedOptions so;
    public SpecifiedOptionsTest() {
    }

    @Before
    public void setUp()
    {
        so = new SpecifiedOptions();
    }

    @Test
    public void testConstructor()
    {
        assertFalse( so.containsArg( "-config" ));
    }

    @Test
    public void testInvalidAddNull()
    {
        so.add( null, null );
        assertEquals( Status.INVALID_PARAM_NULL, so.add( null, "xyz" ));
    }

    @Test
    public void testValidAdd()
    {
        final String vo = "-validOption";
        final String va = "validArg";

        assertEquals( Status.OK, so.add( vo, va ));
        assertTrue( so.containsArg( vo ));
        assertTrue( so.getArg( vo ).equals( va ));
    }
}