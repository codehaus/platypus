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
import org.pz.platypus.DefaultValues;
import org.pz.platypus.plugin.pdf.PdfOutfile;
import org.pz.platypus.test.mocks.MockLogger;
import org.pz.platypus.test.mocks.MockLiterals;

import java.util.logging.Level;

import com.lowagie.text.Paragraph;
import com.lowagie.text.Chunk;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;


/**
 * Test processing of changing font size. Also tests corresponding changes in leading.
 *
 * @author alb
 */
public class FontSizeTest
{
    private GDD gdd;
    private PdfData pdd;
    private Token tok;
    private CommandParameter parm;
    private PdfFsize fsize;

    @Before
    public void setUp()
    {
        gdd = new GDD();
        gdd.initialize();
        gdd.setLogger( new MockLogger() );
        gdd.setLits( new MockLiterals() );
        gdd.getLogger().setLevel( Level.OFF );
        pdd = new PdfData( gdd );
        pdd.setOutfile( new PdfOutfile() );
        fsize = new PdfFsize();
    }

    @Test
    public void testInitialFontSize()
    {
        assertEquals( DefaultValues.FONT_SIZE, pdd.getFontSize(), 0.5f );
    }

    @Test
    public void testNewEqualFontSizeDoesntChangeAnything()
    {
        // set the font size to the existing value using a different line number.
        // make sure that the original line number is preserved, indicating no
        // change was made (due to the fact that the new font size = old font size)
        assertEquals( 0, pdd.getFont().getSource().getLineNumber() );

        parm = new CommandParameter();
        parm.setAmount( DefaultValues.FONT_SIZE );
        parm.setUnit( UnitType.POINT );

        Source src = new Source( 5, 6 );
        tok = new Token( src, TokenType.COMMAND, "[font|size:",
                         "[font|size:" + DefaultValues.FONT_SIZE + "]", parm );
        fsize.process( pdd, tok, 2 );

        assertEquals( 0, pdd.getFont().getSource().getLineNumber() );
    }

    @Test
    public void testValidFontSize()
    {
        float newFontSize = 18f;

        assertEquals( DefaultValues.FONT_SIZE, pdd.getFontSize(), 0.1f );
        assertTrue( newFontSize != pdd.getFontSize() );
        assertEquals( 0, pdd.getFont().getSource().getLineNumber(), 0.1f );

        parm = new CommandParameter();
        parm.setAmount( newFontSize );
        parm.setUnit( UnitType.POINT );

        Source src = new Source( 5, 6 );
        pdd.getOutfile().setItPara( new Paragraph() );
        tok = new Token( src, TokenType.COMMAND, "[font|size:",
                         "[font|size:" + DefaultValues.FONT_SIZE + "]", parm );
        fsize.process( pdd, tok, 2 );

        assertEquals( 6, pdd.getFont().getSource().getLineNumber() );
        assertEquals( newFontSize, pdd.getFontSize(), 0.1f );
    }

    @Test
    public void testLeadingEffectWhenParagraphEmpty()
    {
        float newFontSize = 18f;

        assertEquals( DefaultValues.FONT_SIZE, pdd.getFontSize(), 0.1f );
        assertTrue( newFontSize != pdd.getFontSize() );
        assertEquals( 0, pdd.getFont().getSource().getLineNumber(), 0.1f );

        parm = new CommandParameter();
        parm.setAmount( newFontSize );
        parm.setUnit( UnitType.POINT );

        Source src = new Source( 5, 6 );

        pdd.getOutfile().setItPara( new Paragraph() );
        tok = new Token( src, TokenType.COMMAND, "[font|size:",
                         "[font|size:" + DefaultValues.FONT_SIZE + "]", parm );
        fsize.process( pdd, tok, 2 );

        Paragraph para = pdd.getOutfile().getItPara();
        assertEquals( newFontSize * DefaultValues.LEADING_TO_FONT_SIZE_RATIO,
                      para.getLeading(), 0.1f );
        assertEquals( newFontSize * DefaultValues.LEADING_TO_FONT_SIZE_RATIO,
                      pdd.getLineHeight(), 0.1f );
        assertEquals( 6, pdd.getLineHeightLine().getLineNumber() );
    }

