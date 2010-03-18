/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.AbstractCommands.UrlCommand;
import org.pz.platypus.Token;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.plugin.pdf.PdfOutfile;

/**
 * Prints a URL without any cover text.
 *
 * @author alb
 */
// public class PdfUrl implements OutputCommandable
public class PdfUrl extends UrlCommand
{

    @Override
    protected void outputUrl(final OutputContextable context, String url, String coverText) {
        PdfData pdf = (PdfData) context;
        PdfOutfile outfile = pdf.getOutfile();
        outfile.addUrl( url, coverText );        
    }
}