/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */


package org.pz.platypus.plugin.pdf.commands;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.plugin.common.DocData;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.plugin.pdf.PdfFont;
import org.pz.platypus.plugin.pdf.FormatStack;
import org.pz.platypus.*;
import org.pz.platypus.test.mocks.MockLiterals;
import org.pz.platypus.test.mocks.MockPdfOutfile;

import java.util.logging.Level;


/**
 * Test PDF handling of code listings
 *
 * @author alb
 */
public class PdfCodeWithOptionsTest
{
    PdfCodeWithOptions pdfListing;

    @Before
    public void setUp()
    {
        pdfListing = new PdfCodeWithOptions();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSuperclassErrorThrowsException()
    {
        assertEquals( 0, pdfListing.process( null, null, 6 ));
    }

    @Test
    public void testSuperClassErrorReturns0()
    {
        final GDD gdd = new GDD();
        gdd.initialize();
        gdd.setLits( new MockLiterals() );
        gdd.setupLogger( ( "org.pz.platypus.Platypus" ));
        gdd.getLogger().setLevel( Level.OFF );

        class DocumentData extends DocData
        {
            public DocumentData() { super( gdd ); }
        };

        DocumentData docData = new DocumentData();

        CommandParameter cp = new CommandParameter();
        cp.setString( null );

        assertEquals( 0, pdfListing.process( docData, new Token( new Source(), TokenType.TEXT, "", "", cp ), 1 ));
    }

    @Test
    public void testSwitchToCodeMode()
    {
        final GDD gdd = setupGdd();

        MockPdfOutfile outfile = new MockPdfOutfile();

        PdfData pdd = setupPdd( gdd, outfile );
        pdd.setInCodeSection( false, new Source() );        

        CommandParameter cp = new CommandParameter();
        cp.setString( null );

        Token tok = new Token( new Source(), TokenType.TEXT, "", "", cp );

        pdfListing.switchToCodeMode( pdd, tok, 7 );
        assertTrue( pdd.inCodeSection() );
    }

    private GDD setupGdd()
    {
        final GDD gdd = new GDD();
        gdd.initialize();
        gdd.setLits( new MockLiterals() );
        gdd.setupLogger( ( "org.pz.platypus.Platypus" ));
        gdd.getLogger().setLevel( Level.OFF );
        return( gdd );
    }

    private PdfData setupPdd( GDD gdd, MockPdfOutfile outfile )
    {
        PdfData pdd = new PdfData( gdd );
        pdd.setOutfile( outfile );

        PdfFont font = new PdfFont( pdd );
        pdd.setFont( font );

        pdd.setFormatStack( new FormatStack( pdd ));
        return( pdd );
    }
}
