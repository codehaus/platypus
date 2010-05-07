/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.commands;

import org.pz.platypus.GDD;
import org.pz.platypus.Token;
import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;

/**
 * Abstract portion of command that marks the end of cover text for a URL
 *
 * @author ask
 */
public abstract class UrlWithCoverTextEnd implements OutputCommandable
{
    private final String root = "[-url]";

    public int process(OutputContextable context, Token tok, int tokNum)
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        // UrlWithCoverText.java swallows up all the cover text up to and including this command.
        // Consequently, if this command is encountered, it means that a previous error occurred.
        // So, show a warning and move on.

        showErrorMsg( tok, context );

        return( 0 );
    }

    public String getRoot()
    {
        return( root );
    }

    /**
     * Show error message, giving location in Platypus input file
     * @param tok contains the location data
     * @param context contains the location of the logger and literals file
     */
    private void showErrorMsg(Token tok, OutputContextable context) {
        GDD gdd = context.getGdd();
        gdd.logWarning( gdd.getLit( "FILE#" ) + ": " + tok.getSource().getFileNumber() + " " +
                        gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                        gdd.getLit( "ERROR.INVALID_END_OF_URL_COVER_TEXT" ) + " " +
                        gdd.getLit( "IGNORED" ));
    }

}