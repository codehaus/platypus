/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.rtf;

/**
 * Static class that contains maxima, minima, and other RTF-related limits
 * These are the same as the PDF limits, for the nonce, but might change as
 * we implement more aspects of the RTF standard.
 *
 * @author alb
 */
public class Limits
{
    public final static float PAGE_HEIGHT_MAX =   14400f;           // points
    public final static float PAGE_HEIGHT_MIN =       3f;           // points
    public final static float PAGE_WIDTH_MAX  =   14400f;           // points
    public final static float PAGE_WIDTH_MIN  =       3f;           // points

    /** a value so high that setting the current column to it, forces a new page */
    public final static int   COLUMN_COUNT_MAX =  999999999;
}