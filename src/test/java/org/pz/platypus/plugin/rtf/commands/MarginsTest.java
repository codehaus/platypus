/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.rtf.commands;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.GDD;
import org.pz.platypus.plugin.rtf.RtfData;

/**
 * Test functioning of four margin commands in the RTF plugin
 */
public class MarginsTest
{
    private GDD gdd;
    private RtfData rtd;

    private RtfMarginBottom bM;
    private RtfMarginLeft   lM;
    private RtfMarginRight  rM;
    private RtfMarginTop    tM;

    @Before
    public void setUp()
    {
        gdd = new GDD();
        gdd.initialize();
        rtd = new RtfData( gdd );
    }

    @Test
    public void testConstructor()
    {
        bM = new RtfMarginBottom();
        assertEquals( "[bmargin:", bM.getRoot() );

        lM = new RtfMarginLeft();
        assertEquals( "[lmargin:", lM.getRoot() );

        rM = new RtfMarginRight();
        assertEquals( "[rmargin:", rM.getRoot() );

        tM = new RtfMarginTop();
        assertEquals( "[tmargin:", tM.getRoot() );
    }
}