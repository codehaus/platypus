/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.Token;
import org.pz.platypus.interfaces.IOutputCommand;
import org.pz.platypus.interfaces.IOutputContext;
import org.pz.platypus.plugin.pdf.PdfData;

/**
 * Implementation of fsize (in font family of commands) for PDF plugin
 *
 * @author alb
 */
public class PdfFface implements IOutputCommand
{
    private String root = "[font|face:";

    public int process( final IOutputContext context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdf = (PdfData) context;

        String newFontFace = tok.getParameter().getString();
        pdf.setFontFace( newFontFace, tok.getSource() );

        return 0;
    }

    public String getRoot()
    {
        return( root );
    }
}