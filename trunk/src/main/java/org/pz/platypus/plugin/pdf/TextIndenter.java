/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf;

import org.pz.platypus.Token;
import org.pz.platypus.utilities.Conversions;
import org.pz.platypus.exceptions.InvalidCommandParameterException;

/**
 * Calculates the indent that should be applied to a column of text.
 *
 * @author alb
 */
public class TextIndenter
{
    Token tok;
    PdfData pdd;

    public TextIndenter( final Token t, final PdfData pd )
    {
        assert( t != null );
        assert( pd != null );

        tok = t;
        pdd = pd;
    }

    /**
     * Extracts the new indent amount from the token and verifies its value.
     * @throws InvalidCommandParameterException if indent value is not valid
     * @return column width
     */
    public float calculateIndent() throws InvalidCommandParameterException
    {
        float newIndent = Conversions.convertParameterToPoints( tok.getParameter(), pdd );

        float colWidth = pdd.getUserSpecifiedColumnWidth();
        if( colWidth == 0f ) { // if = 0; user did not specify width
            colWidth = calculateColWidth();
        }

        if ( newIndent < 0 || newIndent >= colWidth ) {
            throw new InvalidCommandParameterException();
        }

        return( newIndent );
    }

    /**
     * Gets the width of the current column, or if the current column has been set
     * artificially to force a new page, then compute the width of column 0.
     * @return  width of the current column 
     */
    float calculateColWidth()
    {
        int currCol = pdd.getCurrColumn();

        // currCol != COLUMN_COUNT_MAX -> OK to use current column's width. No change is pending.
        if( currCol != Limits.COLUMN_COUNT_MAX ) {
            return( pdd.getColumns().getColumn( currCol ).getWidth() );
        }

        // when currCol == Limits.COLUMN_COUNT_MAX, we have previously told PdfOutfile to
        // recalculate the column widths. So we have to do that calculation here to make sure
        // we have indents that are valid for column 0 of the next page.

        Columns cols = new Columns( pdd.getColumnCount(), pdd.getMarginTop(), pdd );
        Column col0 = cols.getColumn( 0 );
        return( col0.getWidth() );
    }
}
