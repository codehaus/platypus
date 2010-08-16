/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import static org.junit.Assert.*;
import org.pz.platypus.*;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.test.mocks.MockLogger;
import org.pz.platypus.test.mocks.MockLiterals;
import org.junit.Test;
import org.junit.Before;

import java.util.logging.Level;

/**
 * Test of plugin.pdf.commands.PdfPrintVariableTest, which prints a previously
 * defined macro's content to the document
 *
 * @author alb
 */
public class PdfPrintVariableTest
{
    PdfData pdd;
    GDD gdd;
    PdfPrintVariable ppv;

    @Before
    public void setUp()
    {
        gdd = new GDD();
        gdd.initialize();
        gdd.setLogger( new MockLogger() );
        gdd.setLits( new MockLiterals() );
        gdd.getLogger().setLevel( Level.OFF );
        pdd = new PdfData( gdd );
        ppv = new PdfPrintVariable();
    }

    // ==== actual tests start here ===== //

    @Test
    public void testConstructor()
    {
        assertNotNull( ppv );
        assertEquals( "[*", ppv.getRoot() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProcessWithNullParameter1()
    {
        assertNotNull( ppv );
        ppv.process( null, new Token( new Source(), TokenType.TEXT, "ALLO" ), 3 );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProcessWithNullParameter2()
    {
        assertNotNull( ppv );
        ppv.process( pdd, null, 3 );
    }

    @Test
    public void testValidUserMacroConversion()
    {
        // first add the macro to the user strings
        UserStrings userMacros = gdd.getUserStrings();
        int ret = userMacros.add( "foo", "bar" );
        assertEquals( Status.OK, ret );

        // then add the [* token to the TokenList
        CommandParameter cp = new CommandParameter();
        cp.setString( "foo" );
        cp.setUnit( UnitType.NONE );
        Token newToken = new Token(  new Source(), TokenType.COMMAND, "[*", "[*foo]", cp );
        TokenList tl = gdd.getInputTokens();
        tl.add( newToken );

        // now, perform the lookup and generation of the text token
        ppv.process( pdd, newToken, 0 );

        // did we get what we expected?
        assertEquals( 2, tl.size() );

        Token outputToken = tl.get( 1 );
        assertEquals( TokenType.MACRO_TEXT, outputToken.getType() );
        assertEquals( "bar", outputToken.getContent() );
    }

    @Test
    public void testValidSystemMacroConversion()
    {
        // first add the macro to the system strings
        SystemStrings sysMacros = gdd.getSysStrings();
        int ret = sysMacros.add( "_version", "the latest release" );
        assertEquals( Status.OK, ret );

        // then add the [* token to the TokenList
        CommandParameter cp = new CommandParameter();
        cp.setString( "_version" );
        cp.setUnit( UnitType.NONE );
        Token newToken = new Token(  new Source(), TokenType.COMMAND, "[*", "[*_version]", cp );
        TokenList tl = gdd.getInputTokens();
        tl.add( newToken );

        // now, perform the lookup and generation of the text token
        ppv.process( pdd, newToken, 0 );

        // did we get what we expected?
        assertEquals( 2, tl.size() );

        Token outputToken = tl.get( 1 );
        assertEquals( TokenType.MACRO_TEXT, outputToken.getType() );
        assertEquals( "the latest release", outputToken.getContent() );
    }

    @Test
    public void testInvalidMacroConversion()
    {
        // add nothing to either user or system macros

        // then add the [* token to the TokenList
        CommandParameter cp = new CommandParameter();
        cp.setString( "_version" );
        cp.setUnit( UnitType.NONE );
        Token newToken = new Token(  new Source(), TokenType.COMMAND, "[*", "[*_version]", cp );
        TokenList tl = gdd.getInputTokens();
        tl.add( newToken );

        // now, perform the lookup and generation of the text token
        ppv.process( pdd, newToken, 0 );

        // did we get what we expected?
        assertEquals( 2, tl.size() );

        // the result should be that the macro lookup command becomes text itself
        Token outputToken = tl.get( 1 );
        assertEquals( TokenType.MACRO_TEXT, outputToken.getType() );
        assertEquals( "[*_version]", outputToken.getContent() );

        // check that an error message was output
        MockLogger mockLog = (MockLogger) gdd.getLogger();
        assertTrue( mockLog.getMessage().contains( "ERROR.INVALID_USER_STRING" ));
    }
}