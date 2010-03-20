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
 * TODO: make sure that at the new column size the existing indents are still valid. If they're
 * not, then reset them so that they occupy the same fraction of the column as they presently do.
 *
 * @author alb
 */
public class PdfColumnWidth implements OutputCommandable
{
    private String root = "[columnwidth:";

    public int process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdd = (PdfData) context;

        float colWidth = Conversions.convertParameterToPoints( tok.getParameter(), pdd );
        float writableAreaWidth =  pdd.getPageWidth() - pdd.getMarginLeft() - pdd.getMarginRight();
        if ( colWidth < 0 || colWidth * pdd.getColumnCount() > writableAreaWidth ) {
            showErrorMsg( tok, pdd, colWidth );
            return 0;
        }

        float pddColumnWidth = pdd.getUserSpecifiedColumnWidth();

        if ( colWidth != pddColumnWidth ) {
            pdd.setUserSpecifiedColumnWidth( colWidth, tok.getSource() );
            return 0;
        }
        return 0;
    }

    /**
     * Show error message, giving location in Platypus input file
     * @param tok contains the location data
     * @param pdd contains the location of the logger and literals file
     * @param width which specifies whether column is specified as too wide or too narrow
     */
    void showErrorMsg( final Token tok, final PdfData pdd, final float width )
    {
        GDD gdd = pdd.getGdd();
        StringBuilder msg = new StringBuilder( 30 );
        msg.append( gdd.getLit( "FILE#" ) + tok.getSource().getFileNumber() + " " +
                    gdd.getLit( "LINE#" ) + tok.getSource().getLineNumber() + " "  );

        if( width < 0 ) {
            msg.append( gdd.getLit( "ERROR.COLUMN_WIDTH_CANNOT_BE_NEGATIVE" ));
        }
        else {
            msg.append( pdd.getColumnCount() + " " + gdd.getLit( "COLUMNS" ) + " " +
                            tok.getParameter().getAmount() + " " +
                            Conversions.convertParameterUnitToString( tok.getParameter(), gdd ) + " " +
                            gdd.getLit( "WIDE_WILL_NOT_FIT" ) + "." );
        }
        msg.append( " " + gdd.getLit( "NEW_COLUMN_SIZE_IGNORED" ));

        gdd.logWarning( msg.toString() );
    }

    public String getRoot()
    {
        return( root );
    }
}