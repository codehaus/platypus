/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.utilities;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.*;

import java.util.logging.Level;

/**
 * Test various error message routines
 *
 * @author alb
 */
public class ErrorMsgTest
{
    private GDD gdd;

    @Before
    public void setUp()
    {
        gdd = new GDD();

        Literals lits = new Literals( );
        lits.loadLine( "LINE#=line #" );
        lits.loadLine( "COMMAND_NOT_ALLOWED_IN_CODE=command not allowed in code listing.");
        lits.loadLine( "IGNORED=Ignored.");
        gdd.setLits( lits );;
        gdd.setupLogger( ( "org.pz.platypus.Platypus" ));
        gdd.getLogger().setLevel( Level.OFF );
    }

    @Test
    public void testInvalidCall1()
    {
        String s = ErrorMsg.NotAllowedInCode( null, null, new Source( 16 ));
        assertEquals( "", s );
    }

    @Test
    public void testInvalidCall2()
    {
        // s/fail because Source is null
        String s = ErrorMsg.NotAllowedInCode( gdd, "[]", null );
        assertEquals( "", s );
    }

    @Test
    public void testValidStringNotAllowedInCodeErr()
    {
        String commandStr = "[fsize:12pt]";
        String msg = ErrorMsg.NotAllowedInCode( gdd, commandStr , new Source( 23 ));
        assertEquals( "line # 23: " + commandStr + " command not allowed in code listing." +  " " +
                      "Ignored.", msg );
    }
}
