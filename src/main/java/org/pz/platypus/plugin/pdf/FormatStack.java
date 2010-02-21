/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf;

import org.pz.platypus.DefaultValues;

import java.util.Stack;

/**
 * Stack where formats are saved to and restored from
 *
 * @author alb
 */
public class FormatStack
{
    private Stack<Format> formats;

    public FormatStack( final PdfData pdd )
    {
        formats = new Stack<Format>();
        saveFormat( createDefaulFormat( pdd ) );
    }

    public void saveFormat( final Format newFormat )
    {
        if( newFormat != null ) {
            formats.push( newFormat );
        }
    }

    /**
     * Returns a format data structure containing the current format
     * @param pdd the PDF data structure
     * @return the format
     */
    public void saveCurrentFormat( PdfData pdd )
    {
        Format f = new Format (
            pdd.getFont(),
            pdd.getStrikethru(),
            pdd.getUnderline(),
            pdd.getEolTreatment(),
            pdd.getLeading() );

        saveFormat( f );
    }

    public Format restoreLastFormat()
    {
        if( formats.size() == 1 ) {
            return( formats.elementAt( 0 ));
        }

        return( formats.pop() );
    }

    private Format createDefaulFormat( final PdfData pdfData )
    {
        Format defaultFormat = new Format (
                new PdfFont( pdfData ),
                DefaultValues.STRIKETHRU,
                new Underline(),
                DefaultValues.EOL_TREATMENT,
                DefaultValues.LEADING );

        return( defaultFormat );
    }

    /**
     * Used primarily (exclusively?) for testing
     *
     * @return number of formats in the stack (min = 1)
     */
    public int getSize()
    {
        return( formats.size() );
    }
}
