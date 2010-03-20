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
import org.pz.platypus.plugin.pdf.Footer;

/**
 * Implementation of turning on the printing of the page footer in the PDF plugin
 *
 * @author alb
 */
public class PdfFooterOn implements OutputCommandable
{
    private String root = "[+footer]";

    public int process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdf = (PdfData) context;

        Footer footer = pdf.getFooter();
        footer.setShouldWrite( true );

        // if we are still in the pages to be skipped, this command overrides that
        // and starts printing the footer immediately (by modifying the pages to skip value) 
        if( pdf.getPageNumber() <= footer.getPagesToSkip() ) {
            footer.setPagesToSkip( pdf.getPageNumber() - 1 );
        }
        return 0;
    }

    public String getRoot()
    {
        return( root );
    }
}