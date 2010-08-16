/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf;

import org.pz.platypus.plugin.common.Underline;

/**
 * Container for the format data at a given point in the document. Used in FormatStack.
 *
 * @author alb
 */
public class Format
{
    private PdfFont font;
    private boolean strikethru;
    private Underline underline;
    private int eolHandling;
    private float leading;

    public Format( final PdfFont newFont, final boolean newStrikethru, final Underline newUnderline,
                   final int newEolHandling, final float newLeading )
    {
        font = newFont;
        strikethru = newStrikethru;
        underline = newUnderline;
        eolHandling = newEolHandling;
        leading = newLeading;
    }

    //=== getters and setters ===//
    
    public PdfFont getFont()
    {
        return font;
    }

    public boolean isStrikethru()
    {
        return strikethru;
    }

    public Underline getUnderline()
    {
        return underline;
    }

    public int getEolHandling()
    {
        return eolHandling;
    }

    public float getLeading()
    {
        return leading;
    }
}
