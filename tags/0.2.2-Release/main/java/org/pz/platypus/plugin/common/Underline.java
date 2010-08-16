/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.common;

import org.pz.platypus.Source;
import org.pz.platypus.DefaultValues;

/**
 * Contains the values that define the underline characteristics
 *
 * @author alb
 */
public class Underline implements Cloneable
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

    /**
     * Clone used in saving format
     *
     * @return a cloned Underline object
     */
    @Override
    public Underline clone()
    {
        Object clonedUnderline = null;

        try {
            clonedUnderline = super.clone();
        }
        catch (CloneNotSupportedException e) {
            //This should not happen, since this class is Cloneable.
        }

        return( (Underline) clonedUnderline );
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
