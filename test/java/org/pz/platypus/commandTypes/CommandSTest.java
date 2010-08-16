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
import org.pz.platypus.test.mocks.MockLogger;
import org.pz.platypus.test.mocks.MockLiterals;

/**
 * Test loading of commands with 1 string parameter into the Platypus command table.
 *
 * @author alb
 */
public class CommandSTest
{
    private CommandS cS;
    private ParseContext pc;
    private TokenList tl;
    private GDD gdd;
    private String root = "[align:";
    private String command = "[align:left]";
    private String commandValue = "left";
    private MockLogger mlog;

    @Before
    public void setUp()
    {
        cS = new CommandS( root, 'n' );
        pc = new ParseContext( gdd, new Source(), "[align:left]blabla\n", 0 );
        tl = new TokenList();
        gdd = new GDD();
        gdd.initialize();
        mlog = new MockLogger();
        gdd.setLogger( mlog );
        gdd.setLits( new MockLiterals() );
    }

    @Test
    public void testConstructor()
    {
        assertEquals( ParamType.STRING, cS.getParamType() );
        assertEquals( root, cS.getRoot() );
        assertNull( cS.getRootSubstitute() );
    }

    @Test
    public void testProcess_DoesItReturnTheRightValue()
    {
         assertEquals( command.length(), cS.process( gdd, pc, tl, false ));
    }

    @Test
    public void testProcess_DoesTokenListHaveAToken()
    {
        assertEquals( 0, tl.size() );
        cS.process( gdd, pc, tl, false );
        assertEquals( 1, tl.size() );
    }

    @Test
    public void testProcess_IsTokenInTokenListCorrect()
    {
        assertEquals( 0, tl.size() );
        cS.process( gdd, pc, tl, false );

        Token tok = tl.get( 0 );
        assertNotNull( tok );
        assertEquals( TokenType.COMMAND, tok.getType() );
        assertEquals( root, tok.getRoot() );
        assertEquals( new Source(), tok.getSource() );
    }

    @Test
    public void testProcess_IsParameterOfTokenInTokenListCorrect()
    {
        assertEquals( 0, tl.size() );
        cS.process( gdd, pc, tl, false );

        Token tok = tl.get( 0 );
        assertNotNull( tok );

        CommandParameter cp = tok.getParameter();
        assertNotNull( cp );
        assertEquals( commandValue, cp.getString());
    }

    @Test
    public void testProcessWithParamInBraces()
    {
        root = "[font|face:";
        command = "[font|face:{Courier New}]";
        commandValue = "Courier New";

        cS = new CommandS( root, 'n' );
        pc = new ParseContext( gdd, new Source(), command + "blablabla\n", 0 );

        assertEquals( 0, tl.size() );
        int charsToSkip = cS.process( gdd, pc, tl, false );

        assertEquals( charsToSkip, command.length() );

        Token tok = tl.get( 0 );
        assertNotNull( tok );
        assertEquals( TokenType.COMMAND, tok.getType() );

        assertEquals( root, tok.getRoot() );
        assertEquals( new Source(), tok.getSource() );

        assertEquals( command, tok.getContent() );
        assertEquals( commandValue, tok.getParameter().getString() );
    }

    @Test
    public void testCommandNotValidInCode()
    {
        root = "[font|face:";
        command = "[font|face:{Courier New}]";
        commandValue = "Courier New";

        cS = new CommandS( root, 'n' );
        gdd.setInCode( true );

        pc = new ParseContext( gdd, new Source(), command + "blablabla\n", 0 );

        assertEquals( 0, tl.size() );
        int charsToSkip = cS.process( gdd, pc, tl, false );

        assertEquals( charsToSkip, command.length() );
        assertEquals( 0, tl.size() );

        assertTrue( mlog.getMessage().contains( "COMMAND_NOT_ALLOWED_IN_CODE" ));
    }
}