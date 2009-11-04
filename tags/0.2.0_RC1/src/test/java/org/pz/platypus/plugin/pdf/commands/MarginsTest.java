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
import org.pz.platypus.plugin.pdf.DefaultValues;
import org.pz.platypus.plugin.pdf.Limits;
import org.pz.platypus.test.mocks.MockLogger;
import org.pz.platypus.test.mocks.MockLiterals;

import java.util.logging.Level;

/**
 * Test processing of changing page sizes. Also testing pageheight and pagewidth commands.
 *
 * @author alb
 */
public class MarginsTest
{
    private GDD gdd;
    private PdfData pdd;

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
    public void testInitialMargins()
    {
        float lm = pdd.getMarginLeft();
        float rm = pdd.getMarginRight();
        float tm = pdd.getMarginTop();
        float bm = pdd.getMarginBottom();

        assertEquals( DefaultValues.MARGIN, lm, 0.5f );
        assertEquals( DefaultValues.MARGIN, rm, 0.5f );
        assertEquals( DefaultValues.MARGIN, tm, 0.5f );
        assertEquals( DefaultValues.MARGIN, bm, 0.5f );

        assertEquals( 0, pdd.getMarginLeftLine().getLineNumber() );
        assertEquals( 0, pdd.getMarginRightLine().getLineNumber() );
        assertEquals( 0, pdd.getMarginBottomLine().getLineNumber() );
        assertEquals( 0, pdd.getMarginTopLine().getLineNumber() );
    }

    @Test
    public void testValidMarginChanges()
    {
        PdfMarginRight  RM = new PdfMarginRight();
        PdfMarginLeft   LM = new PdfMarginLeft();
        PdfMarginTop    TM = new PdfMarginTop();
        PdfMarginBottom BM = new PdfMarginBottom();

        // set margin size
        CommandParameter cp = new CommandParameter();
        cp.setAmount( 40f );
        cp.setUnit( UnitType.POINT );

        // right margin
        RM.process( pdd,
                    new Token( new Source( 5, 6 ), TokenType.COMMAND, "[rmargin:", "[rmargin:", cp ),
                    23 );

        assertEquals( 40f, pdd.getMarginRight(), 0.5f );
        assertEquals( 6, pdd.getMarginRightLine().getLineNumber() );

        // left margin
        cp.setAmount( 44f );
        LM.process( pdd,
                    new Token( new Source( 5, 7 ), TokenType.COMMAND, "[lmargin:", "[lmargin:", cp ),
                    23 );

        assertEquals( 44f, pdd.getMarginLeft(), 0.5f );
        assertEquals( 7, pdd.getMarginLeftLine().getLineNumber() );

        // top margin
        cp.setAmount( 48f );
        TM.process( pdd,
                    new Token( new Source( 5, 8 ), TokenType.COMMAND, "[tmargin:", "[tmargin:", cp ),
                    23 );

        assertEquals( 48f, pdd.getMarginTop(), 0.5f );
        assertEquals( 8, pdd.getMarginTopLine().getLineNumber() );

        // bottom margin
        cp.setAmount( 52f );
        BM.process( pdd,
                    new Token( new Source( 5, 9 ), TokenType.COMMAND, "[bmargin:", "[bmargin:", cp ),
                    23 );

        assertEquals( 52f, pdd.getMarginBottom(), 0.5f );
        assertEquals( 9, pdd.getMarginBottomLine().getLineNumber() );                
    }

    @Test
    public void testInvalidRightMarginOfLessThanZero()
    {
        PdfMarginRight  RM = new PdfMarginRight();
        assertEquals( DefaultValues.MARGIN, pdd.getMarginRight(), 0.5f );

        PdfMarginLeft   LM = new PdfMarginLeft();
        PdfMarginTop    TM = new PdfMarginTop();
        PdfMarginBottom BM = new PdfMarginBottom();

        // set margin to invalid size (-25pts)
        CommandParameter cp = new CommandParameter();
        cp.setAmount( -25f );
        cp.setUnit( UnitType.POINT );

        // right margin
        RM.process( pdd,
                    new Token( new Source( 5, 6 ), TokenType.COMMAND, "[rmargin:", "[rmargin:", cp ),
                    23 );

        //Make sure an error message was generated
        MockLogger ml = (MockLogger) gdd.getLogger();
        assertTrue( ml.getMessage().contains( "ERROR.INVALID_RIGHT_MARGIN" ));
        ml.setMessage( null );
        assertNull( ml.getMessage() );

        //Make sure the invalid value did not actually change the margin setting.
        assertEquals( DefaultValues.MARGIN, pdd.getMarginRight(), 0.5f );
        assertEquals( 0, pdd.getMarginRightLine().getLineNumber() );
    }

