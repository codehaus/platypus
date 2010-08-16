/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.*;
import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;

/**
 * Outputs the user string or system variable that appears after the root.
 *
 * @author alb
 */
public class PdfPrintVariable implements OutputCommandable
{
    private String root = "[*";

    public int process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        GDD gdd = context.getGdd();

        // get the parameter(s) to the [* command
        String varName = tok.getParameter().getString();

        if( varName != null ) {
            String userString = gdd.getUserStrings().getString( varName );

            // if variable name not found in table of user strings, check system variables
            if( userString == null ) {
                userString = gdd.getSysStrings().getString( varName );
            }

            if( userString != null ) {
           //   we add the text as a new token into the token stream at the next spot
           //   We cannot call pdf.getOutfile().emitText() to emit the text because if
           //   this is the first text output, emitText() will fail since the file has
           //   not been opened. Hence, we inject a new TEXT token into the token list
           //   immediately after the present token.
                injectTextToken( gdd, tokNum, userString, tok.getSource() );
            }
            else {
                issueErrorMessage( gdd, tok.getSource(), varName );
                injectTextToken( gdd, tokNum,  root + varName + "]" , tok.getSource() );
            }
            return 0;
        }

        // in theory cannot happen, as the token would be malformed and the parser would know this.
        issueErrorMessage( gdd, tok.getSource(), "[null]" );
        return 0;
    }

    /**
     * Injects a new text token into the TokenList, immediately after the present token.
     *
     * @param gdd the GDD
     * @param tokNum token number of the current token
     * @param text the text to insert
     * @param source the file and line # of the current token
     */
    private void injectTextToken( final GDD gdd, final int tokNum, final String text,
                                  final Source source )
    {
        TokenList tokList = gdd.getInputTokens();
        Token newTextToken = new Token( source, TokenType.MACRO_TEXT, text );
        tokList.add( tokNum+1, newTextToken );
    }

    public void issueErrorMessage( final GDD gdd, final Source source, final String macro )
    {
        gdd.logWarning(
           gdd.getLit( "FILE#" ) + " " + source.getFileNumber() + " " +
           gdd.getLit( "LINE#" ) + " " + source.getLineNumber() + " " +
           gdd.getLit( "ERROR.INVALID_USER_STRING" ) + " " + macro + " " +
           gdd.getLit( "SKIPPED" ));
    }

    //=== getters and setters ===/

    public String getRoot()
    {
        return( root );
    }
}