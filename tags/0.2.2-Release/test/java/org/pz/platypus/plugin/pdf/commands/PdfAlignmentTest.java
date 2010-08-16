/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.*;
import org.pz.platypus.commands.Alignment;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.test.mocks.MockLogger;
import org.pz.platypus.test.mocks.MockLiterals;

import java.util.logging.Level;

/**
 * Test processing of changing paragraph indent from the right side.
 *
 * @author alb
 */
public class PdfAlignmentTest
{
    private GDD gdd;
    private PdfData pdd;
    private Token tok;
    private CommandParameter parm;
    private PdfAlignment align;

    @Before
    public void setUp()
    {
        gdd = new GDD();
        gdd.initialize();
        gdd.setLogger( new MockLogger() );
        gdd.setLits( new MockLiterals() );
        gdd.getLogger().setLevel( Level.OFF );
        pdd = new PdfData( gdd );
    }

    @Test
    public void testConstructor()
    {
        align = new PdfAlignment();
        assertEquals( "[align:", align.getRoot() );
    }
    @Test
    public void testValidCenterAlignment()
    {
        parm = new CommandParameter();
        parm.setString( "center" );
        tok = new Token( new Source( 6 ), TokenType.COMMAND, "[align:",
                         "[align:center]", parm );

        align = new PdfAlignment();
        align.process( pdd, tok, 2 );

        assertEquals( Alignment.CENTER, pdd.getAlignment() );
        assertEquals( 6, pdd.getAlignmentLine().getLineNumber() );
    }

    @Test
    public void testValidJustifiedAlignment()
    {
        parm = new CommandParameter();
        parm.setString( "justified" );
        tok = new Token( new Source( 6 ), TokenType.COMMAND, "[align:",
                         "[align:just]", parm );

        align = new PdfAlignment();
        align.process( pdd, tok, 2 );

        assertEquals( Alignment.JUST, pdd.getAlignment() );
        assertEquals( 6, pdd.getAlignmentLine().getLineNumber() );
    }

    @Test
    public void testInvalidAlignment()
    {
        // get the starting alignment
        int startingAlignment = pdd.getAlignment();

        parm = new CommandParameter();
        parm.setString( "parker" );
        tok = new Token( new Source( 6 ), TokenType.COMMAND, "[align:",
                         "[align:parker]", parm );

        align = new PdfAlignment();
        align.process( pdd, tok, 2 );

        // should be no change to the alignment
        assertEquals( startingAlignment, pdd.getAlignment() );
        assertFalse( 6 == pdd.getAlignmentLine().getLineNumber() );

        MockLogger ml = (MockLogger) gdd.getLogger();
        assertTrue( ml.getMessage().contains( "ERROR.INVALID_ALIGNMENT" ));
    }
}