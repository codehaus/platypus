/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.*;
import org.pz.platypus.plugin.pdf.commands.PdfItalicsOn;
import org.pz.platypus.test.mocks.MockLogger;
import org.pz.platypus.test.mocks.MockLiterals;

import java.util.logging.Level;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.Font;

/**
 * Test of PdfFont objects, which wrap the iText font object
 *
 * @author alb
 */
public class PdfFontTest
{
    private PdfData pd;
    private GDD gdd;
    private PdfFont font;

    @Before
    public void setUp()
    {
        gdd = new GDD();
        gdd.initialize();
        gdd.setLogger( new MockLogger() );
        gdd.setLits( new MockLiterals() );
        gdd.getLogger().setLevel( Level.OFF );
        pd = new PdfData( gdd );

        font = new PdfFont( pd );
    }

    @Test
    public void validSetToDefault()
    {
        assertEquals( DefaultValues.FONT_TYPEFACE, font.getFace() );
        assertEquals( DefaultValues.FONT_SIZE, font.getSize(), 0.01f );
        assertEquals( 0, font.getColor().getR() );
        assertEquals( 0, font.getColor().getG() );
        assertEquals( 0, font.getColor().getB() );

    }

// Commented out because of migration to PdfFontFactory.java. Eventually move to tests for that class.
//    @Test
//    public void isNotBase14FontName()
//    {
//        assertFalse( font.isBase14Font( "muggle" ));
//    }
//
//    @Test
//    public void isBase14FontName()
//    {
//        assertTrue( font.isBase14Font( "SYMBOL" ));
//    }
//
//    @Test
//    public void createItextFontNameTest()
//    {
//        assertEquals( BaseFont.TIMES_ROMAN, font.createItextFontName( font ));
//    }
//
//    @Test
//    public void computeItextStyle()
//    {
//        assertEquals( 0, font.computeItextStyle() );
//    }
//
//    @Test
//    public void createItextFontFromDefault()
//    {
//        Font iTextFont = font.createFont( font );
//        assertFalse( iTextFont.isBold() );
//        assertFalse( iTextFont.isItalic() );
//        assertEquals( DefaultValues.FONT_SIZE, iTextFont.getCalculatedSize(), 0.01f );
//        assertEquals( "Times", iTextFont.getFamilyname() );
//    }

    @Test
    public void setItalicsTest()
    {
        BaseFont bf;
        bf = font.getItextFont().getBaseFont();
        assertEquals(  "Times Roman", bf.getFullFontName()[0][3] );

        font.setItalics( true, new Source() );
        bf = font.getItextFont().getBaseFont();
        assertEquals(  "Times Italic", bf.getFullFontName()[0][3] );
    }

    @Test
    public void setItalicsOnByCommandTest()
    {
        BaseFont bf;
        bf = font.getItextFont().getBaseFont();
        assertEquals(  "Times Roman", bf.getFullFontName()[0][3] );

        pd.setFont( font );
        PdfItalicsOn italOnCommand = new PdfItalicsOn();
        italOnCommand.process( pd, new Token( new Source(), TokenType.COMMAND, "[+i]", "", null ), 1 );
        bf = font.getItextFont().getBaseFont();
        assertEquals(  "Times Italic", bf.getFullFontName()[0][3] );
    }

    @Test
    public void setBoldTest()
    {
        BaseFont bf;
        bf = font.getItextFont().getBaseFont();
        assertEquals(  "Times Roman", bf.getFullFontName()[0][3] );

        font.setBold( true, new Source() );
        bf = font.getItextFont().getBaseFont();
        assertEquals(  "Times Bold", bf.getFullFontName()[0][3] );
    }
}
