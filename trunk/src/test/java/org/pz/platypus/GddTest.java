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

import java.util.logging.Level;

import static org.junit.Assert.*;

/**
 * Unit tests for GDD
 */

public class GddTest
{
    private Literals lits;
    private GDD gdd;

    @Before
    public void setUp()
    {
        MockLiterals mockLits = new MockLiterals( "Platypus.properties" );
        mockLits. setGetLitShouldReturnValue( true );
        lits = mockLits;
        gdd = new GDD();
        gdd.setLits( lits );
        gdd.setupLogger( ( "org.pz.platypus.Platypus" ));
        gdd.getLogger().setLevel( Level.OFF );
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
}

