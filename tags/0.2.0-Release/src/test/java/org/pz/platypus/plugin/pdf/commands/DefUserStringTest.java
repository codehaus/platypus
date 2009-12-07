/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.*;
import org.pz.platypus.test.mocks.MockLiterals;
import org.pz.platypus.test.mocks.MockLogger;

/**
 * Test the definition of a user string, using [def:name{value}].
 *
 * @author alb
 */
public class DefUserStringTest
{
    private DefUserString dus;
    private ParseContext pc;
    private TokenList tl;
    private GDD gdd;
    private String validCommand = "[def:name{{value}}] blabla\n";

    @Before
    public void setUp()
    {
        dus = new DefUserString();

        pc = new ParseContext( gdd, new Source(), validCommand, 0 );
        tl = new TokenList();
        gdd = new GDD();
        gdd.initialize();
        gdd.setLits( new MockLiterals() );
        gdd.setLogger( new MockLogger() );
    }

    @Test
    public void testConstructor()
    {
        assertEquals( ParamType.STRING, dus.getParamType() );
        assertEquals( "[def:", dus.getRoot() );
        assertNull( dus.getRootSubstitute() );
        assertFalse( dus.isAllowedInCode() );
        assertNull( dus.getTokenType() );
    }

    @Test
    public void testExtractMacroNameValid()
    {
        assertEquals( "name", dus.extractMacroName( "name{{value}}" ));
    }

    @Test
    public void testExtractMacroNameInvalid()
    {
        assertEquals( "", dus.extractMacroName( null ));
        assertEquals( "", dus.extractMacroName( "" ));
    }

    @Test
    public void testExtractValueValid()
    {
        assertEquals( "value", dus.extractMacroValue( "name{value}]", "name" ));
        assertEquals( "value", dus.extractMacroValue( "name{{value}}]", "name" ));
    }

    @Test
    public void testExtractValueInvalid()
    {
        assertEquals( "macro is null","", dus.extractMacroValue( null, "name" ));
        assertEquals( "name is empty", "", dus.extractMacroValue( "name{{value}}]", "" ));
        assertEquals( "space bet name+value", "", dus.extractMacroValue( "name {{value}}]", "name" ));
        assertEquals( "wrong macro name", "", dus.extractMacroValue( "name{{value}}]", "nam" ));
    }

//   Create mock user strings and run process(). Mock user strings, should simply record the adds.
}