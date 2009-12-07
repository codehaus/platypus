/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.GDD;
import org.pz.platypus.exceptions.InvalidConfigFileException;
import org.pz.platypus.test.mocks.MockLogger;
import org.pz.platypus.test.mocks.MockLiterals;

import java.util.logging.Level;

public class PdfDataTest
{
    private PdfData pd;
    private GDD gdd;

    @Before
    public void setUp()
    {
        gdd = new GDD();
        gdd.initialize();
        gdd.setLogger( new MockLogger() );
        gdd.setLits( new MockLiterals() );
        gdd.getLogger().setLevel( Level.OFF );
        pd = new PdfData( gdd );
    }

    // test some of the many settings that are initialized in the constructor
    @Test
    public void testConstructor()
    {
        assertEquals( DefaultValues.FONT_SIZE, pd.getFontSize(), 0.1f );
        assertEquals( DefaultValues.LEADING, pd.getLeading(), 0.1f );
        assertEquals( 0, pd.getLeadingLine().getLineNumber() );
        assertEquals( DefaultValues.MARGIN, pd.getMarginBottom(), 0.1f );
        assertEquals( 0, pd.getMarginBottomLine().getLineNumber() );
        assertEquals( 96f, pd.getPixelsPerInch(), 0.1f );
        assertEquals( 0, pd.getPixelsPerInchLine().getLineNumber());
    }

    /**
     * Throws exception because PdfData tries to initialize typefaceMap with the
     * font list file found in config/fontlist.txt under PLATYPUS_HOME. But because
     * PLATYPUS_HOME is set to an invalid value, the config file is not found, and
     * so an exception ensues.
     *
     * @throws InvalidConfigFileException
     */
    @Test (expected = InvalidConfigFileException.class )
    public void testInit() throws InvalidConfigFileException
    {
        pd.init();
    }


}