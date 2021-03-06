/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.Token;
import org.pz.platypus.commands.BulletListPlainStart;
import org.pz.platypus.interfaces.IOutputContext;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.plugin.pdf.PdfOutfile;

/**
 * Implementation of turning on a plain bullet list in the PDF plugin
 *
 * @author alb
 */
public class PdfBulletListPlainStart extends BulletListPlainStart
{
    protected int startNewList( final IOutputContext context, Token tok, int tokNum )
    {
        PdfData pdd = (PdfData) context;
        PdfOutfile outFile = pdd.getOutfile();

        String bulletDot = "\u2022";
        outFile.startPlainBulletList( bulletDot );
        return( 0 );
    }

}