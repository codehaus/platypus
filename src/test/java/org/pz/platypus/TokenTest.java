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
 * Tests for the Token class
 *
 * @author alb
 */
public class TokenTest
{

    Literals lits;
    GDD gdd;

    @Before
    public void setUp()
    {
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
    }

    @Test
    public void testToStringValidNonCommand1()
    {
        Token t = new Token( new Source( 11 ), TokenType.COMPOUND_COMMAND_END, null );
        assertEquals( "Line 0011: Compound Command End ->  ", t.toString( gdd ) );
    }

    @Test
    public void testToStringValidNonCommand2()
    {
        Token t = new Token( new Source( 12 ), TokenType.MACRO, "[$macro]" );
        assertEquals( "Line 0012: Macro                -> [$macro]", t.toString( gdd ) );
    }

    @Test
    public void testToStringValidCommand1()
    {
        CommandParameter cp = new CommandParameter();
        cp.setUnit( UnitType.POINT );
        cp.setAmount( 12.0f );

        Token t = new Token( new Source( 13 ), TokenType.COMMAND,
                             "[fsize:", "[fsize:12pt]", cp );
        String tts = t.toString( gdd );
        assertEquals( "Line 0013: Command              -> font size: 12.0 pt", tts );
    }

    @Test
    public void testValidEquals1()
    {
        Token t1 = new Token( new Source( 14 ), TokenType.COMPOUND_COMMAND_END, null );
        Token t2 = new Token( new Source( 14 ), TokenType.COMPOUND_COMMAND_END, null );
        assertTrue( t1.equals( t2 ));
    }

    @Test
    public void testValidEquals2()
    {
        Token t1 = new Token( new Source( 15 ), TokenType.COMPOUND_COMMAND_END, "[]" );
        Token t2 = new Token( new Source( 15 ), TokenType.COMPOUND_COMMAND_END, "[]" );
        assertTrue( t1.equals( t2 ));
    }

    @Test
    public void testValidCommandEquals1()
    {
        CommandParameter cp = new CommandParameter();
        cp.setString( "Arial" );
        cp.setErrorCode( Status.OK );

        Token t1 = new Token( new Source( 16 ), TokenType.COMMAND, "[ff:",
                "[ff:Arial]", cp );
        Token t2 = new Token( new Source( 16 ), TokenType.COMMAND, "[ff:",
                "[ff:Arial]", cp );
        assertTrue( t1.equals( t2 ));
    }

    @Test
    public void testValidNotEquals1()
    {
        CommandParameter cp1 = new CommandParameter();
        cp1.setString( "Arial" );
        cp1.setErrorCode( Status.OK );

        Token t1 = new Token( new Source( 17 ), TokenType.COMMAND, "[ff:",
                "[ff:Arial]", cp1 );

        CommandParameter cp2 = new CommandParameter();
        cp2.setString( "Arial" );
        cp2.setErrorCode( Status.OK );
        cp2.setString( null );
        Token t2 = new Token( new Source( 17 ), TokenType.COMMAND, "[ff:",
                "[ff:Arial]", cp2 );

        assertFalse( t1.equals( t2 ));  
    }

    @Test
    public void testValidNotEquals2()
    {
        Token t = new Token( new Source( 12 ), TokenType.MACRO, "[$macro]" );
        assertFalse( t.equals( "[$macro]" ));
    }

    @Test
    public void testValidNotEquals3()
    {
        // different Source fields
        Token t1 = new Token( new Source( 12 ), TokenType.MACRO, "[$macro]" );
        Token t2 = new Token( new Source( 13 ), TokenType.MACRO, "[$macro]" );
        assertFalse( t1.equals( t2 ));
    }

    @Test
    public void testValidNotEquals4()
    {
        // different command roots
        CommandParameter cp1 = new CommandParameter();
        cp1.setString( "Arial" );
        cp1.setErrorCode( Status.OK );

        Token t1 = new Token( new Source( 17 ), TokenType.COMMAND, "[ff:",
                "[ff:Arial]", cp1 );

        Token t2 = new Token( new Source( 17 ), TokenType.COMMAND, "[fx:",
                "[ff:Arial]", cp1 );
        assertFalse( t1.equals( t2 ));
    }

    @Test
    public void hashCodeEquals()
    {
        CommandParameter cp1 = new CommandParameter();
        cp1.setString( "Arial" );
        cp1.setErrorCode( Status.OK );

        Token t1 = new Token( new Source( 17 ), TokenType.COMMAND, "[ff:",
                "[ff:Arial]", cp1 );
        Token t2 = new Token( new Source( 17 ), TokenType.COMMAND, "[ff:",
                "[ff:Arial]", cp1 );

        assertEquals( t1.hashCode(), t2.hashCode() );
    }

    @Test
    public void hashCodeNotEquals()
    {
        // different Source fields
        CommandParameter cp1 = new CommandParameter();
        cp1.setString( "Arial" );
        cp1.setErrorCode( Status.OK );

        Token t1 = new Token( new Source( 17 ), TokenType.COMMAND, "[ff:",
                "[ff:Arial]", cp1 );
        Token t2 = new Token( new Source( 18 ), TokenType.COMMAND, "[ff:",
                "[ff:Arial]", cp1 );

        assertFalse( t1.hashCode() == t2.hashCode() );
    }
}
