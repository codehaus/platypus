/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import com.lowagie.text.Paragraph;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.*;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.plugin.pdf.PdfOutfile;
import org.pz.platypus.test.mocks.MockLiterals;
import org.pz.platypus.test.mocks.MockLogger;

import java.util.logging.Level;

import static org.junit.Assert.*;

/**
 * Test processing of hard end-of-line, as specified by [] or by setting the eol's to hard.
 *
 * @author alb
 */
public class PdfHardCRTest
{
    private GDD gdd;
    private PdfData pdd;
    private Token tok;
    private CommandParameter parm;
    private PdfHardCR cr;

    @Before
    public void setUp()
    {
        gdd = new GDD();
        gdd.initialize();
        gdd.setLogger(new MockLogger());
        gdd.setLits(new MockLiterals());
        gdd.getLogger().setLevel(Level.OFF);
        pdd = new PdfData( gdd );
        parm = new CommandParameter();
        cr = new PdfHardCR();
    }

    @Test
    public void testConstructor()
    {
        assertEquals( "[]", cr.getRoot() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidProcess1()
    {   // should throw exception due to initial null parm
        cr.process( null, new Token( new Source(), TokenType.COMMAND, "[]", "[]", parm ), 6 );
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidProcess2()
    {   // should throw exception due to the second null parm
        cr.process( pdd, null,  6 );
    }

    @Test
    public void nonExistentOutfile()
    {   pdd.setOutfile( null );
        assertEquals(0, cr.process(pdd, new Token(new Source(), TokenType.COMMAND, "[]", "[]", parm), 0));
    }

    @Test
    public void isAfterCodeListingFalse1()
    {   // a 1-token list, so prevToken will be null, so should exit with false
        Token tok = new Token( new Source(), TokenType.COMMAND, "[]", "[]", parm );
        TokenList tl = new TokenList();
        tl.add( tok );
        gdd.setInputTokens( tl );
        assertEquals( false, cr.isAfterCodeListing( pdd, 0 ));
    }

    @Test
    public void isAfterCodeListingFalse2()
    {   // a 2-token list, so prevToken will be [], so should exit with false
        Token tok = new Token( new Source(), TokenType.COMMAND, "[]", "[]", parm );
        TokenList tl = new TokenList();
        tl.add( tok );
        tl.add( tok );
        gdd.setInputTokens( tl );
        assertEquals( false, cr.isAfterCodeListing( pdd, 1 ));
    }

    @Test
    public void isAfterCodeListingTrue1()
    {   // a 3 token list: [-code][cr][], so should return true.
        Token tok = new Token( new Source(), TokenType.COMMAND, "[-code]", "[-code]", parm );
        TokenList tl = new TokenList();
        tl.add( tok );

        tok = new Token( new Source(), TokenType.COMMAND, "[cr]", "[cr]", parm );
        tl.add( tok );

        tok = new Token( new Source(), TokenType.COMMAND, "[]", "[]", parm );
        tl.add( tok );

        gdd.setInputTokens( tl );
        assertEquals( true, cr.isAfterCodeListing( pdd, 2 ));
    }

    @Test
    public void testValidProcess()
    {   // a 2-token list: [-url][cr][], so should add [] to output paragraph.
        TokenList tl = new TokenList();
        PdfOutfile outfile = new PdfOutfile();
        outfile.setItPara( new Paragraph( ));
        pdd.setOutfile( outfile );

        int count = outfile.getItPara().size();

        tok = new Token( new Source(), TokenType.COMMAND, "[-url]", "[-url]", parm );
        tl.add( tok );

        tok = new Token( new Source(), TokenType.COMMAND, "[]", "[]", parm );
        tl.add( tok );

        gdd.setInputTokens( tl );
        cr.process( pdd, new Token( new Source(), TokenType.COMMAND, "[]", "[]", parm ), 1 );
        int newCount = outfile.getItPara().size();
        assertTrue( newCount > count );
    }
}