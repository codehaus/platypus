/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.rtf;

import org.pz.platypus.CommandParameter;
import org.pz.platypus.GDD;

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
     * @param rtd RTF Data, which is used in a few calculations
     * @return the value in points
     */
    public static float convertParameterToPoints( final CommandParameter cp, final RtfData rtd )
    {
        assert( cp != null );
        assert( rtd != null );

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
                points = value * rtd.getLeading();
                break;
            case PIXEL:
                points = ( value / rtd.getPixelsPerInch() ) * POINTS_PER_INCH;
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

    /**
     * Converts a parameter unit type to a descriptive string (for error messages)
     * @param cp the parameter containing the unit type
     * @param gdd the GDD, containing the table of literals
     * @return the name of the unit, or " " if it's not valid, or the empty string
     *              for all other errors
     */
    public static String convertParameterUnitToString( final CommandParameter cp, GDD gdd )
    {
        if( cp == null || gdd == null ) {
            return( "" );
        }

        switch( cp.getUnit() )
        {
            case CM:
                return( gdd.getLit( "CM" ));
            case INCH:
                return( gdd.getLit( "INCHES" ));
            case LINE:
                return( gdd.getLit( "LINES" ));
            case PIXEL:
                return( gdd.getLit( "PIXELS" ));
            case POINT:
                return( gdd.getLit( "POINTS" ));
            default:
                return( " " );
        }
    }
}