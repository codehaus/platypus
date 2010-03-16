/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.html;

import org.pz.platypus.DefaultValues;
import org.pz.platypus.Source;


/**
 * Handles the low-level implementation details of page footer.
 *
 * @author alb
 */
public class HtmlFooter
{
    private HtmlFont font;
    private int pagesToSkip;
    private float baseline;
    private boolean shouldWrite;

    HtmlFooter( final HtmlData pdfData )
    {
        baseline = DefaultValues.BASELINE_LOCATION;

        font = new HtmlFont( pdfData );
        font.setFace( DefaultValues.FOOTER_TYPEFACE, new Source() );
        font.setSize( DefaultValues.FOOTER_FONT_SIZE, new Source() );

        pagesToSkip = DefaultValues.FOOTER_PAGES_TO_SKIP;
        shouldWrite = true;
    }

    //==== getters and setters ====/

    public float getBaseline()
    {
        return baseline;
    }

    public void setBaseline( final float baseline )
    {
        this.baseline = baseline;
    }

    public HtmlFont getFont()
    {
        return font;
    }

    public void setFont( final HtmlFont font )
    {
        this.font = font;
    }

    public int getPagesToSkip()
    {
        return pagesToSkip;
    }

    /**
     * Set the number of pages to skip before the first footer is first printed.
     * Typically, this is set to 1, so that the first page of a document has no
     * page number. In a single-page doc, this is standard. In a multipage doc,
     * the first page is often a cover sheet, so a page number is not needed or
     * wanted.
     *
     * @param pagesToSkip  pages to skip
     */
    public void setPagesToSkip( final int pagesToSkip )
    {
        this.pagesToSkip = pagesToSkip;
    }

    public boolean shouldWrite()
    {
        return shouldWrite;
    }

    public void setShouldWrite( final boolean shouldWrite )
    {
        this.shouldWrite = shouldWrite;
    }

}