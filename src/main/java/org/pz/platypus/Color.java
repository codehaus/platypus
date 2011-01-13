/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2011 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import org.pz.platypus.interfaces.IColor;

/**
 * Abstract class from which RgbColor inherits and from which CMYK will at a later point.
 *
 * @author alb
 */

abstract class Color implements IColor
{

    // for RGB colors
    int R;   // red,   0-255
    int G;   // green, 0-255
    int B;   // blue,  0-255

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return( true );
        }

        if (o == null || getClass() != o.getClass() ) {
            return( false );
        }

        Color color = (Color) o;

        if (B != color.B) return( false );
        if (G != color.G) return( false );
        if (R != color.R) return( false );

        return( true );
    }

    @Override
    public int hashCode()
    {
        int result = R;
        result = 31 * result + G;
        result = 31 * result + B;
        return( result );
    }

    public int getR()
    {
        return( R );
    }

    public void setR(int r)
    {
        R = r ;
    }

    public int getG()
    {
        return( G );
    }

    public void setG(int g)
    {
        G = g;
    }

    public int getB()
    {
        return( B );
    }

    public void setB(int b)
    {
        B = b;
    }
}
