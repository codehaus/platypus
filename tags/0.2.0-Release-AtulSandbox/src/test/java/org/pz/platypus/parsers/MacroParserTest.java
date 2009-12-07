/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.parsers;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.*;
import org.pz.platypus.test.mocks.MockLiterals;
import org.pz.platypus.test.mocks.MockLogger;

/**
 * Test the macro parser
 *
 * @author alb
 */
public class MacroParserTest
{
    private GDD gdd;
    private MockLiterals lits;
    private MacroParser mp;

    @Before
    public void setUp()
    {
        gdd = new GDD();
        gdd.initialize();
        gdd.setLogger( new MockLogger() );
        lits = new MockLiterals();
        gdd.setLits( lits );
        mp = new MacroParser( gdd );
    }

    @Test
    public void testLookupSystemMacroInvalid()
    {
        assertNull( mp.lookupSystemMacro( "fanerk" ));
    }

    @Test
    public void testLookupSystemMacroValid()
    {
        lits.setVersionNumberToReturn( "0.2.0" );
        gdd.setLits( lits );
        Platypus.storeVersionNumber( gdd );
        assertTrue( lits.getLit( "VERSION" ).equals( mp.lookupSystemMacro( "_version" )));
    }

    @Test
    public void testExtractMacroNameValid1()
    {
        assertEquals( "$valid_macro", mp.extractMacroName( "$valid_macro]xyz".toCharArray(), 0 ));
    }

    @Test
    public void testParseValid()
    {
        lits.setVersionNumberToReturn( "0.2.0" );
        lits.setGetLitShouldFindLookupString( false );
        gdd.setLits( lits );
        Platypus.storeVersionNumber( gdd );

        assertEquals( "[_version]".length(), mp.parse( "[_version]".toCharArray(), 0 ));
        assertTrue( "0.2.0".equals(  gdd.getExpandedMacro() ));
    }
    
}
