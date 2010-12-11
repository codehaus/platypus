/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.Token;
import org.pz.platypus.TokenList;
import org.pz.platypus.TokenType;
import org.pz.platypus.interfaces.IOutputCommand;
import org.pz.platypus.interfaces.IOutputContext;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.plugin.pdf.PdfOutfile;
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
public class PdfHardCR implements IOutputCommand
{
    private String root = "[]";

    public int process( final IOutputContext context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdd = (PdfData) context;
        PdfOutfile outfile = pdd.getOutfile();
        if( outfile == null ) {
            return( 0 ); //TODO: Error message, though effectively an impossible error.
        }

        // a [] marks the end of a bullet entry. If the entry is a paragraph, (almost always the case)
        // then add it to the current list and return.
        Paragraph currPar = pdd.getOutfile().getItPara();
        if( outfile.inABulletList() ) {
            if( currPar != null && ! currPar.isEmpty() ) {
                outfile.addParagraphToList( currPar );
                return( 0 );
            }
        }

        currPar.add( new Chunk( Chunk.NEWLINE ));

        // if it's the first token in line, it = a blank line, so an additional
        // NEWLINE needs to be emitted, unless it's right after an end of listing command,
        // which has already emitted the extra CR/LF.

        if( isFirstTokenInLine( context, tok, tokNum ) &&
            ! isAfterCodeListing( context, tokNum ) &&
            currPar != null ) {
                currPar.add( new Chunk( Chunk.NEWLINE ));
        }
        return 0;
    }

    /**
     * Were the previous two tokens [-code][cr]? If so, return true.
     * @param context context for this token (needed to get the TokenList )
     * @param tokNum
     * @return
     */
    boolean isAfterCodeListing( final IOutputContext context, final int tokNum )
    {
        TokenList tl = context.getGdd().getInputTokens();

        Token prevTok = tl.getPrevToken( tokNum );
        if( prevTok == null ) {
            return( false );
        }

        if( ! prevTok.getRoot().equals( "[cr]" )) {
            return( false );
        }

        Token prevTok2 = tl.getPrevToken( tokNum - 1 );
        if( prevTok2 == null ) {
            return( false );
        }

        if(( prevTok2.getType() == TokenType.COMMAND ) && prevTok2.getRoot().equals( "[-code]" )) {
            return( true );
        }

        return( false );
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
    private boolean isFirstTokenInLine( final IOutputContext context,
                                        final Token tok,
                                        final int tokNum )
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