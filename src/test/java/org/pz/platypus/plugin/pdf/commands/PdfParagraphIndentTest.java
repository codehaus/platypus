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
 * Test processing of changing paragraph indent.
 *
 * @author alb
 */
public class PdfParagraphIndentTest
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
        tok = new Token( new Source(), TokenType.COMMAND, "[paraindent:", "[paraident:200]", parm );

        PdfParagraphIndent ppi = new PdfParagraphIndent();
        ppi.process( pdd, tok, 2 );

        assertEquals( currColWidth / 4f, pdd.getParagraphIndent(), 0.5f );
    }

    @Test
    public void testValidParagraphIndentOfZero()
    {
        // get the starting page width and height
        pdd.setColumnWidth( 288f, new Source() );
        float currColWidth = pdd.getColumnWidth();

        parm = new CommandParameter();
        parm.setAmount( 0f );
        parm.setUnit( UnitType.POINT );
        tok = new Token( new Source(), TokenType.COMMAND, "[paraindent:", "[paraident:200]", parm );

        PdfParagraphIndent ppi = new PdfParagraphIndent();
        ppi.process( pdd, tok, 2 );

        assertEquals( 0f, pdd.getParagraphIndent(), 0.1f );
    }

    @Test
    public void testInvalidParagraphIndentThatsNegative()
    {
        float startingIndent = pdd.getParagraphIndent();
        // get the starting page width and height
        pdd.setColumnWidth( 288f, new Source() );
        float currColWidth = pdd.getColumnWidth();

        parm = new CommandParameter();
        parm.setAmount( -4.0f );
        parm.setUnit( UnitType.POINT );
        tok = new Token( new Source(), TokenType.COMMAND, "[paraindent:", "[paraident:200]", parm );

        PdfParagraphIndent ppi = new PdfParagraphIndent();
        ppi.process( pdd, tok, 2 );

        // should result in no change to current indent value
        assertEquals( startingIndent, pdd.getParagraphIndent(), 0.5f );

        MockLogger ml = (MockLogger) gdd.getLogger();
        assertTrue( ml.getMessage().contains( "ERROR.INVALID_PARAGRAPH_INDENT" ));
    }

    @Test
    public void testInvalidParagraphIndentTooLarge()
    {
        float startingIndent = pdd.getParagraphIndent();
        // get the starting page width and height
        pdd.setColumnWidth( 288f, new Source() );
        float currColWidth = pdd.getColumnWidth();

        parm = new CommandParameter();
        parm.setAmount( 500 *  currColWidth );
        parm.setUnit( UnitType.POINT );
        tok = new Token( new Source(), TokenType.COMMAND, "[paraindent:", "[paraident:200]", parm );

        PdfParagraphIndent ppi = new PdfParagraphIndent();
        ppi.process( pdd, tok, 2 );

        // should result in no change to current indent value
        assertEquals( startingIndent, pdd.getParagraphIndent(), 0.5f );

        MockLogger ml = (MockLogger) gdd.getLogger();
        assertTrue( ml.getMessage().contains( "ERROR.INVALID_PARAGRAPH_INDENT" ));
    }
}