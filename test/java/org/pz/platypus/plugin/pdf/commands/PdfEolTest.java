/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.junit.*;
import org.pz.platypus.GDD;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.plugin.pdf.*;
import org.pz.platypus.plugin.pdf.commands.PdfEol;
import org.pz.platypus.test.mocks.*;

import java.util.logging.Level;

/**
 * Test hanlding of EOL in PDFs
 *
 * @author alb
 */
public class PdfEolTest
{
    GDD gdd;
    OutputContextable pdfData;
    PdfData pData;
    PdfEol pEol;
    PdfOutfile outfile;

    @Before
    public void setUp()
    {
        gdd = new GDD();
        gdd.initialize();
        gdd.setLogger( new MockLogger() );
        gdd.setLits( new MockLiterals() );
        gdd.getLogger().setLevel( Level.OFF );

        pdfData = new PdfData( gdd );
        pData = (PdfData) pdfData;

        outfile = new PdfOutfile();
        pData.setOutfile( outfile );
        outfile.setPdfData( pData );

        pEol = new PdfEol();
    }
    
    @Test (expected=IllegalArgumentException.class)
    public void processException() throws IllegalArgumentException
    {
        pEol.process( null, null, 0 );
    }
}
