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
import org.pz.platypus.PropertyFile;
import org.pz.platypus.test.mocks.MockLiterals;
import org.pz.platypus.test.mocks.MockLogger;

import java.util.logging.Level;

public class PdfSymbolsTableTest
{
    PdfOutfile pout;
    GDD gdd;
    PdfData pdat;
    PdfSymbolsTable pst;

    @Before
    public void setUp()
    {
        gdd = new GDD();
        gdd.initialize();
        gdd.setLogger( new MockLogger() );
        MockLiterals mockL = new MockLiterals();
        mockL.setGetLitShouldReturnKey( true );
        gdd.setLits( mockL );
        gdd.getLogger().setLevel( Level.OFF );

        pst = new PdfSymbolsTable( gdd );

        pout = new PdfOutfile();
        pdat = new PdfData( gdd );
        pout.setPdfData( pdat );
    }

    @Test
    public void testComputePropFilenameEmpty()
    {
        assertTrue( pst.computePropFilename( gdd ).isEmpty() );
    }

    @Test
    public void testComputePropFilenameEntry()
    {
        gdd.setOutputPluginPrefix( "PDF" );
        gdd.setConfigFile( new PropertyFile( "abcd", gdd ));
        assertTrue( pst.computePropFilename( gdd ).isEmpty() );
        MockLogger mockL = (MockLogger) gdd.getLogger();
        assertTrue( mockL.getMessage().startsWith( "ERROR.INVALID_PLUGIN_URL" ));
    }
}