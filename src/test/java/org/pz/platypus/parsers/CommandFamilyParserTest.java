/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.parsers;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import org.pz.platypus.*;
import org.pz.platypus.exceptions.InvalidCommandException;
import org.pz.platypus.exceptions.InvalidCommandParameterException;
import org.pz.platypus.test.mocks.MockLiterals;
import org.pz.platypus.test.mocks.MockCommandTable;
import org.pz.platypus.test.mocks.MockLogger;

import java.util.logging.Logger;

/**
 * Test CommandFamilyParser
 *
 * @author alb
 */
public class CommandFamilyParserTest
{
    private CommandFamilyParser cfp;
    private GDD gdd;
    private TokenList tl;

    @Before
    public void setUp()
    {
        cfp = new CommandFamilyParser();
        gdd = new GDD();
        gdd.initialize();

        Literals lits = new MockLiterals( );
        gdd.setLits( lits );

        Logger logger = new MockLogger();
        gdd.setLogger( logger );

        tl = new TokenList();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullsInPassedParams1()
    {
        cfp.parse( null, null, null, null, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullsInPassedParams2()
    {
        cfp.parse( null, null, null, null, gdd );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidContext1()
    {
        // parse point + root length will point to \n rather than |, generating an error
        ParseContext context = new ParseContext( gdd, new Source(), "[font|\n", 1 );
        CommandTable ct = new MockCommandTable( gdd );
        cfp.parse( ct, context, tl, "[font|", gdd );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testsInvalidContext2()
    {
        // parse point is beyond end of text to be parsed, generating an error
        ParseContext context = new ParseContext( gdd, new Source(), "[font|\n", 7 );
        CommandTable ct = new MockCommandTable( gdd );
        cfp.parse( ct, context, tl, "[font|", gdd );
    }

    @Test(expected = InvalidCommandException.class)
    public void testInvalidCommandLookupInCommandTable()
    {
        // Command is not found CommandTable
        ParseContext context = new ParseContext( gdd, new Source(), "[font|\n", 0 );
        CommandTable ct = new MockCommandTable( gdd );
        cfp.parse( ct, context, tl, "[font|", gdd );
    }

    @Test
    public void testValidEntryWithNoParameters()
    {
        ParseContext context = new ParseContext( gdd, new Source(), "[font|noparm]\n", 0 );
        CommandTable ct = new MockCommandTable( gdd );
        ct.loadCommand( "[font|noparm]", "0n" );

        cfp.parse( ct, context, tl, "[font|", gdd );
        assertTrue( tl.size() == 1 );
        assertEquals( UnitType.NONE, tl.get( 0 ).getParameter().getUnit() );
    }


    @Test
    public void testValidEntryWithStringParameter()
    {
        ParseContext context = new ParseContext( gdd, new Source(), "[font|face:Arial]\n", 0 );
        CommandTable ct = new MockCommandTable( gdd );
        ct.loadCommand( "[font|face:", "sn" );

        cfp.parse( ct, context, tl, "[font|", gdd );
        assertTrue( tl.size() == 1 );
        assertEquals( UnitType.NONE, tl.get( 0 ).getParameter().getUnit() );
    }

    @Test(expected = InvalidCommandParameterException.class)
    public void testInvalidEntryWithStringParameter()
    {
        // command that takes a string, but is passed the command without a string
        ParseContext context = new ParseContext( gdd, new Source(), "[font|face:]\n", 0 );
        CommandTable ct = new MockCommandTable( gdd );
        ct.loadCommand( "[font|face:", "sn" );

        cfp.parse( ct, context, tl, "[font|", gdd );
    }

    @Test
    public void testValidEntryWithMeasureParameter()
    {
        ParseContext context = new ParseContext( gdd, new Source(), "[font|size:12pt]\n", 0 );
        CommandTable ct = new MockCommandTable( gdd );
        ct.loadCommand( "[font|size:", "vn" );

        cfp.parse( ct, context, tl, "[font|", gdd );
        assertTrue( tl.size() == 1 );
        assertEquals( 12.0f, tl.get(0).getParameter().getAmount(), 0.1f );
        assertEquals( UnitType.POINT, tl.get( 0 ).getParameter().getUnit() );
    }

    @Test(expected = InvalidCommandParameterException.class)
    public void testInvalidEntryWithInvalidNumber()
    {
        ParseContext context = new ParseContext( gdd, new Source(), "[font|size:xyz]\n", 0 );
        CommandTable ct = new MockCommandTable( gdd );
        ct.loadCommand( "[font|size:", "vn" );

        cfp.parse( ct, context, tl, "[font|", gdd );
    }

    @Test(expected = InvalidCommandParameterException.class)
    public void testInvalidEntryWithInvalidUnitParameter()
    {
        ParseContext context = new ParseContext( gdd, new Source(), "[font|size:12xyz]\n", 0 );
        CommandTable ct = new MockCommandTable( gdd );
        ct.loadCommand( "[font|size:", "vn" );

        cfp.parse( ct, context, tl, "[font|", gdd );
    }

    @Test
    public void testValidMultipleCommandString()
    {
        String commandString = "[font|face:Arial|size:12pt]";
        ParseContext context = new ParseContext( gdd, new Source(), commandString + "\n", 0 );
        CommandTable ct = new MockCommandTable( gdd );
        ct.loadCommand( "[font|size:", "vn" );
        ct.loadCommand( "[font|face:", "sn" );

        cfp.parse( ct, context, tl, "[font|", gdd );
        assertTrue( tl.size() == 4 );
        assertEquals( commandString, tl.get(0).getContent() );
        assertEquals( "Arial", tl.get(1).getParameter().getString() );
        assertEquals( 12.0f, tl.get(2).getParameter().getAmount(), 0.1f );
        assertEquals( TokenType.COMPOUND_COMMAND_END, tl.get( 3 ).getType() );
    }
}