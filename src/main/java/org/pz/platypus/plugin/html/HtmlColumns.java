/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.html;

import org.pz.platypus.DefaultValues;
import org.pz.platypus.GDD;

import java.util.ArrayList;


/**
 * Keeps track of the number of columns, their size, and the gutter size. Does no implementation.
 * For implementation, see the appropriate Column class.
 *
 * @author alb/ask
 */
public class HtmlColumns
{
    private ArrayList<HtmlColumn> columns;

    /**
     * Create an arrayList of default number of columns with no preceding vertical space
     *
     * @param pdf the data on this PDF file
     */
    public HtmlColumns( HtmlData pdf)
    {
        this( DefaultValues.COLUMN_COUNT, 0f, pdf );
    }

    public HtmlColumns( final int howMany, final float verticalSkip, final HtmlData pdf )
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
    public int createColumns( final int newCount, final float verticalSkip, final HtmlData pdf )
    {
        /** the number of columns to implement. Must be > 0 */
        int cols = validateColumnCount( newCount, pdf.getGdd() );

        /** the width of the space into which the columns + gutters must fit */
        float textWidth = pdf.getPageWidth() - pdf.getMarginLeft() - pdf.getMarginRight();

        /** number of gutters. There is no gutter to the right of the rightmost column  */
        int numberOfGutters = cols - 1;

        /** width of each column */
        float colWidth = calculateColumnWidth( textWidth, cols, pdf );

        /** the amount of space used by one gutter */
        float gutterWidth = ( textWidth - ( cols * colWidth )) / numberOfGutters;


        // now add the columns
        this.columns = new ArrayList<HtmlColumn>();


        HtmlColumn c;

        for( int i = 0; i < cols; i++ )
        {
            // if at the last column, margin is 0
            if ( i + 1 == cols ) {
                c = new HtmlColumn( colWidth, 0f, verticalSkip, pdf );
            }
            else {
                c = new HtmlColumn( colWidth, gutterWidth, verticalSkip, pdf );
            }

            columns.add( c );
        }

        return( getCount() );
    }

    /**
     * Make sure the specified number of columns is correct ( > 0 ). If not set it to 1.
     * @param colCount how many columns
     * @param gdd contains the error logger
     * @return  if valid, the column count; otherwise, 1.
     */
    int validateColumnCount( final int colCount, GDD gdd )
    {
        // issue warning if column count was < 0
        if( colCount < 1 ) {
            gdd.logWarning( gdd.getLit( "ERROR.COLUMN_COUNT_MUST_BE_GT_0" + " " +
                            gdd.getLit( "SET_TO_1" )));
            return( 1 );
        }
        else {
            return( colCount );
        }
    }

    float calculateColumnWidth( final float textWidth, final int cols, final HtmlData pdf )
    {
        GDD gdd = pdf.getGdd();
        float gutterWidth = 0f;

        float userSpecifiedWidth = pdf.getUserSpecifiedColumnWidth();
        if( userSpecifiedWidth == 0f ) {    // 0 means use the default calculation
            gutterWidth = getRecommendedGutterSize( cols );
            return (( textWidth - ( gutterWidth * ( cols - 1 ))) / cols );
        }

        // checks to see that user-specified column width x number of cols is < text area width
        if( userSpecifiedWidth < 0 || ( userSpecifiedWidth * cols ) > textWidth ) {
            gdd.logWarning( gdd.getLit( "ERROR.INVALID_COLUMN_WIDTH" + ": " + userSpecifiedWidth ));
            gdd.getLit( "USING_DEFAULT_COLUMN_WIDTH" );
            gutterWidth = getRecommendedGutterSize( cols );
            return (( textWidth - ( gutterWidth * cols - 1 )) / cols );
        }

        return( userSpecifiedWidth );       // else use what the user specified
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
    public int startColumnsAtTop( final HtmlData pdf )
    {
        int i;
        HtmlColumn col;

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
    public HtmlColumn getColumn( final int whichCol )
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
     * @param numberOfCols number of columns on the page
     * @return recommended size of gutter in points.
     */
    private float getRecommendedGutterSize( final int numberOfCols )
    {
        assert( numberOfCols > 0 );

        switch ( numberOfCols )
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