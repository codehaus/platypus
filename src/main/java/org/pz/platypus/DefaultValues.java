/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import org.pz.platypus.command.Alignment;
import org.pz.platypus.command.EolTreatment;

/**
 * Default values for PDF plugin
 *
 * @author alb
 */
public class DefaultValues
{
    public static final float   POINTS_PER_INCH = 72f;
    public final static float   PIXELS_PER_INCH = 96f;
    public final static float   TWIPS_PER_POINT = 20f;

    public static final int     ALIGNMENT = Alignment.LEFT;
    public final static float   BASELINE_LOCATION = 20f;                 // used in footer
    public static final int     COLUMN_COUNT = 1;
    public final static float   COLUMN_WIDTH = 0f;          // when 0, use computed column width
    public static final int     EOL_TREATMENT = EolTreatment.SOFT;
    public static final float   FIRST_LINE_INDENT = 0f;
    public static final boolean FONT_BOLD = false;
    public static final boolean FONT_ITALIC = false;
    public static final float   FONT_SIZE = 12.0f;
    // default is: strikethrough location is 1/4 of font size above the baseline
    public static final float   FONT_SIZE_TO_STRIKETHRU_RATIO = 4.0f;
    public static final String  FONT_TYPEFACE = "TIMES_ROMAN";
    public final static float   FOOTER_FONT_SIZE = 10.0f;
    public static final int     FOOTER_PAGES_TO_SKIP = 1;
    public static final String  FOOTER_TYPEFACE = "TIMES_ROMAN";
    public static final float   LEADING_TO_FONT_SIZE_RATIO = 1.2f;
    public static final float   LEADING = FONT_SIZE * LEADING_TO_FONT_SIZE_RATIO;
    public static final float   MARGIN = 1.0f * POINTS_PER_INCH;
    public static final boolean MARGINS_MIRRORED = false;
    public static final boolean NO_INDENT = false;
    public static final float   PAGE_HEIGHT = 11f  * POINTS_PER_INCH;
    public static final float   PAGE_WIDTH  = 8.5f * POINTS_PER_INCH;
    public static final float   PARA_INDENT = 0f;
    public static final float   PARA_INDENT_RIGHT = 0f;
    public static final float   PARA_SKIP_LINES = 1f;
    public static final boolean STRIKETHRU = false;
    public static final float   UNDERLINE_THICKNESS = 0.6f;
    public static final float   UNDERLINE_POSITION = -2.0f;
}
