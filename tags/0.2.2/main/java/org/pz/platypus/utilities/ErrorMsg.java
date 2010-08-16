/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-10 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.utilities;

import org.pz.platypus.GDD;
import org.pz.platypus.Source;
import org.pz.platypus.Token;

/**
 * Various Error Message-Generating Routines
 *
 * @author alb
 */
public class ErrorMsg
{
    /**
     * Most Platypus error messages start with the file# and line# where an error occurred.
     * This method generates the literal of file and line numbers for the token, plus a ": ".
     *
     * @param gdd container that holds location of the literals file
     * @param tok the erroneous token
     * @return  the file and line #s, or an empty string in case of error
     */
    public static String location( final GDD gdd, final Token tok )
    {
        if( gdd == null || tok == null || tok.getSource() == null ) {
            return( "" );
        }

        StringBuffer loc = new StringBuffer( 30 );
        loc.append( gdd.getLit( "FILE#" ) + " " +
                    tok.getSource().getFileNumber() + " " +
                    gdd.getLit( "LINE#" ) + " " +
                    tok.getSource().getLineNumber() + ": " );
        return( loc.toString() );
    }

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
