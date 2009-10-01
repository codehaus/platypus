/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Level;

public class LiteralsTest
{    
    private Literals lits;

    @Before
    public void setUp()
    {
     // lits = new MockLiterals( "Platypus.properties" );
        lits = new Literals( "Platypus" );
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
    public void testGetExistingLiteral()
    {
        String testStr = lits.getLit( "ERROR_COLON" );
        assertEquals( testStr, "Error:" );
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

