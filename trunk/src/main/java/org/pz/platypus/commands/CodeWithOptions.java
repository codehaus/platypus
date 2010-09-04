/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.commands;

import org.pz.platypus.GDD;
import org.pz.platypus.Source;
import org.pz.platypus.Token;
import org.pz.platypus.interfaces.IOutputContext;

/**
 * Abstract class for handling code listing.
 *
 * @author alb
 */
public abstract class CodeWithOptions 
{
    protected final String root = "[code|";

    /** the parameter data for the token */
    protected String tokenParam = null;

    /** command parameters: starting line#, which line #s to print */
    protected String[] params;

    /**
     * Basic command housekeeing.
     * @param context the document data container
     * @param tok the token and parameters
     * @param tokNum the token number
     *
     * @return Returns 0 if we're done, 1 if we keep going.
     */
    protected int preProcess( final IOutputContext context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        tokenParam = tok.getParameter().getString();
        if( tokenParam == null || ! tokenParam.startsWith( "lines:" )) {
            invalidParameterErrMessage( context.getGdd(), tok.getSource() );
            return( 0 );
        }

        if( context.inCodeSection() ) {
            return( 0 ); //already in a code section
        }

        String counts = org.pz.platypus.utilities.TextTransforms.lop( tokenParam, "lines:".length() );
        params = counts.split( "," );
        if( params.length != 2 ) {
            invalidParameterErrMessage( context.getGdd(), tok.getSource() );
            return( 0 ) ;
        }

        // process the parameters to 'lines:', such as "lines:1,5"
        // 1st param = starting line number (0 = continue from previous),
        // 2nd param: which line numbers to print, where 1 = every line, 2 = every even line, and
        //                  x = every line mod x, etc.

        int startLine = parseStartingLineNumber( params[0], context.getGdd(), tok.getSource() );
        if( startLine != 0 ) {  // if == 0, then continue from previous line number.
            context.setLineNumberLast( startLine - 1, tok.getSource() );
        }

        int lineNumberSkip = parseSkipLineNumber( params[1], context.getGdd(), tok.getSource() );
        context.setLineNumberSkip( lineNumberSkip, tok.getSource() );

        return( 1 );
    }

    /**
     * Extracts the line numbers on which to print line numbers in the listings.
     *
     * @param num the passed parameter, as a string
     * @param gdd Global document data (used primarily for accessing literals engine)
     * @param source where the parameter was specified
     *
     * @return the starting line number. In case of error, set to 1 (line numbers on every line).
     */    
    protected int parseSkipLineNumber( final String num, final GDD gdd, final Source source )
    {
        int skipLineNumber;
        try {
            skipLineNumber = Integer.parseInt( num );
        }
        catch( NumberFormatException nfe ) {
            invalidSkipLineNumberErrMessage( gdd, source);
            return( 1 );
        }

        if( skipLineNumber < 1 || skipLineNumber > 100 ) {
            invalidSkipLineNumberErrMessage( gdd, source);
            return( 1 );
        }
        else {
            return( skipLineNumber );
        }
    }

    /**
     * Extracts the starting line number from the passed parameters.
     *
     * @param num the passed parameter, as a string
     * @param gdd Global document data (used primarily for accessing literals engine)
     * @param source where the parameter was specified
     *
     * @return the starting line number. If == 0, means: continue from previous listing line numbers.
     */
    protected int parseStartingLineNumber( final String num, final GDD gdd, final Source source )
    {
        int startingLineNumber;

        try {
            startingLineNumber = Integer.parseInt( num );
        }
        catch( NumberFormatException nfe ) {
            invalidStartingLineNumberErrMessage( gdd, source);
            return( 1 );
        }

        if( startingLineNumber < 0 || startingLineNumber > 999999 ) {
            invalidStartingLineNumberErrMessage( gdd, source);
            return( 1 );
        }
        else {
            return( startingLineNumber );
        }
    }

    /*
     * Output error messages
     *
     * @param gdd Global document data, used here for access to the literals
     * @param source where the error occurred.
     */

    private void invalidSkipLineNumberErrMessage( final GDD gdd, final Source source)
    {
        gdd.logWarning( gdd.getLit( "FILE#" ) + source.getFileNumber() + " " +
            gdd.getLit( "LINE#" ) + source.getLineNumber() + " " +
            gdd.getLit( "ERROR.INVALID_SKIP_LINE_NUMBER_IN_CODE" ) +  ". "  +
            gdd.getLit( "SET_TO_1" ));
    }

    private void invalidStartingLineNumberErrMessage( final GDD gdd, final Source source)
    {
        gdd.logWarning( gdd.getLit( "FILE#" ) + source.getFileNumber() + " " +
            gdd.getLit( "LINE#" ) + source.getLineNumber() + " " +
            gdd.getLit( "ERROR.INVALID_STARTING_LINE_NUMBER_IN_CODE" ) +  ". "  +
            gdd.getLit( "SET_TO_1" ));
    }


    private void invalidParameterErrMessage( GDD gdd, final Source source )
    {
        gdd.logWarning( gdd.getLit( "FILE#" ) + source.getFileNumber() + " " +
                gdd.getLit( "LINE#" ) + source.getLineNumber() + " " +
                gdd.getLit( "ERROR.INVALID_PARAMETER_FOR_CODE_COMMAND" ) +  ". "  +
                gdd.getLit( "SWITCHING_TO_PLAIN_CODE_FORMAT" ));
    }

    
    //=== getters and setters ===//

    public String getRoot()
    {
        return( root );
    }
}
