/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.Token;
import org.pz.platypus.TokenType;
import org.pz.platypus.Source;
import org.pz.platypus.commands.BulletListPlainEnd;
import org.pz.platypus.interfaces.IOutputContext;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.plugin.pdf.PdfOutfile;

/**
 * Implementation of turning on a plain bullet list in the PDF plugin
 *
 * @author alb
 */
public class PdfBulletListPlainEnd extends BulletListPlainEnd
{
    protected int endBulletList( final IOutputContext context, Token tok, int tokNum )
    {
        PdfData pdd = (PdfData) context;
        PdfOutfile outFile = pdd.getOutfile();
        if( outFile == null ) {
            throw new IllegalArgumentException();
        }

        // here, we end the list.
        outFile.endPlainBulletList();

        // if this is the last command in the line, we don't want the concluding [cr]
        // to insert a blank into the text. Likewise, if there is a new-paragraph command (so, a [CR])
        // on the next line, it will appear to add an extra new line because the end of list in iText
        // adds a CR/LF. So, we handle it hear and decrement for the nonce the # of lines added at paragraph break.

        Token nextTok = pdd.getGdd().getInputTokens().getNextToken( tokNum );
        if( nextTok == null || nextTok.getType() != TokenType.COMMAND ) {
            return( 0 );
        }

        Token nextNextTok = pdd.getGdd().getInputTokens().getNextToken( tokNum+1 );
        if( nextNextTok == null || nextNextTok.getType() != TokenType.COMMAND ) {
            return( 1 ); // to skip over the [cr] token found immediately above in nextToken.
        }

        if( nextTok.getRoot().equals( "[cr]" )) {
            if( nextNextTok.getRoot().equals( "[CR]" )) {
                if( pdd.getOutfile().getItPara() == null || pdd.getOutfile().getItPara().isEmpty() ) {
                   pdd.getOutfile().emitText( "" );
                }
                if( pdd.getGdd().getInputTokens().size() > tokNum+2 ) {
                    float currLinesToSkip = pdd.getParagraphSkip();
                    int currLinesSourceToSkipLine = pdd.getParagraphSkipLine();
                    pdd.setParagraphSkip( currLinesToSkip - 1.0f, tok.getSource() );
                    pdd.getOutfile().startNewParagraph();
                    Source src = tok.getSource();
                    src.setLineNumber( currLinesSourceToSkipLine );
                    pdd.setParagraphSkip( currLinesToSkip, src );
                    return( 2 ); // skip over the [cr] and the [CR]
                }
            }
            return( 1 );
        }
        return( 0 );
    }
}