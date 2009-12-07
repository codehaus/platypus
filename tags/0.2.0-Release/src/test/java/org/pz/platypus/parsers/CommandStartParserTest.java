/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.parsers;

import org.junit.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test of CommandStartParser, which lexically analyzes a series
 * of chars with an embedded [ to see whether it's a Platypus command.
 *
 * @author alb
 */
public class CommandStartParserTest
{
    CommandStartParser gcp;

    @Before
    public void setUp()
    {
        gcp = new CommandStartParser();
    }


    @Test
    public void isItACommandNot_1()
    {
        char[] chars = { ' ', '[', '\n' };
        assertFalse( "a [ at end of line cannot be a command",
                      gcp.isItACommand( chars, 1 ));
    }

    @Test
    public void isItACommandNot_2()
    {
        char[] chars = { ' ', '[', ' ', '\n' };
        assertFalse( "a [ followed by whitespace cannot be a command",
                      gcp.isItACommand( chars, 1 ));
    }

    @Test
    public void isItACommandNot_3()
    {
        char[] chars = { ' ', '[', '9', ']', '\n' };
        assertFalse( "a [ followed by a digit cannot be a command",
                      gcp.isItACommand( chars, 1 ));
    }

    @Test
    public void isItACommandNot_4()
    {
        char[] chars = { '/', '[', 'x', '\n' };
        assertFalse( "a [ preceded by a / cannot be a command",
                      gcp.isItACommand( chars, 1 ));
    }

    @Test
    public void isItACommandYes()
    {
        char[] chars = { ' ', '[', ']', '\n' };
        assertTrue( "a [] is a command",
                      gcp.isItACommand( chars, 1 ));
    }
}
