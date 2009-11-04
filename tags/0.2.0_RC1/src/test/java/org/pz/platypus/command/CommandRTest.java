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
import org.pz.platypus.test.mocks.MockLogger;
import org.pz.platypus.test.mocks.MockLiterals;

/**
 * Test loading of commands that are a substitute/alias/replacement for another command.
 *
 * @author alb
 */
public class CommandRTest
{
    private CommandR cR;
    private ParseContext pc;
    private TokenList tl;
    private GDD gdd;
    private CommandTable ct;
    private Literals lits;

    private String root = "[ff:";
    private String command = "[ff:Arial]";
    private final String attributes = "rn [font|face:";

//    private String commandValue = "left";

    @Before
    public void setUp()
    {
        tl = new TokenList();
        gdd = new GDD();
        gdd.initialize();
        gdd.setLogger( new MockLogger() );
        lits = new MockLiterals();
        gdd.setLits( lits );
        ct = new CommandTable( gdd );

//      Insert the following commands that are needed for testing the font replacement commands
        ct.add( new CommandV( "[font|", 'n' ));
        ct.add( new CommandS( "[font|face:", 'n' ));
        ct.add( new CommandV( "[font|size:", 'n' ));
        ct.add( new CommandR( "[fsize:", "r [font|size:", ct ));
        ct.add( new CommandR( "[ff:", "rn [font|face:", ct ));

        gdd.setCommandTable( ct );
    }

	@Test
	public void testConstructor()
	{
		cR = new CommandR( root, attributes, ct );
        assertNotNull( cR );
	}

    @Test
    public void testExtractReplacementString1()
    {
		cR = new CommandR( root, attributes, ct );
        assertNotNull( cR );

        String extraction = cR.extractReplacementRoot( attributes );
        assertEquals( "[font|face:", extraction );
    }

    @Test
    public void testExtractReplacementStringInvalid1()
    {
		cR = new CommandR( root, attributes, ct );
        assertNotNull( cR );

        assertTrue( cR.extractReplacementRoot( null ).isEmpty() );
    }

    @Test // invalid because attrib does not end in :
    public void testExtractReplacementStringInvalid2()
    {
        final String attribs = "rn [font|fac";
		cR = new CommandR( root, attribs, ct );
        assertNotNull( cR );

        assertEquals( "[font|fac", cR.extractReplacementRoot( attribs ) );
    }

    @Test // invalid because replacement string contains a whitespace
    public void testExtractReplacementStringInvalid3()
    {
        final String attribs = "rn [font| fac";
		cR = new CommandR( root, attribs, ct );
        assertNotNull( cR );

        assertEquals( "[font|", cR.extractReplacementRoot( attribs ) );
    }

    @Test
    public void testExtractRootValid()
    {
		cR = new CommandR( root, attributes, ct );
        assertNotNull( cR );
        String  root = cR.extractRoot( gdd, new Source(), "[fsize:12pt" );
        assertEquals( root, "[fsize:" );
    }

    @Test
    public void testExtractRootInvalid()
    {
		cR = new CommandR( root, attributes, ct );
        assertNotNull( cR );
        String  root = cR.extractRoot( gdd, new Source(), "[fs" );
        assertTrue( root.isEmpty() );
    }

    @Test
    public void testProcessValid1()
    {
        final String commandString =   "12345[ff:Arial]678\n";
        final int startPoint = 5; // where to start parsing. 0-based
        TokenList tList = new TokenList();
        final boolean inCode = false;

        ParseContext context = new ParseContext( gdd, new Source(), commandString, startPoint );

		cR = new CommandR( root, attributes, ct );
        assertNotNull( cR );
        int x = cR.process( gdd, context, tList, inCode );
        assertEquals( "[ff:Arial]".length(), x );


    }
}