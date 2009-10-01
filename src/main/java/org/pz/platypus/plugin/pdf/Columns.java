/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf;

import org.pz.platypus.GDD;

import java.util.ArrayList;


/**
 * Keeps track of the number of columns, their size, and the gutter size. Does no implementation.
 * For implementation, see the appropriate Column class.
 * 
 * @author alb
 */
public class Columns
{
    private ArrayList<Column> columns;

    /**
     * Create an arrayList of default number of columns with no preceding vertical space
     *
     * @param pdf the data on this PDF file
     */
    public Columns( PdfData pdf)
    {
        this( DefaultValues.COLUMN_COUNT, 0f, pdf );
    }

    public Columns( final int howMany, final float verticalSkip, PdfData pdf )
    {
        createColumns( howMany, verticalSkip, pdf );
    }
    /**
     * Sets the number of columns. For the time being, these are equally wide and spaced
     * columns. Eventually, we'll permit irregular shapes and varying widths.
     *
     * @param verticalSkip distance to skip on the page before the top of the columns.
     * @param newCount number of new columns
     * @param pdf the Pdf document data
     * @return number of created columns
     */
    public int createColumns( final int newCount, final float verticalSkip, final PdfData pdf )
    {
        /** the number of columns to implement. Must be > 0 */
        final int cols = ( newCount < 1 ? 1 : newCount ); 

        /** the width of the space into which the columns + gutters must fit */
        float textWidth = pdf.getPageWidth() - pdf.getMarginLeft() - pdf.getMarginRight();

        /** number of gutters. There is no gutter to the right of the rightmost column  */
        int numberOfGutters = cols - 1;

        /** the amount of space used by the gutters */
        float gutterSpace = numberOfGutters * getRecommendedGutterSize( cols );

        /** width of each column */
        float colWidth = ( textWidth - gutterSpace ) / cols;

        // now add the columns
        this.columns = new ArrayList<Column>();

        // issue warning if column count was < 0 
        if( newCount < 1 ) {
            GDD gdd = pdf.getGdd();
            gdd.logWarning( gdd.getLit( "ERROR.COLUMN_COUNT_MUST_BE_GT_0" + " " +
                            gdd.getLit( "SET_TO_1" )));
        }

        Column c;

        for( int i = 0; i < cols; i++ )
        {
            // if at the last column, margin is 0
            if ( i + 1 == cols ) {
                c = new Column( colWidth, 0f, verticalSkip, pdf );
            }
            else {
                c = new Column( colWidth, getRecommendedGutterSize( cols ), verticalSkip, pdf );
            }

            columns.add( c );
        }

        return( getCount() );
    }

    /**
     * If we changed column count in the middle of a page, then the columns at the bottom
     * half of the page will be partial height. However, when we start a succeeding page, those
     * columns should now start at the top of the text area of the new page. This function handles
     * that change by reducing verticalSkip to 0 and adjusting height to be the full height of the
     * text area.
     *
     * @param pdf the PdfData bag
     *
     * @return  how many columns were modified.
     */
    public int startColumnsAtTop( final PdfData pdf )
    {
        int i;
        Column col;

        // loop through all columns and make the adjustment
        // TODO: not sure why this was set up to remove then add back in the new columns.
        for( i = 0; i < columns.size(); i++ ) {
            col = columns.get( i );
            columns.remove( i );
            col.setHeight( pdf.getPageHeight() - pdf.getMarginTop() - pdf.getMarginBottom() );
            col.setVertSkip( 0f );
            columns.add( i, col );
        }
        return( i );
    }

    /**
     * Return a designated column
     * @param whichCol the index of the column to get ( 0 = leftmost )
     * @return the specified column, or null on error
     */
    public Column getColumn( final int whichCol )
    {
        if ( whichCol < 0 || whichCol > ( columns.size() - 1 )) {
            return( null );
        }
        else {
            return( columns.get( whichCol ));
        }
    }

    public int getCount()
    {
        return( columns.size() );
    }

    /**
     * Figure out the recommended gutter size for a given number of regular columns
     *
     * @param numberOfColumns number of columns on the page
     * @return recommended size of gutter in points.
     */
    private float getRecommendedGutterSize( final int numberOfColumns )
    {
        assert( numberOfColumns > 0 );

        switch ( numberOfColumns )
        {
            case 1: return(  0f );
            case 2: return( 10f );
            case 3: return(  8f );
            case 4: return(  8f );
            default: return( 6f );
        }
    }

    public String dump( GDD gdd )
    {
        final StringBuffer sb = new StringBuffer();
        sb.append( gdd.getLit( "NUMBER_OF_COLUMNS" ))
          .append( ": " )
          .append( columns.size() );
        return( sb.toString() );
    }

    public int size()
    {
        return( columns.size() );
    }
}
