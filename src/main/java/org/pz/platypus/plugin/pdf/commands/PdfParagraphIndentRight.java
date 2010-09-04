/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.GDD;
import org.pz.platypus.Token;
import org.pz.platypus.exceptions.InvalidCommandParameterException;
import org.pz.platypus.interfaces.IOutputCommand;
import org.pz.platypus.interfaces.IOutputContext;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.plugin.pdf.TextIndenter;

/**
 * Implementation of indentation of a full paragraph from the right side for PDF plugin.
 * Note: this class borrows a lot of code from PdfPargraphIndent. Logic errors found here
 * should be corrected in that file as well.
 *
 * @author alb
 */
public class PdfParagraphIndentRight implements IOutputCommand
{
    private String root = "[paraindentR:";

    public int process( final IOutputContext context, final Token tok, final int tokNum )
    {
        float newIndent;

        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdf = (PdfData) context;

        TextIndenter indenter = new TextIndenter( tok, pdf );


        try {
            newIndent = indenter.calculateIndent();
        }
        catch( InvalidCommandParameterException icpe ) {
            showErrorMsg( tok, pdf );
            return 0;
        }

        if ( newIndent != pdf.getParagraphIndentRight() ) {
            pdf.setParagraphIndentRight( newIndent, tok.getSource() );
        }
        return 0;
    }

    /**
     * Show error message, giving location in Platypus input file
     * @param tok contains the location data
     * @param pdf contains the location of the logger and literals file
     */
    void showErrorMsg( final Token tok, final PdfData pdf )
    {
        GDD gdd = pdf.getGdd();
        gdd.logWarning( gdd.getLit( "FILE#" ) + tok.getSource().getFileNumber() + " " +
                        gdd.getLit( "LINE#" ) + tok.getSource().getLineNumber() + " " +
                        gdd.getLit( "ERROR.INVALID_PARAGRAPH_INDENT" ) + " " +
                        gdd.getLit( "IGNORED" ));
    }

    public String getRoot()
    {
        return( root );
    }
}