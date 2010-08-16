/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-10 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.Token;
import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.plugin.common.Underline;

/**
 * Implementation of turning off underline in the PDF plugin
 *
 * @author alb
 */
public class PdfUnderlineOff implements OutputCommandable
{
    private String root = "[-u]";

    public int process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdd = (PdfData) context;

        Underline underline = pdd.getUnderline();
        if( underline.isInEffect() ) {
            underline.setInEffect( false, tok.getSource() );
        }
        return 0;
    }

    public String getRoot()
    {
        return( root );
    }
}