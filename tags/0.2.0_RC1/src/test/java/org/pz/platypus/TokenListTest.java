/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the TokenList class
 *
 * @author alb
 */
public class TokenListTest
{

    Literals lits;
    GDD gdd;
    TokenList tl;

    @Before
    public void setUp()
    {
        tl = new TokenList();
        gdd = new GDD();
        gdd.initialize();
        lits = new Literals();
        lits.loadLine( "[fsize:=font size" );
        lits.loadLine( "COMMAND.FFACE=font face" );
        lits.loadLine( "COMMAND.FSIZE=font size" );
        lits.loadLine( "COMMAND.LEADING=leading" );
        lits.loadLine( "COMMAND_UNRECOGNIZED=unrecognized command" );
        gdd.setLits( lits );
        gdd.setupHomeDirectory();

        CommandTable ct = new CommandTable( gdd );
        gdd.setCommandTable( ct );
    }

    @Test
    public void addOneToken()
    {
        CommandParameter cp = new CommandParameter();
        cp.setUnit( UnitType.POINT );
        cp.setAmount( 12.0f );

        Token t = new Token( new Source( 13 ), TokenType.COMMAND,
                             "[fsize:", "[fsize:12pt]", cp );

        tl.add( t );
        assertEquals( 1, tl.size() );
    }

    @Test
    public void getNextTokenValid()
    {
        CommandParameter cp = new CommandParameter();
        cp.setUnit( UnitType.POINT );
        cp.setAmount( 12.0f );

        Token t = new Token( new Source( 13 ), TokenType.COMMAND,
                             "[fsize:", "[fsize:12pt]", cp );

        tl.add( t );

        Token t2 = new Token( new Source( 14 ), TokenType.MACRO, "[$macro]" );
        tl.add( t2 );

        Token t3 = tl.getNextToken( 0 );
        assertEquals( TokenType.MACRO, t3.getType() );
    }

    @Test
    public void getNextTokenInvalid1()
    {
        CommandParameter cp = new CommandParameter();
        cp.setUnit( UnitType.POINT );
        cp.setAmount( 12.0f );

        Token t = new Token( new Source( 13 ), TokenType.COMMAND,
                             "[fsize:", "[fsize:12pt]", cp );

        tl.add( t );

        Token t3 = tl.getNextToken( 0 );
        assertNull( t3 );
    }

   @Test
    public void getNextTokenInvalid2()
    {
        CommandParameter cp = new CommandParameter();
        cp.setUnit( UnitType.POINT );
        cp.setAmount( 12.0f );

        Token t = new Token( new Source( 13 ), TokenType.COMMAND,
                             "[fsize:", "[fsize:12pt]", cp );

        tl.add( t );

        Token t3 = tl.getNextToken( -1 );  //invalid token number
        assertNull( t3 );
    }

    @Test
    public void getPrevTokenValid()
    {
        CommandParameter cp = new CommandParameter();
        cp.setUnit( UnitType.POINT );
        cp.setAmount( 12.0f );

        Token t = new Token( new Source( 13 ), TokenType.COMMAND,
                             "[fsize:", "[fsize:12pt]", cp );

        tl.add( t );

        Token t2 = new Token( new Source( 14 ), TokenType.MACRO, "[$macro]" );
        tl.add( t2 );

        Token t3 = tl.getPrevToken( 1 );
        assertEquals( TokenType.COMMAND, t3.getType() );
    }

    @Test
    public void getPrevTokenInvalid()
    {
        CommandParameter cp = new CommandParameter();
        cp.setUnit( UnitType.POINT );
        cp.setAmount( 12.0f );

        Token t = new Token( new Source( 13 ), TokenType.COMMAND,
                             "[fsize:", "[fsize:12pt]", cp );

        tl.add( t );

        Token t3 = tl.getPrevToken( 0 );
        assertNull( t3 );
    }

    @Test
    public void lineSoFarValid1()
    {
        CommandParameter cp = new CommandParameter();
        cp.setUnit( UnitType.POINT );
        cp.setAmount( 12.0f );

        Token t = new Token( new Source( 13 ), TokenType.COMMAND,
                             "[fsize:", "[fsize:12pt]", cp );

        tl.add( t );

        Token t2 = new Token( new Source( 13 ), TokenType.MACRO, "[$macro]" );
        tl.add( t2 );

        assertFalse( tl.lineSoFarEmitsText( 1 ));
    }

    @Test
    public void lineSoFarValid2()
    {
        CommandParameter cp = new CommandParameter();
        cp.setUnit( UnitType.POINT );
        cp.setAmount( 12.0f );

        Token t = new Token( new Source( 13 ), TokenType.COMMAND,
                             "[fsize:", "[fsize:12pt]", cp );

        tl.add( t );

        assertFalse( tl.lineSoFarEmitsText( 0 ));
    }

    @Test
    public void lineSoFarValid3()
    {
        Token t0 = new Token( new Source( 13 ), TokenType.TEXT, "text token" );
        tl.add( t0 );

        CommandParameter cp = new CommandParameter();
        cp.setUnit( UnitType.POINT );
        cp.setAmount( 12.0f );

        Token t1 = new Token( new Source( 13 ), TokenType.COMMAND,
                             "[fsize:", "[fsize:12pt]", cp );

        tl.add( t1 );

        Token t2 = new Token( new Source( 13 ), TokenType.MACRO, "[$macro]" );
        tl.add( t2 );

        assertTrue( tl.lineSoFarEmitsText( 2 ));
    }

    @Test
    public void lineSoFarValid4()
    {
        CommandParameter cp = new CommandParameter();
        cp.setUnit( UnitType.POINT );
        cp.setAmount( 12.0f );

        Token t1 = new Token( new Source( 13 ), TokenType.COMMAND,
                             "[fsize:", "[fsize:12pt]", cp );
        tl.add( t1 );

        Token t2 = new Token( new Source( 13 ), TokenType.TEXT, "text token" );
        tl.add( t2 );

        Token t3 = new Token( new Source( 13 ), TokenType.MACRO, "[$macro]" );
        tl.add( t3 );

        assertTrue( tl.lineSoFarEmitsText( 2 ));
    }

    @Test
    public void lineSoFarValid5()
    {
        // note: text token is on previous line, so test result should be false
        Token t0 = new Token( new Source( 12 ), TokenType.TEXT, "text token" );
        tl.add( t0 );

        CommandParameter cp = new CommandParameter();
        cp.setUnit( UnitType.POINT );
        cp.setAmount( 12.0f );

        Token t1 = new Token( new Source( 13 ), TokenType.COMMAND,
                             "[fsize:", "[fsize:12pt]", cp );

        tl.add( t1 );

        Token t2 = new Token( new Source( 13 ), TokenType.MACRO, "[$macro]" );
        tl.add( t2 );

        assertFalse( tl.lineSoFarEmitsText( 2 ));
    }
}