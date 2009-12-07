/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf;

import org.pz.platypus.Source;

/**
 * Contains the values that define the underline characteristics
 *
 * @author alb
 */
public class Underline
{
    private boolean inEffect = false;

    private float thickness;

    private float position;

    private Source source;

    public Underline()
    {
        inEffect = false;
        thickness = DefaultValues.UNDERLINE_THICKNESS;
        position = DefaultValues.UNDERLINE_POSITION;
        source = new Source();
    }

    public boolean isInEffect()
    {
        return inEffect;
    }

    public void setInEffect( final boolean inEffect, final Source newSource )
    {
        this.inEffect = inEffect;
        this.source = newSource;
    }

    public float geTthickness()
    {
        return thickness;
    }

    public void setThickness( final float thickness )
    {
        this.thickness = thickness;
    }

    public float getPosition()
    {
        return position;
    }

    public void setPosition( final float position )
    {
        this.position = position;
    }

}
