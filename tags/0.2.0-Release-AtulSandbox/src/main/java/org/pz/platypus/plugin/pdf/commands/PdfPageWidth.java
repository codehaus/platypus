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
import org.pz.platypus.plugin.pdf.Limits;

/**
 * Implementation of changing the width of the page in PDF
 *
 * @author alb
 */
public class PdfPageWidth implements OutputCommandable
{
    private String root = "[pagewidth:";

    public void process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdf = (PdfData) context;

        float pageWidth = Conversions.convertParameterToPoints( tok.getParameter(), pdf );
        if ( pageWidth < 0 ||
                pageWidth > Limits.PAGE_WIDTH_MAX || pageWidth < Limits.PAGE_WIDTH_MIN) {
            GDD gdd = pdf.getGdd();
            gdd.logWarning( gdd.getLit( "FILE#" ) + ": " + tok.getSource().getFileNumber() + " " +
                            gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                            gdd.getLit( "ERROR.INVALID_PAGE_HEIGHT" ) + ": " +  pageWidth + " " +
                            gdd.getLit( "IGNORED" ));
            return;
        }

        float docPageWidth = pdf.getPageWidth();

        if ( pageWidth != docPageWidth ) {
            pdf.setPageWidth( pageWidth, tok.getSource() );
        }
    }

    public String getRoot()
    {
        return( root );
    }
}