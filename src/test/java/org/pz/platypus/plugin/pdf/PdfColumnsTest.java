/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf;

import static org.junit.Assert.*;
import org.pz.platypus.GDD;
import org.pz.platypus.Source;
import org.pz.platypus.test.mocks.MockLogger;
import org.pz.platypus.test.mocks.MockLiterals;
import org.junit.Test;
import org.junit.Before;

import java.util.logging.Level;

public class PdfColumnsTest
{
    PdfData pdd;
    GDD gdd;

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
        Columns cols = new Columns( pdd );
        assertNotNull( cols );
        assertEquals( DefaultValues.COLUMN_COUNT, cols.getCount() );
    }

    @Test
    public void testConstructorWithColCount()
    {
        final int numOfCols = 4;
        Columns cols = new Columns( numOfCols, 0f, pdd );
        assertNotNull( cols );
        assertEquals(  numOfCols, cols.getCount() );
    }

    @Test
    public void testCreateColumnsValid()
    {
        final int numOfCols = 3;
        Columns cols = new Columns( pdd );
        assertNotNull( cols );
        int ret = cols.createColumns( numOfCols, 0f, pdd );
        assertEquals( numOfCols, ret );

        Column col = cols.getColumn( 2 );
        assertEquals( 0f, col.getVertSkip(), 0.5f );
    }

    @Test
    public void testCreateColumnsInvalid()
    {
        final int numOfCols = 0;
        Columns cols = new Columns( pdd );
        assertNotNull( cols );
        int ret = cols.createColumns( numOfCols, 0f, pdd );
        // if you ask for an insufficient number of columns, it always creates 1.
        assertEquals( 1, ret );
    }

    @Test
    public void testGetColumnsValid()
    {
        final int numOfCols = 3;
        Columns cols = new Columns( numOfCols, 0f, pdd );
        assertNotNull( cols );

        Column gotColumn = cols.getColumn( 2 );
        assertNotNull( gotColumn );
    }

    @Test
    public void testGetColumnsInvalid()
    {
        final int numOfCols = 3;
        Columns cols = new Columns( numOfCols, 0f, pdd );
        assertNotNull( cols );

        Column gotColumn = cols.getColumn( -1 );
        assertNull( gotColumn );
    }

    @Test
    public void testRecommendedGutter()
    {
        final int numOfCols = 3;
        final float correctGutter = 8f;
        Columns cols = new Columns( numOfCols, 0f, pdd );
        assertNotNull( cols );

        Column gotColumn = cols.getColumn( 1 );
        assertEquals( correctGutter, gotColumn.getGutter(), 0.05f );
    }

    @Test
    public void testRecommendedGutterWhenManyCols()
    {
        final int numOfCols = 12;
        final float correctGutter = 6f;
        Columns cols = new Columns( numOfCols, 0f, pdd );
        assertNotNull( cols );

        Column gotColumn = cols.getColumn( 1 );
        assertEquals( correctGutter, gotColumn.getGutter(), 0.05f );
    }

    @Test
    public void testDump()
    {
        final int numOfCols = 3;
        Columns cols = new Columns( numOfCols, 0f, pdd );
        assertNotNull( cols );

        assertTrue( cols.dump( gdd ).endsWith( "3" ));
    }
}