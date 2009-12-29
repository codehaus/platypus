/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.*;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.test.mocks.*;

import java.util.logging.Level;

/**
 * Test processing of symbols and special characters in PDF plugin.
 *
 * @author alb
 */
public class PdfSymbolTest
{
    private GDD gdd;
    private PdfData pdd;
    private PdfSymbol pds;

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
        String platySymbol = "[--]";
        String pdfSymbol   = "\\\\u2013";
        pds = new PdfSymbol( platySymbol, pdfSymbol  );
        assertEquals( platySymbol, pds.getRoot() );
    }

    @Test
    public void testValidUnicode()
    {
        String platySymbol = "[--]";
        String pdfSymbol   = "\\\\u2013";
        pds = new PdfSymbol( platySymbol, pdfSymbol  );

        MockPdfOutfile mo = new MockPdfOutfile();
        pdd.setOutfile( mo );

        CommandParameter cp = new CommandParameter();
        cp.setString( pdfSymbol );
        Token tok = new Token( new Source(), TokenType.SYMBOL, platySymbol, pdfSymbol, cp );

        pds.process( pdd, tok, 7  );

        assertEquals( "\u2013", mo.getContent() );
    }

    @Test
    public void testValidNonUnicode()
    {
        String platySymbol = "[N~]";
        String pdfSymbol   = "Ñ";
        pds = new PdfSymbol( platySymbol, pdfSymbol  );

        MockPdfOutfile mo = new MockPdfOutfile();
        pdd.setOutfile( mo );

        CommandParameter cp = new CommandParameter();
        cp.setString( pdfSymbol );
        Token tok = new Token( new Source(), TokenType.SYMBOL, platySymbol, pdfSymbol, cp );

        pds.process( pdd, tok, 7  );

        assertEquals( "Ñ", mo.getContent() );
    }

    @Test (expected=IllegalArgumentException.class)
    public void testValidCallToProcess()
    {
        String platySymbol = "[N~]";
        String pdfSymbol   = "Ñ";
        pds = new PdfSymbol( platySymbol, pdfSymbol  );

        CommandParameter cp = new CommandParameter();
        cp.setString( pdfSymbol );
        Token tok = new Token( new Source(), TokenType.SYMBOL, platySymbol, pdfSymbol, cp );

        pds.process( null, tok, 7  );
    }

    @Test
    public void testExtractValidFontName()
    {
        String fontName = new PdfSymbol( "", "" ).extractFontName( "{SYMBOL}\\\\u00E4", null, null );
        assertEquals( "SYMBOL", fontName );

    }
}