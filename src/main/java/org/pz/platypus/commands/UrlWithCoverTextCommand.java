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
 * Abstract portion of command for URL with cover text
 *
 * @author alb
 */
public abstract class UrlWithCoverTextCommand implements OutputCommandable
{
    private final String root = "[+url:";

    protected abstract void outputUrl(final OutputContextable context, String url, String coverText);

    public int process(OutputContextable context, Token tok, int tokNum)
    {
        if( context == null || tok == null || tok.getParameter().getString() == null ) {
            throw new IllegalArgumentException();
        }

        String urlParameter = tok.getParameter().getString();
        String url;
        String coverText = null;

        // test for "|text: after URL, which would signal presence of cover text. If found,
        // set url and coverText to the respective strings in urlParameter; else, it's all
        // URL, so set url and leave coverText = null
        int textFlag = urlParameter.indexOf( "|text:" );
        if( textFlag > 0 ) {
            coverText = urlParameter.substring( textFlag + "|text:".length() );
            url = urlParameter.substring( 0, textFlag - 1);
        }
        else {
            url = urlParameter;
        }

        if( url == null ) {
            showErrorMsg( tok, context );
            return 0;
        }

        outputUrl(context, url, coverText);

        return 0;
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
                        gdd.getLit( "ERROR.URL_IS_NULL" ) + " " +
                        gdd.getLit( "IGNORED" ));

    }

}