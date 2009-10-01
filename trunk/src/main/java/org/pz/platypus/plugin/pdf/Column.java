/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf;

/**
 * Handles the low-level implementation details of columns.
 * Implemented in iText via ColumnText()
 *
 * @author alb
 */
public class Column
{
    /** height of the column in points */
    private float height;

    /** space to skip before column begins. Space is measured in points from the top margin down. */
    private float vertSkip;

    /** width of the column in points */
    private float width;

    /** width of the gutter to the *right* of the column, in points */
    private float gutter;

    /**
     * The basic constructor. Creates one column for the page that matches the area
     * on the page within the margins. Gutter width is 0.
     * @param pdf  PDF document data
     */
    public Column( final PdfData pdf )
    {
        width = pdf.getPageWidth() - pdf.getMarginLeft() - pdf.getMarginRight();
        height = pdf.getPageHeight() - pdf.getMarginTop() - pdf.getMarginBottom();
        gutter = 0f;
        vertSkip = 0f;

    }

//    /**
//     * constructor when the height is equal to the distance between top + bottom margins
//     *
//     * @param colWidth width of the new column
//     * @param colGutterWidth the gutter to the right of the column
//     */
//
//    public Column( final float colWidth, final float colGutterWidth )
//    {
//        width = colWidth;
//        gutter = colGutterWidth;
//
//        vertSkip = 0f;
//        height = GDD.getInstance().getPageSize().getHeight() -
//                    GDD.getInstance().getMargin().getSize( Margins.TOP ) -
//                    GDD.getInstance().getMargin().getSize( Margins.BOTTOM );
//    }
//
    /**
     * constructor for when we need to specify column size exactly
     * @param colWidth new column width (in points)
     * @param colGutterWidth new gutter width (in points) for right gutter
     * @param colVertSkip new distance to skip before column (in points)
     */
    public Column( final float colWidth, final float colGutterWidth, final float colVertSkip,
                   final PdfData pdf )
    {
        width   = colWidth;
        gutter  = colGutterWidth;

        height  = pdf.getPageHeight() - pdf.getMarginTop() - pdf.getMarginBottom() -
                    colVertSkip;

        vertSkip = colVertSkip;
    }

    public float getHeight()
    {
        return height;
    }

    public void setHeight( final float newHeight )
    {
        height = newHeight;
    }

    public float getWidth()
    {
        return width;
    }

    public float getGutter()
    {
        return gutter;
    }

    public float getVertSkip()
    {
        return vertSkip;
    }

    public void setVertSkip( final float newVertSkip ) {
        vertSkip = newVertSkip;
    }

}
