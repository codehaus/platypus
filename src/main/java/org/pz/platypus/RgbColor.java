/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2011 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

/**
 * Derived from Color; specifies color via three integers, 0-255, that correspond to RGB
 *
 * @author alb
 */
public class RgbColor extends Color
{
    /**
     * Default color is black: ( 0, 0, 0 )
     */
    public RgbColor()
    {
        R = 0;
        G = 0;
        B = 0;
    }

    public RgbColor( final int r, final int g, final int b )
    {
        R = r;
        G = g;
        B = b;
    }

    public RgbColor( final String colorMacro )
    {
        //TODO: allow a macro to specify the color.
    }

    public int[] parse()  //TODO: fill in. Extract from PdfFcolor.java
    {
        return( null );
    }

    public int getR()
    {
        return R;
    }

    public void setR( final int r )
    {
        if( r >=0 && r <= 255 ) {
            R = r;
        }
    }

    public int getG()
    {
        return G;
    }

    public void setG( final int g )
    {
        if( g >=0 && g <= 255 ) {
            G = g;
        }
    }

    public int getB() {
        return B;
    }

    public void setB( final int b )
    {
        if( b >=0 && b <= 255 ) {
            B = b;
        }
    }
}
