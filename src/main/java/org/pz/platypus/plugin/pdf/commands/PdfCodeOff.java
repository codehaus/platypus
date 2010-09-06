/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.interfaces.IOutputCommand;
import org.pz.platypus.interfaces.IOutputContext;
import org.pz.platypus.Token;
import org.pz.platypus.TokenList;
import org.pz.platypus.TokenType;
import org.pz.platypus.plugin.pdf.*;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Chunk;

/**
 * End of a code section or code listing
 *
 * @author alb
 */
public class PdfCodeOff implements IOutputCommand
{
    private String root = "[-code]";

    public int process( final IOutputContext context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdd = (PdfData) context;
        if( ! pdd.inCodeSection() ) {
            return 0; //not currently in a code section
        }

        int addlTokensToSkip = 0;
        TokenList tokens = pdd.getGdd().getInputTokens();

        // must output the paragraph manually for listing, because the [-code] command after a
        // listing implies an end of a paragraph and adds a CR/LF to the listing.
        if( pdd.isInCodeListing() ) {
            pdd.setInCodeListing( false );
            PdfOutfile outfile = pdd.getOutfile();
            if( outfile != null ) {
                Paragraph para = outfile.getItPara();
                if( para != null && para.size() > 0 ) {
                    if( nextTokenIsEol( tokens, tokNum ) && ! justBeforeEndOfParagraph( tokens, tokNum )) {
                        para.add( new Chunk( Chunk.NEWLINE ));
                        addlTokensToSkip += 1;
                    }
//                    outfile.addParagraph( para, outfile.getItColumn() );
//                    outfile.setItPara( new Paragraph( pdd.getLeading() ));
                }
            }
        }

        PdfRestoreFormat.restore( pdd, tok.getSource() );
        pdd.setInCodeSection( false, tok.getSource() );
        return ( addlTokensToSkip );
    }

    /**
     * Is the next command an end of line, that is, a [cr] command?
     * @param tokens list of input tokens
     * @param tokNum  number of the current token, that is, of [-code]
     * @return true if next token is [cr], else false.
     */
    boolean nextTokenIsEol( TokenList tokens, int tokNum )
    {
        int nextTokenNum = tokNum + 1;

        if( tokens.size() > nextTokenNum ) {
            Token nextTok = tokens.get( nextTokenNum );
            if( nextTok.getType().equals( TokenType.COMMAND ) && nextTok.getRoot().equals( "[cr]" )) {
                return( true );
            }
        }

        return( false );
    }

    /**
     * A new paragraph adds the requisite number of lines presuming that it's adding them
     * to the end of the previous paragraph, not at the start of the next line, as is the
     * case here when [-code] is followed by a [cr]. So in this case, don't emit the CR/LF
     * so that the end of paragraph routine will work correctly.
     * @param tokens list of input tokens
     * @param tokNum  number of the current token, that is, of [-code]
     * @return  true if next
     */
    boolean justBeforeEndOfParagraph( TokenList tokens, int tokNum )
    {
        int nextTokenNum = tokNum + 1;

        if( tokens.size() > nextTokenNum+1 ) {
            Token nextTok = tokens.get( nextTokenNum );
            Token nextNextTok = tokens.get( nextTokenNum+1 );
            if( nextTok == null || nextNextTok == null ) {
                return( false );
            }

            if(( nextTok.getType().equals( TokenType.COMMAND ) && nextTok.getRoot().equals( "[cr]" )) &&
                nextNextTok.getType().equals( TokenType.COMMAND ) && nextNextTok.getRoot().equals( "[CR]" )) {
                return( true );
            }
        }

        return( false );
    }

    public String getRoot()
    {
        return( root );
    }
}