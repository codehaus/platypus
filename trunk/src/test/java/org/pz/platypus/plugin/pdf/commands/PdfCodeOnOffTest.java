/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import org.pz.platypus.*;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.plugin.pdf.FormatStack;
import org.pz.platypus.plugin.pdf.PdfOutfile;
import org.pz.platypus.test.mocks.MockLogger;
import org.pz.platypus.test.mocks.MockLiterals;
import org.pz.platypus.test.mocks.MockPdfOutfile;
import org.junit.Test;
import org.junit.Before;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.util.logging.Level;

/**
 * Test the start and end of a code section/listing.
 *
 * @author alb
 */

public class PdfCodeOnOffTest
{
    PdfData pdd;
    GDD gdd;
    PdfCodeOn codeOn;
    PdfCodeOff codeOff;

    String codeOnRoot = "[code]";
    String codeOffRoot = "[-code]";

    @Before
    public void setUp()
    {
        gdd = new GDD();
        gdd.initialize();
        gdd.setLogger( new MockLogger() );
        gdd.setLits( new MockLiterals() );
        gdd.getLogger().setLevel( Level.OFF );
        pdd = new PdfData( gdd );

        codeOn = new PdfCodeOn();
        codeOff = new PdfCodeOff();
    }

    // ==== actual tests start here ===== //

    @Test
    public void testConstructors()
    {
        assertEquals( codeOnRoot, codeOn.getRoot() );
        assertEquals( codeOffRoot, codeOff.getRoot() );
    }

