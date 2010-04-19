/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.*;
import org.pz.platypus.plugin.pdf.*;
import com.lowagie.text.Chunk;
import com.lowagie.text.Paragraph;

/**
 * Begin a code section or code listing, with options controlling the line count
 * To be added: - the language for use in syntax highlighting
 *              - file location to extract from a source file
 *
 * Status of implementation:
 *      TO DO: Currently, line number is hard-coded to a max of three digits.
 *             Need error routines (and defaults) in case parameters are in error.
 *
 * @author alb
 */
public class PdfCodeWithOptions implements OutputCommandable
{
    private String root = "[code|";

    public int process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdd = (PdfData) context;
        if( pdd.inCodeSection() ) {
            return( 0 ); //already in a code section
        }

        String param = tok.getParameter().getString();
        if( param == null || ! param.startsWith( "lines:" )) {
            invalidParameterErrMessage( pdd.getGdd(), tok.getSource() );
            return( 0 );
        }

        // process the line count options: starting line number, how often to print the line numbers
        // where 1 = every line, 2 = every even line, x = every line mod x, etc.

        String counts = org.pz.platypus.utilities.TextTransforms.lop( param, "lines:".length() );
        String[] params = counts.split( "," );
        if( params.length != 2 ) {
            invalidParameterErrMessage( pdd.getGdd(), tok.getSource() );
            return( 0 ) ;
        }

        int startingLineNumber = Integer.parseInt( params[0] );
        if( startingLineNumber > 0 ) {
            pdd.setLineNumberLast( startingLineNumber - 1, tok.getSource() );
        }

        int lineNumberSkip = Integer.parseInt( params[1] );
        if( lineNumberSkip > 0 ) {
            pdd.setLineNumberSkip( lineNumberSkip, tok.getSource() );
        }

        // switch to monospace, etc.
        switchToCodeMode( pdd, tok, tokNum );

        int i;
        boolean firstLine = true;
        float initialFontSize = pdd.getFontSize();
        TokenList tokens = pdd.getGdd().getInputTokens();
        
        for( i = tokNum+1; i < tokens.size(); i++ ) {
            Token t = tokens.get( i );
            if( isText( t ) || isBlankLine( t )) {
                pdd.setFontSize( 7.0f, tok.getSource() ); //Should it be a fraction of existing font size?
                pdd.setLineNumberLast( pdd.getLineNumberLast() + 1, t.getSource() );
                if((( firstLine ) || ( pdd.getLineNumberLast() % lineNumberSkip ) == 0 )) {
                    emitLineNumber( pdd );
                    firstLine = false;
                }
                else {
                    emitLineMarker( pdd.getOutfile() );
                }
                
                if( isText( t )) {
                    pdd.setFontSize( initialFontSize, t.getSource() );
                    pdd.getOutfile().emitText( t.getContent() );
                }

                if( isBlankLine( t )) {
                    emitNewLine( pdd.getOutfile() );
                };
            }
            else
            if( t.getType() == TokenType.COMMAND &&  t.getRoot().equals( "[cr]" )) {
                emitNewLine( pdd.getOutfile() );
            }
            else
            if( endOfCode( t )) {
                break;
            }
        }

        if( i >= tokens.size() ) {
            // issue a warning. This is not a problem, but it is poor form.
            GDD gdd = pdd.getGdd();
            gdd.logWarning( gdd.getLit( "WARNING.FILE_ENDS_WITHOUT_CODE_END" ));
        }

        return( i - tokNum - 1 );     // return the number of tokens we skipped
    }

    /**
     * Emits a CR or CR/LF to the PDF outfile.
     * @param outfile to which newLine is written
     */
    private void emitNewLine( final PdfOutfile outfile )
    {
        Paragraph para = outfile.getItPara();
        para.add( new Chunk( Chunk.NEWLINE ));
    }

    /**
     * Is the token text?
     * @param t the token
     *
     * @return true if text, false otherwise
     */
    private boolean isText( final Token t )
    {
        return( t.getType() == TokenType.TEXT );
    }

    /**
     * Is the token a blank line (so, [CR]) ?
     * @param t Token
     *
     * @return true if blank line, false otherwise
     */
    private boolean isBlankLine( final Token t )
    {
        return( t.getType() == TokenType.COMMAND && t.getRoot().equals( "[CR]" ));
    }

    /**
     * Emit the line number for a listing line
     *
     * @param pdd PDF data block
     */
    private void emitLineNumber( PdfData pdd )
    {
        pdd.getOutfile().emitText( String.format( "%3d. ", pdd.getLineNumberLast() ));
    }

    /**
     * Emit the marker for a line with no number
     *
     * @param outfile the name of the PDF outfile
     */
    private void emitLineMarker( PdfOutfile outfile ) {
        outfile.emitText( "   . " );
    }
    //>>> compute width of line number (how many digits should it have?)


    /**
     *  At the [-code] token? (So, at end of code?)
     *
     * @param tok current token
     * @return true if at [-code], false otherwise
     */
    private boolean endOfCode( final Token tok )
    {
        return( tok.getType() == TokenType.COMMAND && tok.getRoot().equals( "[-code]" ));
    }

    /**
     * Code mode means saving the current format and switching to a code font.
     * @param pdd PDF data
     * @param tok token
     * @param tokNum token number of token in the token list.
     */
    private void switchToCodeMode( final PdfData pdd, final Token tok, final int tokNum)
    {
        pdd.getOutfile().startNewParagraph();

        PdfCodeOn turnCodeOn = new PdfCodeOn();
        turnCodeOn.process( pdd, tok, tokNum );
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