/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.*;
import org.pz.platypus.test.mocks.MockLogger;
import org.pz.platypus.test.mocks.MockLiterals;

import java.util.logging.Level;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.Font;

/**
 * Test of PdfFontFactory, which creates the iText font object
 *
 * @author alb
 */
public class PdfFontFactoryTest
{
    private PdfData pdd;
    private GDD gdd;
    private PdfFontFactory ff;
    private PdfFont pfont;

    @Before
    public void setUp()
    {
        gdd = new GDD();
        gdd.initialize();
        gdd.setLogger( new MockLogger() );
        gdd.setLits( new MockLiterals() );
        gdd.getLogger().setLevel( Level.OFF );
        pdd = new PdfData( gdd );

        ff = new PdfFontFactory( gdd, pdd );
    }

    @Test
    public void isNotBase14FontName()
    {
        assertFalse( ff.isBase14Font( "muggle" ));
    }

    @Test
    public void isBase14FontName()
    {
        assertTrue( ff.isBase14Font( "SYMBOL" ));
    }

    @Test
    public void createItextFontNameTest()
    {
        assertEquals( BaseFont.TIMES_ROMAN, ff.createItextFontName( new PdfFont( pdd ) ));
    }

    @Test
    public void computeItextStyle()
    {
        assertEquals( 0, ff.computeItextStyle( new PdfFont( pdd )));
    }

    @Test
    public void computeItextStyleBold()
    {
        PdfFont pdff = new PdfFont( pdd );
        pdff.setBold( true, new Source() );
        assertEquals( Font.BOLD, ff.computeItextStyle( pdff ));
    }

    @Test
    public void computeBase14ItextFontNameTestSymbol()
    {
        PdfFont pdff = new PdfFont( pdd );
        pdff.setFace( "SYMBOL", new Source() );
        assertEquals( BaseFont.SYMBOL, ff.computeBase14ItextFontName( pdff ));
    }

    @Test
    public void computeBase14ItextFontNameTestCourierOblique()
    {
        PdfFont pdff = new PdfFont( pdd );
        pdff.setFace( "COURIER", new Source() );
        pdff.setItalics( true, new Source() );
        assertEquals( BaseFont.COURIER_OBLIQUE, ff.computeBase14ItextFontName( pdff ));
    }

    @Test
    public void createItextFontFromDefault()
    {
        Font iTextFont = ff.createItextFont( new PdfFont( pdd ));
        assertFalse( iTextFont.isBold() );
        assertFalse( iTextFont.isItalic() );
        assertEquals( DefaultValues.FONT_SIZE, iTextFont.getCalculatedSize(), 0.01f );
        assertEquals( "Times", iTextFont.getFamilyname() );
    }

    @Test
    public void createItextFontFromNull()
    {
        // if null is given as the font name, return a Times family font (namely, Times Roman)
        Font iTextFont = ff.createItextFont( null );
        assertEquals( "Times", iTextFont.getFamilyname());        
    }

    @Test
    public void isRegisteredWithItextYesTest()
    {
        // 'symbol' is one of the base14 fonts for PDFs, and so is always registered in iText.
        assertTrue( ff.isRegisteredWithItext( "symbol" ));
    }

    @Test
    public void isRegisteredWithItextNoTest()
    {
        assertFalse( ff.isRegisteredWithItext( "no-such-font" ));
    }
}