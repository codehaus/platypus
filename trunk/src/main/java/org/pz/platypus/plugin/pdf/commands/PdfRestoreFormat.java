/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.Token;
import org.pz.platypus.Source;
import org.pz.platypus.plugin.pdf.*;

/**
 * Restore the previous format from the format stack (thereby, popping it)
 *
 * @author alb
 */
public class PdfRestoreFormat implements OutputCommandable
{
    private String root = "[restorefmt]";

    public void process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdd = (PdfData) context;
        restore( pdd, tok.getSource() );
    }

    /**
     * Restore the current format. This is called so often, it's made static here.
     *
     * @param pdd the PDF data
     * @param source file and line # where this action took place
     */
    static public void restore( final PdfData pdd, final Source source )
    {
        FormatStack formatStack =  pdd.getFormatStack();
        Format format = formatStack.restoreLastFormat();

        pdd.setFont( format.getFont() );
        pdd.setStrikethru( format.isStrikethru(), source  );
        pdd.setEolTreatment( format.getEolHandling(), source );
        pdd.setLeading( format.getLeading(), source );
        
        Underline underline = pdd.getUnderline();
        underline.setInEffect( format.getUnderline().isInEffect(), source );
    }

    public String getRoot()
    {
        return( root );
    }
}