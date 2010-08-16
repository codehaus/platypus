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
import org.pz.platypus.plugin.pdf.PdfData;

/**
 * Implementation of Paragraph Skip command for PDF plugin. Currently designed so that the
 * value only refers to lines. (Lines can be fractional.) 
 *
 * @author alb
 */
public class PdfParagraphSkip implements OutputCommandable
{
    private String root = "[paraskip:";

    public int process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdf = (PdfData) context;

        float newSkip =  tok.getParameter().getAmount();
        // skip must be positive and (skip * leading) must be less than the height of the page
        if ( newSkip < 0 || 
             ( newSkip * pdf.getLeading() ) >= ( pdf.getPageHeight() - pdf.getMarginTop() -
                                        pdf.getMarginBottom() )) {
            GDD gdd = pdf.getGdd();
            gdd.logWarning( gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                            gdd.getLit( "ERROR.INVALID_PARAGRAPH_SKIP" ) + " " +
                            gdd.getLit( "IGNORED" ));

            return 0;
        }

        float currParaSkip = pdf.getParagraphSkip();

        if ( newSkip != currParaSkip ) {
            pdf.setParagraphSkip( newSkip, tok.getSource() );
        }

        return 0;
    }

    public String getRoot()
    {
        return( root );
    }
}