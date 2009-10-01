/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.Token;
import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.plugin.pdf.PdfOutfile;

/**
 * Implementation of new page, which skips to the top of the next page (and thereby
 * triggers the processing for end of current page).
 *
 * @author alb
 */
public class PdfNewPage implements OutputCommandable
{
    private String root = "[pg]";

    public void process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdf = (PdfData) context;

        PdfOutfile outfile = pdf.getOutfile();
        outfile.newPage();
    }

    public String getRoot()
    {
        return( root );
    }
}