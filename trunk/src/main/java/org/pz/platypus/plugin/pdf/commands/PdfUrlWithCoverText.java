/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.commands.UrlWithCoverText;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.plugin.pdf.PdfOutfile;

/**
 * Handles processing of URL cover text for PDF.
 *
 * @author alb
 */
public class PdfUrlWithCoverText extends UrlWithCoverText
{
    public void outputUrl( OutputContextable context, final String url, String text )
    {
        String coverText = text;
        
        // if there is no cover text, the URL is printed as a raw URL
        if( coverText.isEmpty() ) {
            coverText = null;
        }

        PdfData pdf = (PdfData) context;
        PdfOutfile outfile = pdf.getOutfile();
        outfile.addUrl( url, coverText );
    }
}