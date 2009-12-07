/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf;

/**
 * Color, as specified by three integers, 0-255, that correspond to RGB
 *
 * @author alb
 */
public class RgbColor
{
    private int R;
    private int G;
    private int B;

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

    public int getR() {
        return R;
    }

    public void setR( final int r ) {
        R = r;
    }

    public int getG() {
        return G;
    }

    public void setG( final int g ) {
        G = g;
    }

    public int getB() {
        return B;
    }

    public void setB( final int b ) {
        B = b;
    }

}
