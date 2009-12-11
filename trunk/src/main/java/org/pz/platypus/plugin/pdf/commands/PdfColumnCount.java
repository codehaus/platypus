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
import org.pz.platypus.plugin.pdf.PdfOutfile;
import org.pz.platypus.plugin.pdf.Columns;

/**
 * Change the number of columns in a document
 *
 * @author alb
 */
public class PdfColumnCount implements OutputCommandable
{
    private String root = "[columns:";

    public void process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdd = (PdfData) context;

        int newColumnCount = (int) tok.getParameter().getAmount();
        if ( newColumnCount < 0 ) {
            GDD gdd = pdd.getGdd();
            gdd.logWarning( gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                            gdd.getLit( "ERROR.INVALID_COLUMN_COUNT" ) + " " +
                            gdd.getLit( "IGNORED" ));
            return;
        }

        int pddColumnCount = pdd.getColumnCount();

        if ( newColumnCount != pddColumnCount ) {
            if( pdd.getOutfile().isOpen() ) {
                flushExistingText( pdd.getOutfile() );
                setupNewColumns( newColumnCount, pdd );
            }
            pdd.setColumnCount( newColumnCount, tok.getSource() );
            return;
        }
    }

    /**
     * Set up the new columns starting wherever we are on the page.
     * We first figure where we are vertically on the page, and then
     * set up the columns to occupy the rest of the vertical space on the page
     *
     * @param newColumnCount new number of columns (guaranteed != to current number)
     * @param pdd the PDF document data
     */
    void setupNewColumns( final int newColumnCount, final PdfData pdd )
    {
        PdfOutfile outfile = pdd.getOutfile();
        float currYposition = outfile.getYposition();
        float verticalSkip = computeVerticalSkip( currYposition, pdd );

        // going from 1 column to many
        if( pdd.getColumnCount() == 1 ) {
            pdd.setColumns( new Columns( newColumnCount, verticalSkip, pdd ));
        }
        else
        // going from many to 1, so then we can close off where we are on the
        // page and go back to a single column. If we are in the leftmost column,
        // then we make the conversion right there on the page. If we are not in the
        // leftmost column, then we presume that the leftmost column has been filled
        // to the bottom of the page, so we need to do a page eject and start on the
        // next page.

        // adjustCurrentColumn(); //-----from v. 0.1.16. Needed?
        if( newColumnCount == 1 ) {
            // are we in the leftmost column?
            if( pdd.getCurrColumn() == 0 ) {
                pdd.setColumns( new Columns( 1, verticalSkip, pdd ));
            }
            else
            // if we're not in the first column, we have to assume
            // that the leftmost column is full to the bottom of the
            // page, so we push out a new page and start in the left
            // column of that page (column 0)
            {
                outfile.newPageLowLevel();
                pdd.setColumns( new Columns( 1, 0f, pdd ));
                pdd.setCurrColumn( 0 );
            }
        }
        else
        // we are going form many columns to another number of many columns
        // so start a new page and resume in column 0
        {
            outfile.newPageLowLevel();
            pdd.setColumns( new Columns( newColumnCount, 0f, pdd ));
            pdd.setCurrColumn( 0 );
        }
    }

    /**
     * Computes how much vertical distance there is between the current baseline and
     * the top margin. Recall that in PDFs, the Y values increase from the bottom upward.
     * So, the top edge of the paper (the page height) is the highest possible value.
     * From this we subtract the top margin and the current Y value from the baseline.
     * This gives us how much distance exists between the top margin and the current baseline.
     *
     * @param yPosition Y position of the current writing point
     * @param pdd PDF document data
     * @return the actual vertical location in absolute points
     */
    float computeVerticalSkip( final float yPosition, final PdfData pdd )
    {
        return( pdd.getPageHeight() - pdd.getMarginTop() - yPosition );
    }

    /**
     * Before changing columns, we need to flush any existing text to the PDF file.
     * That action is performed here.
     *
     * @param outfile the outfile that emits the PDF document
     */
    void flushExistingText( final PdfOutfile outfile )
    {
        outfile.addColumnsContentToDocument();
    }

    public String getRoot()
    {
        return( root );
    }
}