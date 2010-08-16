/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.commandTypes;

import org.pz.platypus.exceptions.EolTreatmentException;

/**
 * The possible end-of-line treatments. Cannot use enums because of the way the Value class
 * is set up.
 *
 * soft = [cr] is treated as a space, two [cr]'s are treated as a []
 * hard = [cr] is treated as []
 *
 * @author alb
 */
public class EolTreatment
{
    public static final int SOFT = 's';
    public static final int HARD = 'h';

    private static final String SOFT_STR = "soft";
    private static final String HARD_STR = "hard";

    /**
     * Determines whether a treatment specifider is valid or not
     * @param treatment the specifier, which can only be soft/hard currently
     * @return true if valid, false if not.
     */
    public boolean isValid( final String treatment )
    {
        if( treatment == null ) {
            return( false );
        }
        if( treatment.equals( SOFT_STR ) || treatment.equals( HARD_STR )) {
            return( true );
        }

        return( false );
    }

    /**
     * Converts the treatment string specified in the command to a valid int representation
     * @param treatment the string as specified
     * @return  the integer value of the treatment type
     * @throws EolTreatmentException if the input string is invalid
     */
    public int toInteger( final String treatment ) throws EolTreatmentException
    {
        if( treatment != null && treatment.equals( SOFT_STR )) {
            return( SOFT );
        }
        else
        if( treatment != null && treatment.equals( HARD_STR )) {
            return( HARD );
        }
        else {
            throw new EolTreatmentException( treatment );
        }
    }

    public boolean isSoft( final int treatment )
    {
        return( treatment == SOFT );
    }
}
