/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.utilities;

import org.pz.platypus.GDD;
import org.pz.platypus.Source;

/**
 * Various Error Message-Generating Routines
 *
 * @author alb
 */
public class ErrorMsg
{
    /**
     * Logs warning when a command that is not supported in a code section is encountered.
     *
     * @param gdd Global document data
     * @param command  the command root or, in some cases, the command string
     * @param source the file# and line# where the command came from
     * @return the string that's written to the logger (used primarily for testing), or "" if
     *         an error occurred
     */
    public static String notAllowedInCode( final GDD gdd, final String command,final Source source )
    {
        if( gdd == null || command == null || source == null ) {
            return( "" );
        }

        String msg = new StringBuilder( 50 ).append( gdd.getLit( "LINE#" ))
                                        .append( " " )
                                        .append( source.getLineNumber() )
                                        .append( ": " )
                                        .append( command )
                                        .append( " " )
                                        .append( gdd.getLit( "COMMAND_NOT_ALLOWED_IN_CODE" ))
                                        .append( " " )
                                        .append( gdd.getLit( "IGNORED" ))
                                        .toString();

        gdd.logInfo( msg );
        return( msg );
    }
}
