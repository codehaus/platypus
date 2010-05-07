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
import org.pz.platypus.plugin.pdf.commands.PdfUrlWithCoverTextEnd;
import org.pz.platypus.plugin.pdf.commands.PdfPrintVariable;
import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;

/**
 * Abstract portion of command for URL with cover text
 *
 * @author alb
 */
public abstract class UrlWithCoverText implements OutputCommandable
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
        String coverText = getCoverText( context, tokNum );
        if( coverText == null || coverText.isEmpty() ) {
            showNoCoverTextlErrorMsg( tok, context);
            outputUrl( context, url, null );
        }
        else {
            outputUrl(context, url, coverText);
        }

        return( tokensToSkip );
    }

    /**
     * Go down the list of tokens looking for text and a [-url] token pair. These
     * are the cover text. If you find anything else, issue a warning and handle the error.
     *
     * @param context the PDF document data
     * @param startingNum the number of the current token in the list
     *
     * @return a string containing the cover text
     */
    String getCoverText( final OutputContextable context, final int startingNum )
    {
        GDD gdd = context.getGdd();
        TokenList tokens = gdd.getInputTokens();
        int currNum;
        Token tok = null;
        StringBuilder coverText = new StringBuilder();

        for( currNum = startingNum+1; currNum < tokens.size() ;currNum++ ) {
            tok = tokens.get( currNum );
            if( tok.getType() == TokenType.TEXT ) {
                coverText.append( tok.getContent() );
                continue;
            }

            // if it's a macro, process the macro and get the injected text token
            if( tok.getType() == TokenType.COMMAND &&
                tok.getRoot().startsWith( new PdfPrintVariable().getRoot() )) {
                    new PdfPrintVariable().process( context, tok, currNum );
                    continue;
            }

            // if it's the [-url] (end of cover text) command, we're done.
            if( tok.getType() == TokenType.COMMAND &&
                tok.getRoot().equals( new PdfUrlWithCoverTextEnd().getRoot() )) {
                    break;
            }

            // anything but the valid options above is an error.
            showUnclosedUrlCoverTextErrorMsg( tokens.get( startingNum ), gdd );
        }

        tokensToSkip = currNum - startingNum;
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
    private void showNullUrlErrorMsg( Token tok, OutputContextable context )
    {
        GDD gdd = context.getGdd();
        gdd.logWarning( gdd.getLit( "FILE#" ) + ": " + tok.getSource().getFileNumber() + " " +
                        gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                        gdd.getLit( "ERROR.URL_IS_NULL" ) + " " +
                        gdd.getLit( "IGNORED" ));
    }

    private void showNoCoverTextlErrorMsg( Token tok, OutputContextable context )
    {
        GDD gdd = context.getGdd();
        gdd.logWarning( gdd.getLit( "FILE#" ) + ": " + tok.getSource().getFileNumber() + " " +
                        gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                        gdd.getLit( "ERROR.MISSING_COVER_TEXT_FOR_URL" ));
    }

    private void showUnclosedUrlCoverTextErrorMsg( final Token tok, final GDD gdd )
    {
        gdd.logWarning( gdd.getLit( "FILE#" ) + ": " + tok.getSource().getFileNumber() + " " +
                        gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                        gdd.getLit( "ERROR.URL_COVER_TEXT_NOT_PROPERLY_ENDED" ));
    }

}