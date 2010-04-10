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
import org.pz.platypus.utilities.Conversions;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.plugin.pdf.Limits;

/**
 * Implementation of changing the height of the page in PDF
 *
 * @author alb
 */
public class PdfPageHeight implements OutputCommandable
{
    private String root = "[pageheight:";

    public int process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdf = (PdfData) context;

        float pageHeight = Conversions.convertParameterToPoints( tok.getParameter(), pdf );
        if ( pageHeight < 0 || pageHeight > Limits.PAGE_HEIGHT_MAX || pageHeight < Limits.PAGE_HEIGHT_MIN) {
            GDD gdd = pdf.getGdd();
            gdd.logWarning( gdd.getLit( "FILE#" ) + ": " + tok.getSource().getFileNumber() + " " +
                            gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                            gdd.getLit( "ERROR.INVALID_PAGE_HEIGHT" ) + ": " +  pageHeight + " " +
                            gdd.getLit( "IGNORED" ));
            return 0;
        }

        float docPageHeight = pdf.getPageHeight();

        if ( pageHeight != docPageHeight ) {
            pdf.setPageHeight( pageHeight, tok.getSource() );
            return 0;
        }
        return 0;
    }

    public String getRoot()
    {
        return( root );
    }
}