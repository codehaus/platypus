/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.commandTypes;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.*;

/**
 * Test loading of commands with 0 parameters into the Platypus command table.
 *
 * @author alb
 */
public class Command0Test
{
    private Command0 c0;
    private ParseContext pc;
    private TokenList tl;
    private GDD gdd;

    @Before
    public void setUp()
    {
        c0 = new Command0( "[]", 'n' );
        pc = new ParseContext( gdd, new Source(), "[]blabla\n", 0 );
        tl = new TokenList();
    }

    @Test
    public void testProcess_DoesItReturnTheRightValue()
    {
        final String root = "[]";

        assertEquals( root.length(), c0.process( gdd, pc, tl, false ));
    }

    @Test
    public void testProcess_DoesTokenListHaveAToken()
    {
        final String root = "[]";

        assertEquals( 0, tl.size() );
        c0.process( gdd, pc, tl, false );
        assertEquals( 1, tl.size() );
    }

    @Test
    public void testProcess_IsTokenInTokenListCorrect()
    {
        final String root = "[]";

        assertEquals( 0, tl.size() );
        c0.process( gdd, pc, tl, false );

        Token tok = tl.get( 0 );
        assertNotNull( tok );
        assertEquals( TokenType.COMMAND, tok.getType() );
        assertEquals( root, tok.getRoot() );
        assertEquals( new Source(), tok.getSource() );
    }
}