    @Test
    public void testEffectsOfCodeSwitchedOn()
    {
        assertFalse( pdd.inCodeSection() );
        pdd.setFormatStack( new FormatStack( pdd ));
        FormatStack formats = pdd.getFormatStack();
        assertEquals( 1, formats.getSize() );

        Token tok = new Token( new Source(), TokenType.COMMAND, codeOnRoot, "", null );
        codeOn.process( pdd, tok, 6);

        assertEquals( 2, formats.getSize() );
        assertTrue( pdd.inCodeSection() );
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCodeOffIllegalParameter1()
    {
        codeOff.process( pdd,null, 7);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCodeOffIllegalParameter2()
    {
        Token tok = new Token( new Source(), TokenType.COMMAND, codeOffRoot, "", null );
        codeOff.process( null, tok, 7);
    }

    @Test
    public void testNotInCodeSection()
    {
        pdd.setInCodeSection( false, new Source() );
        Token tok = new Token( new Source(), TokenType.COMMAND, codeOffRoot, "", null );
        assertEquals( 0, codeOff.process( pdd, tok, 7 ));
    }

    @Test
    public void testEffectsOfCodeSwitchedOff()
    {
        // first, turn code on
        assertFalse( pdd.inCodeSection() );
        pdd.setFormatStack( new FormatStack( pdd ));
        FormatStack formats = pdd.getFormatStack();
        Token tok = new Token( new Source(), TokenType.COMMAND, codeOnRoot, "", null );
        codeOn.process( pdd, tok, 6);
        assertTrue( pdd.inCodeSection() );

        // now, turn it off
        Token tok2 = new Token( new Source(), TokenType.COMMAND, codeOffRoot, "", null );
        codeOff.process( pdd, tok2, 7);

        assertEquals( 1, formats.getSize() );
        assertFalse( pdd.inCodeSection() );
    }

    @Test
    public void testFullProcessingOfCodeOff()
    {
        pdd.setInCodeSection( true, new Source() );
        pdd.setInCodeListing( true );
        PdfOutfile outfile = (PdfOutfile) new MockPdfOutfile();
        pdd.setOutfile( outfile );
        pdd.setFormatStack( new FormatStack( pdd ));

        Paragraph para = new Paragraph();
        para.add( "hello" );
        outfile.setItPara( para );

        TokenList tl = gdd.getInputTokens();
        Token tok1= new Token( new Source(), TokenType.COMMAND, codeOffRoot, "", null );
        Token tok2 = new Token( new Source(), TokenType.COMMAND, "[cr]", "[cr]", null );

        tl.add( tok1 );
        tl.add( tok2 );
        int ret =  codeOff.process( pdd, tok1, 0 );
        assertEquals( 1, ret );
    }

    @Test
    public void testNexTokenIsEolTrueInCodeOff()
    {
        TokenList tl = new TokenList();

        Token tok1= new Token( new Source(), TokenType.COMMAND, codeOffRoot, "", null );
        Token tok2 = new Token( new Source(), TokenType.COMMAND, "[cr]", "[cr]", null );

        tl.add( tok1 );
        tl.add( tok2 );

        assertTrue( codeOff.nextTokenIsEol( tl, 0 ));
    }

   @Test
    public void testNexTokenIsEolFalseInCodeOff()
    {
        TokenList tl = new TokenList();

        Token tok1= new Token( new Source(), TokenType.COMMAND, codeOffRoot, "", null );
        Token tok2 = new Token( new Source(), TokenType.COMMAND, "[fsize:12pt]", "[fsize:12pt]", null );

        tl.add( tok1 );
        tl.add( tok2 );

        assertFalse( codeOff.nextTokenIsEol( tl, 0 ));
    }

   @Test
    public void testNexTokenIsEolTextInCodeOff()
    {
        TokenList tl = new TokenList();

        Token tok1= new Token( new Source(), TokenType.COMMAND, codeOffRoot, "", null );
        Token tok2 = new Token( new Source(), TokenType.TEXT, null, null, null );

        tl.add( tok1 );
        tl.add( tok2 );

        assertFalse( codeOff.nextTokenIsEol( tl, 0 ));
    }

   @Test
    public void testBeforeEndOfParagraphTrueInCodeOff()
    {
        TokenList tl = new TokenList();

        Token tok1= new Token( new Source(), TokenType.COMMAND, codeOffRoot, "", null );
        Token tok2 = new Token( new Source(), TokenType.COMMAND, "[cr]", "[cr]", null );
        Token tok3 = new Token( new Source(), TokenType.COMMAND, "[CR]", "[CR]", null );

        tl.add( tok1 );
        tl.add( tok2 );
        tl.add( tok3 );

        assertTrue( codeOff.justBeforeEndOfParagraph( tl, 0 ));
    }

   @Test
    public void testBeforeEndOfParagraphFalse1InCodeOff()
    {
        TokenList tl = new TokenList();

        Token tok1= new Token( new Source(), TokenType.COMMAND, codeOffRoot, "", null );
        Token tok2 = new Token( new Source(), TokenType.COMMAND, "[fsize:12pt]", "[fsize:12pt]", null );
        Token tok3 = new Token( new Source(), TokenType.COMMAND, "[CR]", "[CR]", null );

        tl.add( tok1 );
        tl.add( tok2 );
        tl.add( tok3 );

        assertFalse( codeOff.justBeforeEndOfParagraph( tl, 0 ));
    }

   @Test
    public void testBeforeEndOfParagraphFalse2InCodeOff()
    {
        TokenList tl = new TokenList();

        Token tok1= new Token( new Source(), TokenType.COMMAND, codeOffRoot, "", null );
        Token tok2 = new Token( new Source(), TokenType.COMMAND, "[cr]", "[cr]", null );
        Token tok3 = new Token( new Source(), TokenType.COMMAND, "[fsize:12pt]", "[fsize:12pt]", null );


        tl.add( tok1 );
        tl.add( tok2 );
        tl.add( tok3 );

        assertFalse( codeOff.justBeforeEndOfParagraph( tl, 0 ));
    }

   @Test
    public void testBeforeEndOfParagraphTextInCodeOff()
    {
        TokenList tl = new TokenList();

        Token tok1= new Token( new Source(), TokenType.COMMAND, codeOffRoot, "", null );
        Token tok2 = new Token( new Source(), TokenType.COMMAND, "[cr]", "[cr]", null );
        Token tok3 = new Token( new Source(), TokenType.TEXT, null, null, null );


        tl.add( tok1 );
        tl.add( tok2 );
        tl.add( tok3 );

        assertFalse( codeOff.justBeforeEndOfParagraph( tl, 0 ));
    }

   @Test
    public void testBeforeEndOfParagraphNullInCodeOff()
    {
        TokenList tl = new TokenList();

        Token tok1= new Token( new Source(), TokenType.COMMAND, codeOffRoot, "", null );
        Token tok2 = new Token( new Source(), TokenType.COMMAND, "[cr]", "[cr]", null );

        tl.add( tok1 );
        tl.add( tok2 );

        assertFalse( codeOff.justBeforeEndOfParagraph( tl, 0 ));
    }
}