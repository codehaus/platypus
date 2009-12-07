/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.command;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.*;

/**
 * Test loading of commands with 1 value parameter into the Platypus command table.
 *
 * @author alb
 */
public class CommandVTest
{
    private CommandV cV;
    private ParseContext pc;
    private TokenList tl;
    private GDD gdd;
    private String root = "[leading:";
    private String command = "[leading:12pt]";
    private String commandValue = "left";

    @Before
    public void setUp()
    {
        cV = new CommandV( root, 'n' );
        pc = new ParseContext( gdd, new Source(), command + "blabla\n", 0 );
        tl = new TokenList();
        gdd = new GDD();
        gdd.initialize();

    }
	
	@Test
	public void dummyTest()
	{
		assertEquals( 1, 1 );
	}
}