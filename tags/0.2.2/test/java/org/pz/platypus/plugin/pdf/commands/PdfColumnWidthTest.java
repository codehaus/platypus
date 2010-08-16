/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import org.pz.platypus.*;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.plugin.pdf.Columns;
import org.pz.platypus.test.mocks.MockLogger;
import org.pz.platypus.test.mocks.MockLiterals;
import org.junit.Test;
import org.junit.Before;

import java.util.logging.Level;

/**
 * Test the capability to specify the width of a column.
 *
 * @author alb
 */

public class PdfColumnWidthTest
{
    PdfData pdd;
    GDD gdd;
    PdfColumnWidth colWidth;

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
        pdd.setUserSpecifiedColumnWidth( 0f, new Source() );
    }

    // ==== actual tests start here ===== //

    @Test
    public void testConstructor()
    {
        colWidth = new PdfColumnWidth();
        assertNotNull( colWidth );
        assertEquals( "[columnwidth:", colWidth.getRoot() );
    }

    @Test
    public void testColumnSizeTooBig()
    {
        final int numOfCols = 1;
        Columns cols = new Columns( numOfCols, 0f, pdd );
        assertEquals(  numOfCols, cols.getCount() );

        float textAreaWidth =  pdd.getPageWidth() - pdd.getMarginLeft() - pdd.getMarginRight();

        // make sure column width is set correctly before we start
        assertEquals( cols.getColumn( 0 ).getWidth(), textAreaWidth, 0.1f );

        colWidth = new PdfColumnWidth();

        // create a command parameter of column width = 2 x text area width
        CommandParameter cp = new CommandParameter();
        cp.setAmount( textAreaWidth * 2 );
        cp.setUnit( UnitType.POINT );

        colWidth.process( pdd,
                          new Token( new Source(), TokenType.COMMAND, colWidth.getRoot(), "", cp),
                          2 );

        // should result in an error message in the logger
        MockLogger mockLog = (MockLogger) gdd.getLogger();
        assertTrue( mockLog.getMessage().contains( "NEW_COLUMN_SIZE_IGNORED" ));
    }

    @Test
    public void testColumnSizeTooSmall()
    {
        final int numOfCols = 1;
        Columns cols = new Columns( numOfCols, 0f, pdd );
        assertEquals(  numOfCols, cols.getCount() );

        colWidth = new PdfColumnWidth();

        // create a command parameter of column width that's less than zero. S/result in error.
        CommandParameter cp = new CommandParameter();
        cp.setAmount( -99 );
        cp.setUnit( UnitType.POINT );

        colWidth.process( pdd,
                          new Token( new Source(), TokenType.COMMAND, colWidth.getRoot(), "", cp),
                          2 );

        // should result in an error message in the logger
        MockLogger mockLog = (MockLogger) gdd.getLogger();
        assertTrue( mockLog.getMessage().contains( "ERROR.COLUMN_WIDTH_CANNOT_BE_NEGATIVE" ));
    }

    @Test
    public void testValidColumnSize()
    {
        final int numOfCols = 1;
        Columns cols = new Columns( numOfCols, 0f, pdd );
        assertEquals(  numOfCols, cols.getCount() );

        colWidth = new PdfColumnWidth();

        // create a command parameter of column width that's less than zero. S/result in error.
        final float validWidth = 200f;
        CommandParameter cp = new CommandParameter();
        cp.setAmount( validWidth );
        cp.setUnit( UnitType.POINT );

        colWidth.process( pdd,
                          new Token( new Source(), TokenType.COMMAND, colWidth.getRoot(), "", cp),
                          2 );

        assertEquals( validWidth, pdd.getUserSpecifiedColumnWidth(), 0.1f );
    }
}