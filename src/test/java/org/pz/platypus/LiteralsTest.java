/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2011 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.test.mocks.MockLiterals;

import java.util.logging.Level;

public class LiteralsTest
{
    private Literals lits;

    @Before
    public void setUp()
    {
        MockLiterals mockLits = new MockLiterals( "Platypus.properties" );
        mockLits. setGetLitShouldReturnValue( true );
        lits = mockLits;
     //   lits = new Literals( "Platypus" );
        GDD gdd = new GDD();
        gdd.setLits( lits );
        gdd.setupLogger( ( "org.pz.platypus.Platypus" ));
        gdd.getLogger().setLevel( Level.OFF );
    }

    @Test
    public void testGetLiterals()
    {
        assertNotNull( lits );
    }

    @Test
    public void testHandLoadingOfLiterals()
    {
        lits.loadLine( "MANUALLY_ADDED_ENTRY=by hand" );
        assertEquals( "by hand", lits.getLit( "MANUALLY_ADDED_ENTRY" ));
    }

    @Test
    public void testGetExistingLiteralWrongCase()
    {
        String testStr = lits.getLit( "error.colon" );
        assertEquals( testStr, " " );
    }

    @Test
    public void testGetNonExistentLiteral()
    {
        String testStr = lits.getLit( "(((&*$%##(((((" );
        assertEquals( testStr, " " );
    }
}

