/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.interfaces.IOutputCommand;
import org.pz.platypus.interfaces.IOutputContext;
import org.pz.platypus.*;
import org.pz.platypus.commands.CodeWithOptions;
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
public class PdfCodeWithOptions extends CodeWithOptions implements IOutputCommand
{
    public int process( final IOutputContext context, final Token tok, final int tokNum )
    {
        if( super.preProcess( context, tok, tokNum ) == 0 ) {
            return( 0 );
        }

        PdfData pdd = (PdfData) context;

        pdd.setInCodeListing( true );
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
                if(( firstLine ) || ( pdd.getLineNumberLast() % pdd.getLineNumberSkip() ) == 0 ) {
                    emitLineNumber( pdd );
                    firstLine = false;
                }
                else {
                    emitLineMarker( pdd.getOutfile() );
                }
                
                if( isText( t )) {
                    emitCode( initialFontSize, pdd, t );
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
            else { // it's some kind of command //TODO: make sure that line number has been emitted
                if( t.getType()  == TokenType.COMMAND ) {
                    emitCode( initialFontSize, pdd, t );
                }
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
     * Emits code
     */
    private void emitCode( final float fontSize, final PdfData pdd, final Token tok )
    {
        pdd.setFontSize( fontSize, tok.getSource() );
        pdd.getOutfile().emitText( tok.getContent() );
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
    private void emitLineMarker( PdfOutfile outfile )
    {
        outfile.emitText( "   . " );
    }

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
    protected void switchToCodeMode( final PdfData pdd, final Token tok, final int tokNum)
    {
        pdd.getOutfile().startNewParagraph();

        PdfCodeOn turnCodeOn = new PdfCodeOn();
        turnCodeOn.process( pdd, tok, tokNum );
    }
}