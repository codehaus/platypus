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
import org.pz.platypus.plugin.pdf.*;

/**
 * Change the width of columns on a page. This command specifies a width that is applied to
 * all the columns on the page.
 *
 * @author alb
 */
public class PdfColumnWidth implements OutputCommandable
{
    private String root = "[columnwidth:";

    public void process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdd = (PdfData) context;

        float colWidth = Conversions.convertParameterToPoints( tok.getParameter(), pdd );
        float writableAreaWidth =  pdd.getPageWidth() - pdd.getMarginLeft() - pdd.getMarginRight();
        if ( colWidth < 0 || colWidth * pdd.getColumnCount() > writableAreaWidth ) {
            showErrorMsg( tok, pdd, colWidth );
            return;
        }

        float pddColumnWidth = pdd.getUserSpecifiedColumnWidth();

        if ( colWidth != pddColumnWidth ) {
            pdd.setUserSpecifiedColumnWidth( colWidth, tok.getSource() );
            return;
        }
    }

    /**
     * Show error message, giving location in Platypus input file
     * @param tok contains the location data
     * @param pdd contains the location of the logger and literals file
     */
    void showErrorMsg( final Token tok, final PdfData pdd, final float colWidth )
    {
            GDD gdd = pdd.getGdd();
            gdd.logWarning( gdd.getLit( "FILE#" ) + ": " + tok.getSource().getFileNumber() + " " +
                            gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                            gdd.getLit( "ERROR.INVALID_COLUMN_WIDTH" ) + ": " + colWidth + " " +
                            gdd.getLit( "IGNORED" ));
    }

    public String getRoot()
    {
        return( root );
    }
}