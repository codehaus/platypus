/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.commandTypes;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.*;
import org.pz.platypus.test.mocks.MockLiterals;

/**
 * Test values of alignment in [align: command.
 *
 * @author alb
 */
public class AlignmentTest
{
    private Alignment al;
    private GDD gdd;

    @Before
    public void setUp()
    {
        al = new Alignment();
        gdd = new GDD();
        gdd.initialize();
    }

    @Test
    public void testToStringWithNullGdd()
    {
        assertEquals( "Error: GDD not specified in class Alignment", al.toString( 'c', null ));
    }

    @Test
    public void testToStringWithValidAlignment()
    {
        MockLiterals mockLits = new MockLiterals( );
        mockLits.setGetLitShouldReturnKey( true );
        gdd.setLits( mockLits );

        assertEquals( "CENTER", al.toString( 'c', gdd ));
        assertEquals( "JUSTIFIED", al.toString( 'j', gdd ));
        assertEquals( "LEFT", al.toString( 'l', gdd ));
        assertEquals( "RIGHT", al.toString( 'r', gdd ));
    }

    @Test
    public void testToStringWithInvalidAlignment()
    {
        MockLiterals mockLits = new MockLiterals( );
        mockLits.setGetLitShouldReturnKey( true );
        gdd.setLits( mockLits );

        assertEquals( "INVALID", al.toString( 'C', gdd ));
        assertEquals( "INVALID", al.toString( 'x', gdd ));
        assertEquals( "INVALID", al.toString( ' ', gdd ));
        assertEquals( "INVALID", al.toString( -28345, gdd ));
    }    
}