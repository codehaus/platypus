/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf;

import org.pz.platypus.CommandParameter;

/**
 * Performs various conversions needed in the PDF plugin
 *
 * @author alb
 */
public class Conversions
{
    /**
     * Utility function to convert units in command parameters to points
     * @param cp the command parameter
     * @param pdfData pdfData, which is used in a few calculations
     * @return the value in points
     */
    public static float convertParameterToPoints( final CommandParameter cp, final PdfData pdfData )
    {
        final float POINTS_PER_INCH = 72f;
        final float CMS_PER_INCH = 2.54f;

        final float points;
        final float value = cp.getAmount();

        switch( cp.getUnit() )
        {
            case CM:
                points = ( value / CMS_PER_INCH  ) * POINTS_PER_INCH;   // CM->IN->PT
                break;
            case INCH:
                points = value * POINTS_PER_INCH;                       // IN->PT
                break;
            case LINE:
                points = value * pdfData.getLeading();
                break;
            case PIXEL:
                points = ( value / pdfData.getPixelsPerInch() ) * POINTS_PER_INCH;
                break;
            case POINT:
                points =  value;
                break;
            default:    // if we don't know the units (should not occur)
                        // then return the value as it is
                points =  value;
                break;
        }
        return( points );
    }
}

