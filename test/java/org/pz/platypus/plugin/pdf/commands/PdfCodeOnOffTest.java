/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import org.pz.platypus.*;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.plugin.pdf.FormatStack;
import org.pz.platypus.test.mocks.MockLogger;
import org.pz.platypus.test.mocks.MockLiterals;
import org.junit.Test;
import org.junit.Before;

import java.util.logging.Level;

/**
 * Test the start and end of a code section/listing.
 *
 * @author alb
 */

public class PdfCodeOnOffTest
{
    PdfData pdd;
    GDD gdd;
    PdfCodeOn codeOn;
    PdfCodeOff codeOff;

    String codeOnRoot = "[code]";
    String codeOffRoot = "[-code]";

    @Before
    public void setUp()
    {
        gdd = new GDD();
        gdd.initialize();
        gdd.setLogger( new MockLogger() );
        gdd.setLits( new MockLiterals() );
        gdd.getLogger().setLevel( Level.OFF );
        pdd = new PdfData( gdd );

        codeOn = new PdfCodeOn();
        codeOff = new PdfCodeOff();
    }

    // ==== actual tests start here ===== //

    @Test
    public void testConstructors()
    {
        assertEquals( codeOnRoot, codeOn.getRoot() );
        assertEquals( codeOffRoot, codeOff.getRoot() );
    }

    @Test
    public void testEffectsOfCodeSwitchedOn()
    {
        assertFalse( pdd.inCodeSection() );
        pdd.setFormatStack( new FormatStack( pdd ));
        FormatStack formats = pdd.getFormatStack();
        assertEquals( 1, formats.getSize() );

        Token tok = new Token( new Source(), TokenType.COMMAND, codeOnRoot, "", null);
        codeOn.process( pdd, tok, 6);

        assertEquals( 2, formats.getSize() );
        assertTrue( pdd.inCodeSection() );
    }


    @Test
    public void testEffectsOfCodeSwitchedOff()
    {
        // first, turn code on
        assertFalse( pdd.inCodeSection() );
        pdd.setFormatStack( new FormatStack( pdd ));
        FormatStack formats = pdd.getFormatStack();
        Token tok = new Token( new Source(), TokenType.COMMAND, codeOnRoot, "", null);
        codeOn.process( pdd, tok, 6);
        assertTrue( pdd.inCodeSection() );

        // now, turn it off
        Token tok2 = new Token( new Source(), TokenType.COMMAND, codeOffRoot, "", null);
        codeOff.process( pdd, tok2, 7);

        assertEquals( 1, formats.getSize() );
        assertFalse( pdd.inCodeSection() );
    }
}