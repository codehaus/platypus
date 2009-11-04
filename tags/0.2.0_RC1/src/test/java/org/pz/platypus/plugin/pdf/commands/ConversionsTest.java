/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.GDD;
import org.pz.platypus.CommandParameter;
import org.pz.platypus.UnitType;
import org.pz.platypus.Source;
import org.pz.platypus.test.mocks.MockLiterals;
import org.pz.platypus.test.mocks.MockLogger;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.plugin.pdf.Conversions;


public class ConversionsTest
{
    CommandParameter cp;
    GDD gdd;
    PdfData pd;

    @Before
    public void setUp()
    {
        gdd = new GDD();
        gdd.initialize();
        gdd.setLits( new MockLiterals()  );
        gdd.setLogger( new MockLogger() );

        pd = new PdfData( gdd );
        cp = new CommandParameter();
    }

    @Test
    public void test10Inches()
    {
        cp.setUnit( UnitType.INCH );
        cp.setAmount( 10f );
        assertEquals( 720f,
                      Conversions.convertParameterToPoints( cp, pd ),
                      0.1f );
    }

    @Test
    public void test25cm()
    {
        cp.setUnit( UnitType.CM );
        cp.setAmount( 25.4f );
        assertEquals( 720f,
                      Conversions.convertParameterToPoints( cp, pd ),
                      0.1f );
    }

    @Test
    public void test12ptline()
    {
        pd.setLeading( 12f, new Source() );
        cp.setUnit( UnitType.LINE );
        cp.setAmount( 1 );
        assertEquals( 12f,
                      Conversions.convertParameterToPoints( cp, pd ),
                      0.1f );
    }

    @Test
    public void test25pts()
    {
        cp.setUnit( UnitType.POINT );
        cp.setAmount( 25f );
        assertEquals( 25f,
                      Conversions.convertParameterToPoints( cp, pd ),
                      0.1f );
    }

    @Test
    public void test192pixels()
    {
        cp.setUnit( UnitType.PIXEL );
        cp.setAmount( 192f );  // @96 pixels/in, 192 pix = 2"
        assertEquals( 144f,    // 2" = 144 pts
                      Conversions.convertParameterToPoints( cp, pd ),
                      0.1f );
    }

    @Test
    public void test25unknownUnits()
    {
        cp.setUnit( UnitType.UNKNOWN );
        cp.setAmount( 25f );
        assertEquals( 25f,
                      Conversions.convertParameterToPoints( cp, pd ),
                      0.1f );

    }
}