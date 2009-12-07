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
import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.plugin.pdf.Conversions;
import org.pz.platypus.plugin.pdf.PdfData;

/**
 * Implementation of indentation of the first line of a paragraph for PDF plugin
 *
 * @author alb
 */
public class PdfFirstLineIndent implements OutputCommandable
{
    private String root = "[indent:";

    public void process( final OutputContextable context, final Token tok, final int tokNum )
    {
        float newIndent;

        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdf = (PdfData) context;

        try {
            newIndent = obtainNewIndent( tok, pdf );
        }
        catch( InvalidCommandParameterException icpe ) {
            showErrorMsg( tok, pdf );
            return;
        }

        if ( newIndent != pdf.getFirstLineIndent() ) {
            pdf.setFirstLineIndent( newIndent, tok.getSource() );
        }
    }

    /**
     * Extracts the new indent amount from the token and verifies its value.
     * @param tok token containing the new indent specification
     * @param pdf current PDF status info
     * @throws org.pz.platypus.exceptions.InvalidCommandParameterException if indent value is not valid
     * @return the indent if value is valid
     */
    float obtainNewIndent( final Token tok, final PdfData pdf )
            throws InvalidCommandParameterException
    {
        float newIndent = Conversions.convertParameterToPoints( tok.getParameter(), pdf );

        float colWidth = pdf.getUserSpecifiedColumnWidth();
        if( colWidth == 0f ) { // if = 0; get computed size
            colWidth = pdf.getColumns().getColumn( pdf.getCurrColumn() ).getWidth();
        }
        if ( newIndent < 0 || newIndent >= colWidth ) {
            throw new InvalidCommandParameterException();
        }

        return( newIndent );
    }

    /**
     * Show error message, giving location in Platypus input file
     * @param tok contains the location data
     * @param pdf contains the location of the logger and literals file
     */
    void showErrorMsg( final Token tok, final PdfData pdf )
    {
        GDD gdd = pdf.getGdd();
        gdd.logWarning( gdd.getLit( "FILE#" ) + ": " + tok.getSource().getFileNumber() + " " +
                        gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                        gdd.getLit( "ERROR.INVALID_PARAGRAPH_INDENT" ) + " " +
                        gdd.getLit( "IGNORED" ));

    }

    public String getRoot()
    {
        return( root );
    }
}