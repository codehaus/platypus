/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2011 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Test of RgbColor, which is a simple JavaBean that holds the font color as a triple 0-255 integer.
 *
 * @author alb
 */
public class RgbColorTest
{
    private RgbColor rgb;

    @Before
    public void setUp()
    {
        rgb = new RgbColor();
    }

    @Test
    public void validSetToDefault()
    {
        assertEquals( 0, rgb.getR() );
        assertEquals( 0, rgb.getG() );
        assertEquals( 0, rgb.getB() );
    }

    // if RGB values are out of 0-255 range, the color is not reset. Hence, here, they should all remain at 0.
    @Test
    public void invalidRgbValues()
    {
        rgb.setB( -26 );
        rgb.setG( 300 );
        rgb.setR( 12000 );

        assertEquals( 0, rgb.getR() );
        assertEquals( 0, rgb.getG() );
        assertEquals( 0, rgb.getB() );
    }

    @Test
    public void validRgbValues()
    {
        rgb.setR( 100 );
        rgb.setG( 200 );
        rgb.setB( 250 );

        assertEquals( 100, rgb.getR() );
        assertEquals( 200, rgb.getG() );
        assertEquals( 250, rgb.getB() );
    }

    @Test
    public void testSettingAllValuesAtOnce()
    {
        rgb = new RgbColor( 100, 200, 250 );

        assertEquals( 100, rgb.getR() );
        assertEquals( 200, rgb.getG() );
        assertEquals( 250, rgb.getB() );
    }
}
