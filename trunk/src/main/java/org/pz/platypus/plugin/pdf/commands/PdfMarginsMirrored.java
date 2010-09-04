/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.Token;
import org.pz.platypus.interfaces.IOutputCommand;
import org.pz.platypus.interfaces.IOutputContext;
import org.pz.platypus.plugin.pdf.PdfData;

/**
 * Turns on mirrored margins command for PDF plugin. This setting cannot be turned off.
 *
 * @author alb
 */
public class PdfMarginsMirrored implements IOutputCommand
{
    private String root = "[marginsmirrored]";

    public int process( final IOutputContext context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdf = (PdfData) context;
        pdf.setMarginsMirrored( true, tok.getSource() );
        pdf.getOutfile().setMarginsMirrored();
        return 0;
    }

    public String getRoot()
    {
        return( root );
    }
}