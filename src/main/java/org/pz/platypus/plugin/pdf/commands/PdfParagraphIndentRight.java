/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.GDD;
import org.pz.platypus.Token;
import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.plugin.pdf.Conversions;
import org.pz.platypus.plugin.pdf.PdfData;

/**
 * Implementation of indentation of a full paragraph from the right side for PDF plugin
 *
 * @author alb
 */
public class PdfParagraphIndentRight implements OutputCommandable
{
    private String root = "[paraindentR:";

    public void process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdf = (PdfData) context;
        float indent = obtainNewIndent( tok, pdf );
        if( Float.isNaN( indent )) {
            return;
        }

        float currParaIndentRight = pdf.getParagraphIndentRight();
        if ( indent != currParaIndentRight ) {
            pdf.setParagraphIndentRight( indent, tok.getSource() );
            return;
        }
    }

    /**
     * Extracts the new indent amount from the token and verifies its value.
     * @param tok token containing the new indent specification
     * @param pdf current PDF status info
     * @return the indent if value is valid; otherwise Float.Nan (Nan = a value that's Not a Number)
     */
    float obtainNewIndent( final Token tok, final PdfData pdf )
    {
        float indent = Conversions.convertParameterToPoints( tok.getParameter(), pdf );
        if ( indent < 0 || indent >= pdf.getColumnWidth() ) {
            GDD gdd = pdf.getGdd();
            gdd.logWarning( gdd.getLit( "FILE#" ) + ": " + tok.getSource().getFileNumber() + " " +
                            gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                            gdd.getLit( "ERROR.INVALID_PARAGRAPH_INDENT" ) + " " +
                            gdd.getLit( "IGNORED" ));
            return( Float.NaN );
        }

        return( indent );
    }

    public String getRoot()
    {
        return( root );
    }
}