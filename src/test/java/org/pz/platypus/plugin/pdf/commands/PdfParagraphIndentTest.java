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
import org.pz.platypus.plugin.pdf.Columns;
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
        pdd.setColumns( new Columns( pdd ));
    }

    @Test
    public void testValidParagraphIndent()
    {
        // get the starting page width and height
        pdd.setUserSpecifiedColumnWidth( 288f, new Source() );
        float currColWidth = pdd.getUserSpecifiedColumnWidth();

        parm = new CommandParameter();
        parm.setAmount( currColWidth / 4.0f );
        parm.setUnit( UnitType.POINT );
        tok = new Token( new Source(), TokenType.COMMAND, "[paraindent:",
                         "[paraindent:200]", parm );

        PdfParagraphIndent ppi = new PdfParagraphIndent();
        ppi.process( pdd, tok, 2 );

        assertEquals( currColWidth / 4f, pdd.getParagraphIndent(), 0.5f );
    }

    @Test
    public void testValidParagraphIndentOfZero()
    {
        parm = new CommandParameter();
        parm.setAmount( 0f );
        parm.setUnit( UnitType.POINT );
        tok = new Token( new Source(), TokenType.COMMAND, "[paraindent:", "[paraindent:0]", parm );

        PdfParagraphIndent ppi = new PdfParagraphIndent();
        ppi.process( pdd, tok, 2 );

        assertEquals( 0f, pdd.getParagraphIndent(), 0.1f );
    }

    @Test
    public void testInvalidParagraphIndentThatsNegative()
    {
        // get the starting page width and height
        float startingIndent = pdd.getParagraphIndent();

        parm = new CommandParameter();
        parm.setAmount( -4.0f );
        parm.setUnit( UnitType.POINT );
        tok = new Token( new Source(), TokenType.COMMAND, "[paraindent:", "[paraindent:-4]", parm );

        PdfParagraphIndent ppi = new PdfParagraphIndent();
        ppi.process( pdd, tok, 2 );

        // should result in no change to current indent value
        assertEquals( startingIndent, pdd.getParagraphIndent(), 0.1f );

        MockLogger ml = (MockLogger) gdd.getLogger();
        assertTrue( ml.getMessage().contains( "ERROR.INVALID_PARAGRAPH_INDENT" ));
    }

    @Test
    public void testInvalidParagraphIndentTooLarge()
    {
        // get the starting page width and height
        float startingIndent = pdd.getParagraphIndent();

        pdd.setUserSpecifiedColumnWidth( 288f, new Source() );
        float currColWidth = pdd.getUserSpecifiedColumnWidth();

        parm = new CommandParameter();
        parm.setAmount( 500 *  currColWidth );
        parm.setUnit( UnitType.POINT );
        tok = new Token( new Source(), TokenType.COMMAND, "[paraindent:",
                         "[paraindent:144000]", parm );

        PdfParagraphIndent ppi = new PdfParagraphIndent();
        ppi.process( pdd, tok, 2 );

        // should result in no change to current indent value
        assertEquals( startingIndent, pdd.getParagraphIndent(), 0.1f );

        MockLogger ml = (MockLogger) gdd.getLogger();
        assertTrue( ml.getMessage().contains( "ERROR.INVALID_PARAGRAPH_INDENT" ));
    }
}