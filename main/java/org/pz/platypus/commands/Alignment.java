/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.commands;

import org.pz.platypus.GDD;

/**
 * The possible paragraph alignments. Cannot use enums because of the way the Value class
 * is set up.
 *
 * @author alb
 */
public class Alignment
{
    public static final int CENTER = 'c';
    public static final int JUST   = 'j';
    public static final int LEFT   = 'l';
    public static final int RIGHT  = 'r';

    /**
     * Convert an alignment to string
     *
     * @param alignment the alignment to convert (see static values at start of class)
     * @param gdd contains the literals lookup table
     * @return  a string version of the aligment, or "INVALID" if not a valid value
     */
    public String toString( int alignment, GDD gdd )
    {
        if( gdd == null ) {
            return( "Error: GDD not specified in class Alignment" );
        }

        switch( alignment )
        {
            case 'c': return gdd.getLit( "CENTER" );
            case 'j': return gdd.getLit( "JUSTIFIED" );
            case 'l': return gdd.getLit( "LEFT" );
            case 'r': return gdd.getLit( "RIGHT" );
        }
        return( gdd.getLit( "INVALID" ));
    }
}