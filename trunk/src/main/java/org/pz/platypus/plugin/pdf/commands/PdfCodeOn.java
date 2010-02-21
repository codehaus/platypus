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
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.plugin.pdf.FormatStack;

/**
 * Begin a code section
 *
 * @author alb
 */
public class PdfCodeOn implements OutputCommandable
{
    private String root = "[code]";

    public void process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdd = (PdfData) context;
        if( pdd.inCodeSection() ) {
            return; //already in a code section
        }

        // save the current format
        FormatStack formatStack =  pdd.getFormatStack();
        formatStack.saveCurrentFormat( pdd );

        // now switch to code format
        ///>>>> resume here <<<<

        pdd.setInCode( true );

    }

    public String getRoot()
    {
        return( root );
    }
}
