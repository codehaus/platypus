/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
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
 * Implementation of indentation of a full paragraph for PDF plugin
 *
 * @author alb
 */
public class PdfFirstLineIndent implements OutputCommandable
{
    private String root = "[indent:";

    public void process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdf = (PdfData) context;

        float indent = Conversions.convertParameterToPoints( tok.getParameter(), pdf );
        if ( indent < 0 ) { //TODO: get column width and make it the maximum indent to test for
            GDD gdd = pdf.getGdd();
            gdd.logWarning( gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                            gdd.getLit( "ERROR.INVALID_PARAGRAPH_INDENT" ) + " " +
                            gdd.getLit( "IGNORED" ));
            return;
        }

        float currFirstLineIndent = pdf.getFirstLineIndent();

        if ( indent != currFirstLineIndent ) {
            pdf.setFirstLineIndent( indent, tok.getSource() );
            return;
        }
    }

    public String getRoot()
    {
        return( root );
    }
}