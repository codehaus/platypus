/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.commands.UrlRaw;
import org.pz.platypus.interfaces.IOutputContext;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.plugin.pdf.PdfOutfile;

/**
 * Prints a URL without any cover text.
 *
 * @author alb
 */

public class PdfUrl extends UrlRaw
{
    @Override
    protected void outputUrl(final IOutputContext context, String url)
    {
        PdfData pdf = (PdfData) context;
        PdfOutfile outfile = pdf.getOutfile();
        outfile.addUrl( url, null );        
    }
}