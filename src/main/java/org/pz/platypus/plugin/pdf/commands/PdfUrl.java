/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.Token;
import org.pz.platypus.GDD;
import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.plugin.pdf.PdfOutfile;

/**
 * Prints a URL without any cover text.
 *
 * @author alb
 */
public class PdfUrl implements OutputCommandable
{
    private String root = "[url:";

    public void process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdf = (PdfData) context;
        GDD gdd = pdf.getGdd();

        String url = tok.getParameter().getString();
        if( url == null ) {
            gdd.logWarning(
                    gdd.getLit( "FILE#" ) + " " + tok.getSource().getFileNumber() + " " +
                    gdd.getLit( "LINE#" ) + " " + tok.getSource().getLineNumber() + " " +
                    gdd.getLit( "ERROR.ITEM_TO_DUMP_IS_NULL" ) + " " +
                    gdd.getLit( "IGNORED" ));
            return;
        }

        PdfOutfile outfile = pdf.getOutfile();
        outfile.addUrl( url, null );
    }

    public String getRoot()
    {
        return( root );
    }
}