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

import com.lowagie.text.Paragraph;

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
    PdfSymbol ps;

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
    public void getCharCodeInvalid1()
    {
        String root = "[beta]";
        String sym = "{}";   // invalid, as it contains no symbol name
        ps = new PdfSymbol( root, sym );

        String charCode = ps.getCharCode( sym );
        assertTrue( charCode.isEmpty() );
    }


    @Test
    public void getCharCodeInvalid2()
    {
        String root = "[beta]";
        String sym = "{Arial}\\20";   // invalid, because char part should start with \\\\
        ps = new PdfSymbol( root, sym );

        String charCode = ps.getCharCode( sym );
        assertTrue( charCode.isEmpty() );
    }

    @Test
    public void testValidUnicode()
    {
        String platySymbol = "[--]";
        String pdfSymbol   = "\\\\u2013";
        pds = new PdfSymbol( platySymbol, pdfSymbol  );

        // note this test requires an iText paragraph in the mock outfile.
        MockPdfOutfile mo = new MockPdfOutfile();
        pdd.setOutfile( mo );
        mo.setPdfData( pdd );
        mo.setItPara( new Paragraph() );

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

    @Test
    public void testValidUnicodeWithFontChange()
    {
        String platySymbol = "[trademark]";
        String pdfSymbol   = "{SYMBOL}\\\\u00E4";
        pds = new PdfSymbol( platySymbol, pdfSymbol  );

        // note this test requires an iText paragraph in the mock outfile.
        MockPdfOutfile mo = new MockPdfOutfile();
        pdd.setOutfile( mo );
        mo.setPdfData( pdd );
        mo.setItPara( new Paragraph() );

        CommandParameter cp = new CommandParameter();
        cp.setString( pdfSymbol );
        Token tok = new Token( new Source(), TokenType.SYMBOL, platySymbol, pdfSymbol, cp );

        pds.process( pdd, tok, 7  );

        assertEquals( "\u00E4", mo.getContent() );
    }

    @Test
    public void testInvalidUnicodeValue1()
    {
        String value = new PdfSymbol( "", "" ).getUnicodeValue( "!!" ); // invalid b/c < 4 chars
        assertEquals( "", value );
    }

    @Test
    public void testInvalidUnicodeValue2()
    {
        String value = new PdfSymbol( "", "" ).getUnicodeValue( "!!!!" );  // invalid b/c not valid value
        assertEquals( "", value );
    }
    @Test
    public void getCharCodeValidWithoutFontName()
    {
        String root = "[nine]"; // not a real Platypus command, but immaterial in this test.
        String sym  = "\\\\u0039";
        ps = new PdfSymbol( root, sym );

        String charCode = ps.getCharCode( ps.getSymEquivalent() );
        assertTrue( ! charCode.isEmpty() );
        assertEquals( charCode, ( "9" ));
    }

    @Test
    public void getCharCodeValidWithFontName()
    {
        String root = "[nine]"; // not a real Platypus command, but immaterial in this test.
        String sym  = "{Symbola}\\\\u0039";
        ps = new PdfSymbol( root, sym );

        String charCode = ps.getCharCode( ps.getSymEquivalent() );
        assertTrue( ! charCode.isEmpty() );
        assertEquals( charCode, ( "9" ));
    }

    @Test (expected=IllegalArgumentException.class)
    public void testInvalidCallToProcess()
    {
        String platySymbol = "[N~]";
        String pdfSymbol   = "Ñ";
        pds = new PdfSymbol( platySymbol, pdfSymbol  );

        CommandParameter cp = new CommandParameter();
        cp.setString( pdfSymbol );
        Token tok = new Token( new Source(), TokenType.SYMBOL, platySymbol, pdfSymbol, cp );

        pds.process( null, tok, 7  ); // invalide because first param cannot be null
    }

    @Test
    public void testExtractValidFontName()
    {
        String fontName = new PdfSymbol( "", "" ).extractFontName( "{SYMBOL}\\\\u00E4", null, null );
        assertEquals( "SYMBOL", fontName );

    }

    @Test
    public void extractValidFontName2()
    {
        String root = "[trademark]"; // not a real Platypus command, but immaterial in this test.
        String sym  = "{Symbol}\\\\u2122";
        ps = new PdfSymbol( root, sym );

        String charCode = ps.extractFontName( sym, gdd, new Token( new Source(), TokenType.COMMAND, "" ));
        assertTrue( ! charCode.isEmpty() );
        assertEquals( charCode, ( "Symbol" ));
    }

    @Test
    public void testExtractInvalidFontName()
    {
        String fontName = new PdfSymbol( "", "" ).extractFontName( null, null, null );
        assertEquals( "", fontName );
    }
}