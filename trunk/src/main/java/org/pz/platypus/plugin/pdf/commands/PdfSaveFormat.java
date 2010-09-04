/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.interfaces.IOutputCommand;
import org.pz.platypus.interfaces.IOutputContext;
import org.pz.platypus.Token;
import org.pz.platypus.plugin.pdf.*;

/**
 * Save the current format on a stack
 *
 * @author alb
 */
public class PdfSaveFormat implements IOutputCommand
{
    private String root = "[savefmt]";

    public int process( final IOutputContext context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdd = (PdfData) context;
        save( pdd );
        return 0;
    }

    /**
     * Save the current format. This is called so often, it's made static here.
     *
     * @param pdd the PDF data
     */
    static public void save( final PdfData pdd )
    {
        FormatStack formatStack =  pdd.getFormatStack();
        formatStack.saveCurrentFormat( pdd );
    }

    public String getRoot()
    {
        return( root );
    }
}