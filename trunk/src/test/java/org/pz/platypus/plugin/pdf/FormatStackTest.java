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
import org.pz.platypus.*;

/**
 * Test the format stack operations
 *
 * @author alb
 */
public class FormatStackTest
{
    private GDD gdd;
    private PdfData pdd;
    private FormatStack fs;

    @Before
    public void setUp()
    {
        gdd = new GDD();
        gdd.initialize();
        pdd = new PdfData( gdd );
        fs = new FormatStack( pdd );
    }

    /**
     * Make sure the constructor creates an instance of the default format
     */
    @Test
    public void testConstructor()
    {
        assertEquals( 1, fs.getSize() );
        Format f = fs.restoreLastFormat();
        assertEquals( DefaultValues.STRIKETHRU, f.isStrikethru() );
        assertEquals( DefaultValues.FONT_SIZE, f.getFont().getSize(), 0.05f );
        assertEquals( DefaultValues.LEADING,  f.getLeading(), 0.05f );
        assertEquals( DefaultValues.EOL_TREATMENT, f.getEolHandling() );
        assertEquals( DefaultValues.UNDERLINE_POSITION, f.getUnderline().getPosition(), 0.05f );
    }

    /**
     * Even if the stack is down to one format, you should be able to pop it multiple times
     * without getting any error.
     */
    @Test
    public void testMultiplePopsAreOk()
    {
        fs.restoreLastFormat();
        fs.restoreLastFormat();
        fs.restoreLastFormat();
        assertEquals( 1, fs.getSize() );
    }

    @Test
    public void testPushingSixFormats()
    {
        for( int i = 0; i < 6; i++ ) {
            fs.saveCurrentFormat( pdd );
        }

        assertEquals( 6 + 1, fs.getSize() );
    }

    @Test
    public void testPushesAndPops()
    {
        fs.saveCurrentFormat( pdd );
        fs.saveCurrentFormat( pdd );
        fs.restoreLastFormat();
        assertEquals( 2, fs.getSize() );
    }
}
