/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.commands;

import org.pz.platypus.GDD;
import org.pz.platypus.Token;
import org.pz.platypus.TokenList;
import org.pz.platypus.TokenType;
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

    /** how many tokens do we consume reading the cover text? Should generally be 2. */
    private int tokensToSkip = 0;

    protected abstract void outputUrl( final OutputContextable context, String url, String coverText );

    public int process(OutputContextable context, Token tok, int tokNum)
    {
        if( context == null || tok == null || tok.getParameter().getString() == null ) {
            throw new IllegalArgumentException();
        }

        // get the URL
        String url = tok.getParameter().getString();
        if( url == null ) {
            showNullUrlErrorMsg( tok, context );
            return 0;
        }

        // get the cover text
        String coverText = getCoverText( context.getGdd(), tokNum );

        outputUrl(context, url, coverText);

        return( tokensToSkip );
    }

    /**
     * Go down the list of tokens looking for text and an [-url] token pair. These
     * are the cover text. If you find anything else, raise a fuss.
     *
     * @param gdd containing the list of input tokens
     * @param startingNum the number of the current token in the list
     *
     * @return a string containing the cover text
     */
    String getCoverText( final GDD gdd, final int startingNum )
    {
        TokenList tokens = gdd.getInputTokens();
        int currNum;
        Token tok = null;
        StringBuilder coverText = new StringBuilder();

        for( currNum = startingNum+1; currNum < tokens.size() ;currNum++ ) {
            tok = tokens.get( currNum );
            if( tok.getType() == TokenType.TEXT ) {
                coverText.append( tok.getContent() );
            }
            else {
                break;
            }
        }

        // this can only happen if a [+url: command is the last token in the input token list
        if( tok == null ) {
            showUnclosedUrlCoverText( tokens.get( startingNum ), gdd );
            return( "" );
        }

//        if( tok.getRoot() != new UrlWithCoverTextEndCommand().getRoot() ) {
//
//        }
        return( coverText.toString() );
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
    private void showNullUrlErrorMsg(Token tok, OutputContextable context)
    {
        GDD gdd = context.getGdd();
        gdd.logWarning( gdd.getLit( "FILE#" ) + ": " + tok.getSource().getFileNumber() + " " +
                        gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                        gdd.getLit( "ERROR.URL_IS_NULL" ) + " " +
                        gdd.getLit( "IGNORED" ));
    }

    /**
     * Show error message, giving location in Platypus input file
     * @param tok contains the location data
     * @param gdd contains the location of the logger and literals file
     */
    private void showUnclosedUrlCoverText(final Token tok, final GDD gdd )
    {
        gdd.logWarning( gdd.getLit( "FILE#" ) + ": " + tok.getSource().getFileNumber() + " " +
                        gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                        gdd.getLit( "ERROR.URL_COVER_TEXT_NOT_PROPERLY_ENDED" ));
    }

}