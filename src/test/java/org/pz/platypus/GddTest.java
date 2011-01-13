/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2011 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.test.mocks.MockLiterals;
import org.pz.platypus.test.mocks.MockLogger;

import java.util.Map;
import java.util.logging.Level;

import static org.junit.Assert.*;

/**
 * Unit tests for GDD
 */

public class GddTest
{
    private Literals lits;
    private MockLogger mockLog;
    private GDD gdd;

    @Before
    public void setUp()
    {
        MockLiterals mockLits = new MockLiterals( "Platypus.properties" );
        mockLits. setGetLitShouldReturnValue( true );
        lits = mockLits;

        gdd = new GDD();
        gdd.setLits( lits );
        gdd.setupLogger("org.pz.platypus.Platypus");
        mockLog = new MockLogger();
        gdd.setLogger( mockLog );
    }

    @Test
    public void verifyConstructor()
    {
        assertEquals( "Commands.properties", gdd.getCommandPropertyFilename() );
    }

    @Test
    public void validLoadofRgbColors()
    {
        UserStrings us = new UserStrings();
        assertEquals( 0, us.getSize() );
        gdd.loadRgbColorsIntoUserStrings( us );
        assertEquals( 17, us.getSize() );
    }

    @Test
    public void validColorsLoadedDuringInitialize()
    {
        UserStrings us = gdd.getUserStrings();
        assertEquals( "128,128,0", us.getString( "OLIVE" ));
    }

    @Test
    public void testLogFine()
    {
        String msg = "allo, fine";
        mockLog.setLevel( Level.FINE );
        gdd.logFine( msg );
        assertTrue( mockLog.getMessage().endsWith( msg ));
    }

    @Test
    public void testLogFiner()
    {
        String msg = "allo, finer";
        mockLog.setLevel( Level.FINER );
        gdd.logFine( msg );
        assertTrue( mockLog.getMessage().endsWith( msg ));
    }

    @Test
    public void testLogFinest()
    {
        String msg = "allo, finest";
        mockLog.setLevel( Level.FINEST );
        gdd.logFine( msg );
        assertTrue( mockLog.getMessage().endsWith( msg ));
    }

    @Test
    public void testGetEnv()
    {
        Map< String,String > env = gdd.getUserEnv();
        assertNotNull( env );
    }
}

