/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.*;
import org.pz.platypus.command.Alignment;
import org.pz.platypus.exceptions.FileCloseException;
import org.pz.platypus.test.mocks.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.ArrayList;


public class PdfOutfileTest
{
    PdfOutfile pout;
    GDD gdd;
    PdfData pdat;

    @Before
    public void setUp()
    {
        gdd = new GDD();
        gdd.initialize();
        gdd.setLogger( new MockLogger() );
        gdd.getLogger().setLevel( Level.OFF );

        MockLiterals mockLits = new MockLiterals();
        mockLits.setGetLitShouldFindLookupString( true );
        gdd.setLits( mockLits );

        pout = new PdfOutfile();
        pdat = new PdfData( gdd );
        pout.setPdfData( pdat );
    }

    @Test
    public void testConstructor()
    {
        assertFalse( pout.isOpen() );
    }

    @Test(expected = IOException.class)
    public void testNullFilenameOpen() throws IOException
    {
        pout.open( gdd, null, null );
    }

    @Test(expected = IOException.class)
    public void testEmptyFilenameOpen() throws IOException
    {
        pout.open( gdd, "", null );
    }

    @Test(expected = IOException.class)
    public void testEmptyFileForOpenPdfFile() throws IOException
    {
        pout.openPdfFile( gdd, pdat, "" );
    }

    // next test should return the literal key for the error lookup in the
    // literals file, when an unopened outfile is closed.
    @Test
    public void testIllegalClose() throws FileCloseException
    {
        pout.close();
        assertEquals( gdd.getLogger().toString(), "NO_OUTPUT_FILE" );
    }

    @Test
    public void testAddParagraph1()
    {
        class mockPdfWriter extends PdfWriter {}

        final PdfContentByte pcb = new PdfContentByte( new mockPdfWriter());

        class mockColumnText extends ColumnText {
            mockColumnText() { super( pcb ); }
            public int getElementsCount() {
                if( compositeElements == null ) {
                    return( 0 );
                }
                return( compositeElements.size() );
            }
        }

        mockColumnText mockCt = new mockColumnText();
        assertEquals( 0, mockCt.getElementsCount() );

        Paragraph par = new Paragraph( "test string" );
        pout.addParagraph( par, mockCt );
        assertEquals( 1, mockCt.getElementsCount() );
    }

    @Test
    public void testNewPageLowLevel()
    {
        assertFalse( pout.newPageLowLevel() );
    }

    @Test
    public void testComputeBottomColEdgeFor1Column()
    {
        Columns cols = new Columns( pdat );
        pdat.setColumns( cols );

        float topColEdge = DefaultValues.PAGE_HEIGHT - DefaultValues.MARGIN;
        assertEquals( DefaultValues.MARGIN, pout.computeBottomColEdge( topColEdge ), 0.05f );
    }

    @Test
    public void testComputeLeftColEdgeFor1Column()
    {
        Columns cols = new Columns( pdat );
        pdat.setColumns( cols );

        float correctLeftColEdge = DefaultValues.MARGIN;
        assertEquals( correctLeftColEdge, pout.computeLeftColEdge(), 0.05f );
    }

    @Test
    public void testComputeRightColEdgeFor1Column()
    {
        Columns cols = new Columns( pdat );
        pdat.setColumns( cols );

        float leftColEdge = pout.computeLeftColEdge();
        float correctRightColEdge = DefaultValues.PAGE_WIDTH - DefaultValues.MARGIN;
        assertEquals( correctRightColEdge, pout.computeRightColEdge( leftColEdge ), 1f );
    }

    @Test
    public void testComputeTopColEdgeFor1Column()
    {
        Columns cols = new Columns( pdat );
        pdat.setColumns( cols );

        float correctTopColEdge =
             DefaultValues.PAGE_HEIGHT - DefaultValues.MARGIN;
        assertEquals( correctTopColEdge, pout.computeTopColEdge(), 0.05f );
    }

