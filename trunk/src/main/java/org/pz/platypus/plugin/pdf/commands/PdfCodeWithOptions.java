/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.Token;
import org.pz.platypus.GDD;
import org.pz.platypus.TokenList;
import org.pz.platypus.TokenType;
import org.pz.platypus.plugin.pdf.*;
import com.lowagie.text.Chunk;
import com.lowagie.text.Paragraph;

/**
 * Begin a code section or code listing, with options controlling the line count
 * To be added: - the language for use in syntax highlighting
 *              - file location to extract from a source file
 *
 * @author alb
 */
public class PdfCodeWithOptions implements OutputCommandable
{
    private String root = "[code|";

    public void process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdd = (PdfData) context;
        if( pdd.inCodeSection() ) {
            return; //already in a code section
        }

        String param = tok.getParameter().getString();
        if( param == null || ! param.startsWith( "lines:" )) {
            GDD gdd = pdd.getGdd();
            gdd.logWarning( gdd.getLit( "ERROR.INVALID_PARAMETER_FOR_CODE_COMMAND" ) +  ". "  +
                            gdd.getLit( "SWITCHING_TO_PLAIN_CODE_FORMAT" ));
            switchToCodeMode( pdd,tok, tokNum );
        }

        // process the line count options: starting line number, how often to print the line numbers
        // where 1 = every line, 2 = every other line, 3 = every third line, etc.

        String counts = org.pz.platypus.utilities.TextTransforms.lop( param, "lines:".length() );
        String[] params = counts.split( "," );
        if( params.length != 2 ) {
            /// error message.    
        }

        int startingLineNumber = Integer.parseInt( params[0] );
        if( startingLineNumber > 0 ) {
            pdd.setLineNumberLast( startingLineNumber - 1, tok.getSource() );
        }

        int lineNumberSkip     = Integer.parseInt( params[1] );
        if( lineNumberSkip > 0 ) {
            pdd.setLineNumberSkip( lineNumberSkip, tok.getSource() );
        }

        // start outputting the lines of code
        switchToCodeMode( pdd, tok, tokNum );

        float initialFontSize = pdd.getFontSize();
        TokenList tokens = pdd.getGdd().getInputTokens();
        for( int i = tokNum+1; i < tokens.size(); i++ ) {
            Token t = tokens.get( i );
            if( t.getType() == TokenType.TEXT ) {
                pdd.setFontSize( 7.0f, tok.getSource() ); //TODO: make a fraction of existing font size.
                pdd.setLineNumberLast( pdd.getLineNumberLast() + 1, t.getSource() );
                pdd.getOutfile().emitText( String.format( "%3d. ", pdd.getLineNumberLast() ));
                pdd.setFontSize( initialFontSize, t.getSource() );
                pdd.getOutfile().emitText( t.getContent() );
            }
            else
            if( t.getType() == TokenType.COMMAND && t.getRoot().equals( "[cr]" )) {
                Paragraph para = pdd.getOutfile().getItPara();
                para.add( new Chunk( Chunk.NEWLINE ));
            }
            else
            if( t.getType() == TokenType.COMMAND && t.getRoot().equals( "[CR]" )) {
                Paragraph para = pdd.getOutfile().getItPara();
                para.add( new Chunk( Chunk.NEWLINE ));
                para.add( new Chunk( Chunk.NEWLINE ));
            }
            else
            if( t.getType() == TokenType.COMMAND && t.getRoot().equals( "[-code]" )) {
                return;
            }
        }
    }

    //>>> need to be able to have token parser that called this routine skip to the [-code] token, or end of file.
    //>>> print - marks for non-numbered lines
    //>>> make sure empty lines get line numbers

    private void switchToCodeMode( final PdfData pdd, final Token tok, final int tokNum)
    {
        pdd.getOutfile().startNewParagraph();

        PdfCodeOn turnCodeOn = new PdfCodeOn();
        turnCodeOn.process( pdd, tok, tokNum );
    }


    public String getRoot()
    {
        return( root );
    }
}