    @Test
    public void testLeadingEffectWhenParagraphNotEmpty()
    {
        float newFontSize = 20f;

        assertEquals( DefaultValues.FONT_SIZE, pdd.getFontSize(), 0.1f );
        assertTrue( newFontSize != pdd.getFontSize() );
        assertEquals( 0, pdd.getFont().getSource().getLineNumber(), 0.1f );

        parm = new CommandParameter();
        parm.setAmount( newFontSize );
        parm.setUnit( UnitType.POINT );

        Source src = new Source( 5, 6 );

        pdd.getOutfile().setItPara( new Paragraph() );
        pdd.getOutfile().getItPara().add( new Chunk( "I dig a platypus" ));
        tok = new Token( src, TokenType.COMMAND, "[font|size:",
                         "[font|size:" + DefaultValues.FONT_SIZE + "]", parm );
        fsize.process( pdd, tok, 2 );
        pdd.getOutfile().getItPara().add( new Chunk( "I dig a platypus--the sequel" ));

        // this should work because when the font is changed to a larger size
        // than the previous text, the whole paragraph is reset to the larger size
        Paragraph para = pdd.getOutfile().getItPara();
        assertEquals( newFontSize * DefaultValues.LEADING_TO_FONT_SIZE_RATIO,
                      para.getLeading(), 0.1f );
        assertEquals( newFontSize * DefaultValues.LEADING_TO_FONT_SIZE_RATIO,
                      pdd.getLineHeight(), 0.1f );
        assertEquals( 6, pdd.getLineHeightLine().getLineNumber() );
    }

    @Test
    public void testLeadingFontSizeChangeIsSlight()
    {
        float newFontSize = DefaultValues.FONT_SIZE + 1f;

        assertEquals( DefaultValues.FONT_SIZE, pdd.getFontSize(), 0.1f );
        assertTrue( newFontSize != pdd.getFontSize() );
        assertEquals( 0, pdd.getFont().getSource().getLineNumber(), 0.1f );

        parm = new CommandParameter();
        parm.setAmount( newFontSize );
        parm.setUnit( UnitType.POINT );

        Source src = new Source( 5, 6 );

        pdd.getOutfile().setItPara( new Paragraph() );
        Paragraph para = pdd.getOutfile().getItPara();
        float paraLeading = para.getLeading();
        assertEquals( 16, paraLeading, 0.1f );  // the iText default

        para.add( new Chunk( "I dig a platypus" ));
        tok = new Token( src, TokenType.COMMAND, "[font|size:",
                         "[font|size:" + DefaultValues.FONT_SIZE + "]", parm );
        fsize.process( pdd, tok, 2 );
        pdd.getOutfile().getItPara().add( new Chunk( "I dig a platypus--the sequel" ));

        // when the font-size change is <= 3pts and < 20% of the existing font's size,
        // the leading does not change. These tests make sure the leading = default,
        // even though we made the font size 1 pt. bigger.

        assertEquals( paraLeading, para.getLeading(), 0.1f );
        assertEquals( DefaultValues.FONT_SIZE * DefaultValues.LEADING_TO_FONT_SIZE_RATIO,
                      pdd.getLineHeight(), 0.1f );
        assertEquals( 0, pdd.getLineHeightLine().getLineNumber() );
    }

    @Test
    public void testLeadingFontSizeChangeIsToLowerFontSize()
    {
        float newFontSize = DefaultValues.FONT_SIZE - 4f;

        assertEquals( DefaultValues.FONT_SIZE, pdd.getFontSize(), 0.1f );
        assertTrue( newFontSize != pdd.getFontSize() );
        assertEquals( 0, pdd.getFont().getSource().getLineNumber(), 0.1f );

        parm = new CommandParameter();
        parm.setAmount( newFontSize );
        parm.setUnit( UnitType.POINT );

        Source src = new Source( 5, 6 );

        pdd.getOutfile().setItPara( new Paragraph() );
        Paragraph para = pdd.getOutfile().getItPara();
        float paraLeading = para.getLeading();
        assertEquals( 16, paraLeading, 0.1f );  // the iText default

        para.add( new Chunk( "I dig a platypus" ));
        tok = new Token( src, TokenType.COMMAND, "[font|size:",
                         "[font|size:" + DefaultValues.FONT_SIZE + "]", parm );
        fsize.process( pdd, tok, 2 );
        pdd.getOutfile().getItPara().add( new Chunk( "I dig a platypus--the sequel" ));

        // when the font-size change is made smaller, the existing paragraph leading is not
        // changed. However, the pdfData setting is changed so that the next paragraph will
        // be leaded at the new smaller size.

        assertEquals( paraLeading, para.getLeading(), 0.1f );
        assertEquals( newFontSize * DefaultValues.LEADING_TO_FONT_SIZE_RATIO,
                      pdd.getLeading(), 0.1f  );

        // following values needed to permit writing the present paragraph to column text

        pdd.getOutfile().setItColumn( new ColumnText( new PdfContentByte( null )));
        pdd.getOutfile().setPdfData( pdd );
        // >>>>>>>>>>>>need to add column to columns in PdfData
       // pdd.setAlignment( Alignment.LEFT, new Source() );

        // start the new paragraph
        pdd.getOutfile().startNewParagraph();

        // are the values as expected?
        Paragraph newPara = pdd.getOutfile().getItPara();
        assertEquals(newFontSize * DefaultValues.LEADING_TO_FONT_SIZE_RATIO,
                      newPara.getLeading(), 0.1f );
        assertTrue( newPara.getLeading() != paraLeading );
    }
}