    @Test
    public void testSettingAlignment()
    {
        Paragraph par = new Paragraph( "test string" );

        pdat.setAlignment( Alignment.LEFT, new Source() );
        pout.doParagraphAlignment( par, pdat );
        assertEquals( "expecting left alignment in paragraph",
                      Element.ALIGN_LEFT, par.getAlignment());

        pdat.setAlignment( Alignment.JUST, new Source() );
        pout.doParagraphAlignment( par, pdat );
        assertEquals( "expecting justified alignment in paragraph",
                      Element.ALIGN_JUSTIFIED, par.getAlignment());

        pdat.setAlignment( Alignment.CENTER, new Source() );
        pout.doParagraphAlignment( par, pdat );
        assertEquals( "expecting center alignment in paragraph",
                      Element.ALIGN_CENTER, par.getAlignment());

        pdat.setAlignment( Alignment.RIGHT, new Source() );
        pout.doParagraphAlignment( par, pdat );
        assertEquals( "expecting right alignment in paragraph",
                      Element.ALIGN_RIGHT, par.getAlignment());

        pdat.setAlignment( -1, new Source() );     // invalid alignment
        pout.doParagraphAlignment( par, pdat );
        assertEquals( "expecting center alignment in paragraph", // should be unchanged
                      Element.ALIGN_RIGHT, par.getAlignment());
    }

    @Test
    public void testFirstLineNoIndent()
    {
        Paragraph par = new Paragraph( "test string" );
        pdat.setNoIndent( true, new Source() );
        float indent = pout.doFirstLineIndent( par, pdat );
        assertEquals( "indent s/be 0 on no-indent", 0f, indent, 0.5f );

        assertFalse( "no-indent s/be reset to off", pdat.getNoIndent() );
    }

    @Test
    public void testFirstLineIndent()
    {
        Paragraph par = new Paragraph( "test string" );
        pdat.setNoIndent( false, new Source() );
        pdat.setFirstLineIndent( 40f, new Source() );
        float indent = pout.doFirstLineIndent( par, pdat );
        assertEquals( "indent s/be 40", 40f, indent, 0.5f );
    }

    @Test
    public void testChangedLeftRightMarginsToInvalidExcessSize()
    {
        // this open will fail, but it will init key pout structures before failing.
        try {
        pout.openPdfFile( gdd, pdat, "" );
        }
        catch( IOException ioe ) {
            ; //do nothing.
        }
        pdat.setMarginLeft( 2 * pdat.getPageWidth(), new Source() );

        // call the inner class's updateAllMargins() method
        pout.new OnPageEnd().updateAllMargins();

        MockLogger ml = (MockLogger) gdd.getLogger();
        assertTrue( ml.getMessage().contains( "ERROR.LEFT_RIGHT_MARGINS_TOO_BIG" ));
        assertTrue( "bad value should have been reset to current valid value",
                    pdat.getMarginLeft() != 2 * pdat.getPageWidth() );

    }

    @Test
    public void testChangedTopBottomMarginsToInvalidExcessSize()
    {
        // this open will fail, but it will init key pout structures before failing.
        try {
        pout.openPdfFile( gdd, pdat, "" );
        }
        catch( IOException ioe ) {
            ; //do nothing.
        }
        pdat.setMarginBottom( 2 * pdat.getPageHeight(), new Source() );

        // call the inner class's updateAllMargins() method
        pout.new OnPageEnd().updateAllMargins();

        MockLogger ml = (MockLogger) gdd.getLogger();
        assertTrue( ml.getMessage().contains( "ERROR.TOP_BOTTOM_MARGINS_TOO_BIG" ));
        assertTrue( "bad value should have been reset to current valid value",
                    pdat.getMarginBottom() != 2 * pdat.getPageHeight() );

    }

    @Test
    public void testStartNewParagraph()
    {
        pdat.setLeading( 26f, new Source() );
        pout.setItPara( null );
        pout.startNewParagraph();
        Paragraph newPar = pout.getItPara();
        assertEquals( 26f, newPar.getLeading(), 0.5f );
    }

    @Test
    public void testEmitText1()
    {
        String msg = "Hello from Unit test!";
        float newLeading = 22.22f;

        pdat.setStrikethru( false, new Source() );
        pdat.getUnderline().setInEffect( false, new Source() );
        pdat.getFont().setToDefault();
        pdat.setLeading( newLeading, new Source() );
        pout.setItPara( null );
        pout.emitText( msg );
        Paragraph para = pout.getItPara();
        ArrayList chunks = para.getChunks();
        assertTrue( chunks.size() > 0 );
        Chunk chunk = (Chunk) chunks.get( 0 );
        assertEquals( msg, chunk.toString() );
        assertEquals( newLeading, para.getLeading(), 0.5f );
    }
}