    @Test
    public void testInvalidLeftMarginOfLessThanZero()
    {
        PdfMarginLeft LM = new PdfMarginLeft();
        assertEquals( DefaultValues.MARGIN, pdd.getMarginLeft(), 0.5f );

        // set margin to invalid size (-25pts)
        CommandParameter cp = new CommandParameter();
        cp.setAmount( -30f );
        cp.setUnit( UnitType.POINT );

        // right margin
        LM.process( pdd,
                    new Token( new Source( 5, 7 ), TokenType.COMMAND, "[lmargin:", "[lmargin:", cp ),
                    23 );

        //Make sure an error message was generated
        MockLogger ml = (MockLogger) gdd.getLogger();
        assertTrue( ml.getMessage().contains( "ERROR.INVALID_LEFT_MARGIN" ));
        ml.setMessage( null );

        //Make sure the invalid value did not actually change the margin setting.
        assertEquals( DefaultValues.MARGIN, pdd.getMarginLeft(), 0.5f );
        assertEquals( 0, pdd.getMarginLeftLine().getLineNumber() );
    }

    @Test
    public void testInvalidTopMarginOfLessThanZero()
    {
        PdfMarginTop  TM = new PdfMarginTop();
        assertEquals( DefaultValues.MARGIN, pdd.getMarginTop(), 0.5f );

        // set margin to invalid size (-35pts)
        CommandParameter cp = new CommandParameter();
        cp.setAmount( -35f );
        cp.setUnit( UnitType.POINT );

        // right margin
        TM.process( pdd,
                    new Token( new Source( 5, 8 ), TokenType.COMMAND, "[tmargin:", "[tmargin:", cp ),
                    23 );

        //Make sure an error message was generated
        MockLogger ml = (MockLogger) gdd.getLogger();
        assertTrue( ml.getMessage().contains( "ERROR.INVALID_TOP_MARGIN" ));
        ml.setMessage( null );
        assertNull( ml.getMessage() );

        //Make sure the invalid value did not actually change the margin setting.
        assertEquals( DefaultValues.MARGIN, pdd.getMarginTop(), 0.5f );
        assertEquals( 0, pdd.getMarginTopLine().getLineNumber() );
    }

    @Test
    public void testInvalidBottomMarginOfLessThanZero()
    {
        PdfMarginBottom BM = new PdfMarginBottom();
        assertEquals( DefaultValues.MARGIN, pdd.getMarginBottom(), 0.5f );

        // set margin to invalid size (-40pts)
        CommandParameter cp = new CommandParameter();
        cp.setAmount( -40f );
        cp.setUnit( UnitType.POINT );

        // right margin
        BM.process( pdd,
                    new Token( new Source(5, 9), TokenType.COMMAND, "[bmargin:", "[bmargin:", cp ),
                    23 );

        //Make sure an error message was generated
        MockLogger ml = (MockLogger) gdd.getLogger();
        assertTrue( ml.getMessage().contains( "ERROR.INVALID_BOTTOM_MARGIN" ));
        ml.setMessage( null );

        //Make sure the invalid value did not actually change the margin setting.
        assertEquals( DefaultValues.MARGIN, pdd.getMarginBottom(), 0.5f );
        assertEquals( 0, pdd.getMarginBottomLine().getLineNumber() );
    }

@Test
    public void testInvalidTopMarginOfGreaterThanMax()
    {
        PdfMarginTop  TM = new PdfMarginTop();
        assertEquals( DefaultValues.MARGIN, pdd.getMarginTop(), 0.5f );

        // set margin to invalid size ( 2pts bigger than maximum height )
        CommandParameter cp = new CommandParameter();
        cp.setAmount( Limits.PAGE_HEIGHT_MAX + 2f );
        cp.setUnit( UnitType.POINT );

        // right margin
        TM.process( pdd,
                    new Token( new Source(5, 10), TokenType.COMMAND, "[tmargin:", "[tmargin:", cp ),
                    23 );

        //Make sure an error message was generated
        MockLogger ml = (MockLogger) gdd.getLogger();
        assertTrue( ml.getMessage().contains( "ERROR.INVALID_TOP_MARGIN" ));
        ml.setMessage( "" );
        assertEquals( "", ml.getMessage() );

        //Make sure the invalid value did not actually change the margin setting.
        assertEquals( DefaultValues.MARGIN, pdd.getMarginTop(), 0.5f );
        assertEquals( 0, pdd.getMarginTopLine().getLineNumber() );
    }

    @Test
    public void testInvalidLeftMarginOfGreaterThanMax()
    {
        PdfMarginLeft LM = new PdfMarginLeft();
        assertEquals( DefaultValues.MARGIN, pdd.getMarginLeft(), 0.5f );

        // set margin to invalid size ( 2pts bigger than maximum width )
        CommandParameter cp = new CommandParameter();
        cp.setAmount( Limits.PAGE_WIDTH_MAX + 2f );
        cp.setUnit( UnitType.POINT );

        // right margin
        LM.process( pdd,
                    new Token( new Source(5, 11), TokenType.COMMAND, "[lmargin:", "[lmargin:", cp ),
                    23 );

        //Make sure an error message was generated
        MockLogger ml = (MockLogger) gdd.getLogger();
        assertTrue( ml.getMessage().contains( "ERROR.INVALID_LEFT_MARGIN" ));
        ml.setMessage( "" );
        assertEquals( "", ml.getMessage() );

        //Make sure the invalid value did not actually change the margin setting.
        assertEquals( DefaultValues.MARGIN, pdd.getMarginLeft(), 0.5f );
        assertEquals( 0, pdd.getMarginLeftLine().getLineNumber() );
    }
}