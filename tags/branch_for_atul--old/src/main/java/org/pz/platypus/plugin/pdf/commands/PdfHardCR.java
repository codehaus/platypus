/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.Token;
import org.pz.platypus.TokenList;
import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.plugin.pdf.PdfData;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Chunk;

/**
 * Force a hard CR/LF (note: this command does *not* force an end of paragraph)
 *
 * Note that the main token processor in Pdf.Start() will check the token after []. If it's a [cr],
 * that [cr] is ignored (so that the next line does not begin with a blank).
 *
 * @author alb
 */
public class PdfHardCR implements OutputCommandable
{
    private String root = "[]";

    public void process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdf = (PdfData) context;
        Paragraph currPar = pdf.getOutfile().getItPara();
        if( currPar != null ) {
            currPar.add( new Chunk( Chunk.NEWLINE ));
        }

        // if it's the first token in line, it = a blank line, so an additional
        // NEWLINE needs to be emitted.

        if( isFirstTokenInLine( context, tok, tokNum ) && currPar != null ) {
            currPar.add( new Chunk( Chunk.NEWLINE ));   
        }
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