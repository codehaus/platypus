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
import org.pz.platypus.GDD;
import org.pz.platypus.exceptions.InvalidConfigFileException;
import org.pz.platypus.test.mocks.MockLiterals;
import org.pz.platypus.test.mocks.MockLogger;

import java.io.IOException;
import java.util.logging.Level;


public class PdfStartTest
{
    PdfOutfile pout;
    PdfCommandTable ctable;
    GDD gdd;
    PdfData pdat;
    Start start;

    @Before
    public void setUp()
    {
        gdd = new GDD();
        gdd.initialize();
        gdd.setLogger( new MockLogger() );
        gdd.setLits( new MockLiterals() );
        gdd.getLogger().setLevel( Level.OFF );

        pout = new PdfOutfile();
        pdat = new PdfData( gdd );
        pout.setPdfData( pdat );

        ctable = new PdfCommandTable();
    }

    @Test
    public void testSetUpDataItems()
    {
        start = new Start( pout, ctable );
        start.setUpDataItems( gdd );
        // the above method should not complete, due to inability to finish setting up data items,
        // so ctable size should be 0, as it never loaded the commands
        assertEquals( 0, ctable.getSize() );
    }

    @Test (expected = IOException.class)
    public void testProcessTextWithEmptyOutputFile() throws IOException
    {
        start = new Start( pout, ctable );
        start.setUpDataItems( gdd );
        start.processText( gdd, "", "text from testProcessTextWithEmptyOutputFile()" );
    }

    @Test (expected = IOException.class)
    public void testProcessTextWithNullOutputFile() throws IOException
    {
        start = new Start( pout, ctable );
        start.setUpDataItems( gdd );
        start.processText( gdd, null, "text from testProcessTextWithNullOutputFile()" );
    }
}