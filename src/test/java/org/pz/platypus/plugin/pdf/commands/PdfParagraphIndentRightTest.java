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
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.test.mocks.MockLogger;
import org.pz.platypus.test.mocks.MockLiterals;

import java.util.logging.Level;

/**
 * Test processing of changing paragraph indent from the right side.
 *
 * @author alb
 */
public class PdfParagraphIndentRightTest
{
    private GDD gdd;
    private PdfData pdd;
    private Token tok;
    private CommandParameter parm;

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
    public void testValidParagraphIndent()
    {
        // get the starting page width and height
        pdd.setColumnWidth( 288f, new Source() );
        float currColWidth = pdd.getColumnWidth();

        parm = new CommandParameter();
        parm.setAmount( currColWidth / 4.0f );
        parm.setUnit( UnitType.POINT );
        tok = new Token( new Source(), TokenType.COMMAND, "[paraindentR:",
                         "[paraindentR:72]", parm );

        PdfParagraphIndentRight ppir = new PdfParagraphIndentRight();
        ppir.process( pdd, tok, 2 );

        assertEquals( currColWidth / 4f, pdd.getParagraphIndentRight(), 0.5f );
    }

    @Test
    public void testValidParagraphIndentOfZero()
    {
        parm = new CommandParameter();
        parm.setAmount( 0f );
        parm.setUnit( UnitType.POINT );
        tok = new Token( new Source(), TokenType.COMMAND, "[paraindentR:",
                         "[paraindentR:0]", parm );

        PdfParagraphIndentRight ppir = new PdfParagraphIndentRight();
        ppir.process( pdd, tok, 2 );

        assertEquals( 0f, pdd.getParagraphIndentRight(), 0.1f );
    }

    @Test
    public void testInvalidParagraphIndentThatsNegative()
    {
        // get the starting page width and height
        float startingIndent = pdd.getParagraphIndent();

        parm = new CommandParameter();
        parm.setAmount( -4.0f );
        parm.setUnit( UnitType.POINT );
        tok = new Token( new Source(), TokenType.COMMAND, "[paraindentR:",
                         "[paraindentR:-4]", parm );

        PdfParagraphIndentRight ppir = new PdfParagraphIndentRight();
        ppir.process( pdd, tok, 2 );

        // should result in no change to current indent value
        assertEquals( startingIndent, pdd.getParagraphIndentRight(), 0.1f );

        MockLogger ml = (MockLogger) gdd.getLogger();
        assertTrue( ml.getMessage().contains( "ERROR.INVALID_PARAGRAPH_INDENT" ));
    }

    @Test
    public void testInvalidParagraphIndentTooLarge()
    {
        // get the starting page width and height
        float startingIndent = pdd.getParagraphIndent();

        parm = new CommandParameter();
        parm.setAmount( 144000 );
        parm.setUnit( UnitType.POINT );
        tok = new Token( new Source(), TokenType.COMMAND, "[paraindentR:",
                         "[paraindentR:144000]", parm );

        PdfParagraphIndentRight ppir = new PdfParagraphIndentRight();
        ppir.process( pdd, tok, 2 );

        // should result in no change to current indent value
        assertEquals( startingIndent, pdd.getParagraphIndentRight(), 0.5f );

        MockLogger ml = (MockLogger) gdd.getLogger();
        assertTrue( ml.getMessage().contains( "ERROR.INVALID_PARAGRAPH_INDENT" ));
    }
}