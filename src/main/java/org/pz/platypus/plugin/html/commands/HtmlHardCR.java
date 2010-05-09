/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.html.commands;

import org.pz.platypus.Token;
import org.pz.platypus.TokenList;
import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.plugin.html.HtmlData;

/**
 * Force a hard CR/LF (note: this command does *not* force an end of paragraph)
 *
 * Note that the main token processor in Pdf.Start() will check the token after []. If it's a [cr],
 * that [cr] is ignored (so that the next line does not begin with a blank).
 *
 * @author ask
 */
public class HtmlHardCR implements OutputCommandable
{
    private String root = "[]";

    public int process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        HtmlData htmlData = (HtmlData) context;

        // if it's the first token in line, it = a blank line, so an additional
        // NEWLINE needs to be emitted.

        if( isFirstTokenInLine( context, tok, tokNum ) ) {
            htmlData.getOutfile().emitText("<br>");
        }
        return 0;
    }

    /**
     * Determines whether the current [] is the first token in the line (which means either
     * the absolute first token, or the first token other than a command).
     *
     * @param context PdfData containing the Source fields for this token
     * @param tok present token
     * @param tokNum present token number
     * @return true if first token as specified above, or false if not.
     */
    private boolean isFirstTokenInLine( final OutputContextable context,
                                        final Token tok, final int tokNum )
    {
        TokenList tl = context.getGdd().getInputTokens();

        Token prevTok = tl.getPrevToken( tokNum );
        if( prevTok == null ) {
            return( true );
        }

        if( ! prevTok.sourceEquals( tok )) {
            return( true );
        }

        if( ! tl.lineSoFarEmitsText( tokNum )) {
            return( true );
        }

        return( false );
    }

    public String getRoot()
    {
        return( root );
    }
}