/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.command;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.GDD;
import org.pz.platypus.exceptions.EolTreatmentException;

/**
 * Test End of line treatment processing
 *
 * @author alb
 */
public class EolTreatmentTest
{
    private EolTreatment et;
    private GDD gdd;

    @Before
    public void setUp()
    {
        gdd = new GDD();
        gdd.initialize();
        et = new EolTreatment();
    }

    @Test
    public void testIsValid()
    {
        assertEquals( true, et.isValid( "soft" ));
        assertEquals( true, et.isValid( "hard" ));
        assertEquals( false, et.isValid( "not valid" ));
        assertEquals( false, et.isValid( null ));
    }

    @Test
    public void testValidToInteger()
    {
        try {
            assertEquals( 's', et.toInteger( "soft" ));
            assertEquals( 'h', et.toInteger( "hard" ));
            assertEquals( false, ( 'h' == et.toInteger( "soft" )));
        }
        catch( EolTreatmentException eolte ) {
            assertEquals( "threw EolTreatment exception when not expected", 1, 2 );
        }
    }

    @Test (expected=EolTreatmentException.class)
    public void testInvalidToIntegerNull() throws EolTreatmentException

    {
        et.toInteger( null );
    }

    @Test
    public void testIsSoftValidTrue()
    {
        assertTrue( et.isSoft( EolTreatment.SOFT ));
    }

    @Test
    public void testIsSoftValidFalse()
    {
        assertFalse( et.isSoft( EolTreatment.HARD ));
    }

    @Test
    public void testIsSoftInvalid()
    {
        assertFalse( et.isSoft( ' ' ));
    }
}