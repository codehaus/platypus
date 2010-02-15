/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.rtf.commands;

import org.pz.platypus.GDD;
import org.pz.platypus.Token;
import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.plugin.pdf.Conversions;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.plugin.pdf.Limits;

/**
 * Implementation of changing the size of the left margin
 *
 * @author alb
 */
public class RtfMarginLeft implements OutputCommandable
{
    private String root = "[lmargin:";

    public void process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdf = (PdfData) context;

        float lMargin = Conversions.convertParameterToPoints( tok.getParameter(), pdf );
        if ( lMargin < 0 || lMargin > Limits.PAGE_WIDTH_MAX || lMargin < Limits.PAGE_WIDTH_MIN ) {
            GDD gdd = pdf.getGdd();
            gdd.logWarning( gdd.getLit( "FILE#" ) + ": " + tok.getSource().getFileNumber() + " " +
                            gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                            gdd.getLit( "ERROR.INVALID_LEFT_MARGIN" ) + ": " + lMargin + " " +
                            gdd.getLit( "IGNORED" ));
            return;
        }

        float currLMargin = pdf.getMarginLeft();

        if ( lMargin != currLMargin ) {
            pdf.setMarginLeft( lMargin, tok.getSource() );
            return;
        }
    }

    public String getRoot()
    {
        return( root );
    }
}