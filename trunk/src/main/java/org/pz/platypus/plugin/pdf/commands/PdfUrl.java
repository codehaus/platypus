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
        if( context == null || tok == null || tok.getParameter().getString() == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdf = (PdfData) context;
        String urlParameter = tok.getParameter().getString();
        String url;
        String coverText = null;

        // test for "|text: after URL, which would signal presence of cover text. If found,
        // set url and coverText to the respective strings in urlParameter; else, it's all
        // URL, so set url and leave coverText = null
        int textFlag = urlParameter.indexOf( "|text:" );
        if( textFlag > 0 ) {
            coverText = urlParameter.substring( textFlag + "|text:".length() );
            url = urlParameter.substring( 0, textFlag - 1);
        }
        else {
            url = urlParameter;
        }

        if( url == null ) {
            showErrorMsg( tok, pdf );
            return;
        }

        PdfOutfile outfile = pdf.getOutfile();
        outfile.addUrl( url, coverText );
    }

    /**
     * Show error message, giving location in Platypus input file
     * @param tok contains the location data
     * @param pdf contains the location of the logger and literals file
     */
    void showErrorMsg( final Token tok, final PdfData pdf )
    {
        GDD gdd = pdf.getGdd();
        gdd.logWarning( gdd.getLit( "FILE#" ) + ": " + tok.getSource().getFileNumber() + " " +
                        gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                        gdd.getLit( "ERROR.URL_IS_NULL" ) + " " +
                        gdd.getLit( "IGNORED" ));

    }

    public String getRoot()
    {
        return( root );
    }
}