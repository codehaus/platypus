/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.GDD;
import org.pz.platypus.Token;
import org.pz.platypus.command.Alignment;
import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.plugin.pdf.PdfData;

/**
 * Alignment command for PDF plugin
 *
 * @author alb
 */
public class PdfAlignment implements OutputCommandable
{
    private String root = "[align:";

    public void process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null || tok.getParameter().getString() == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdfData = (PdfData) context;

        if( tok.getParameter().getString().equals( "center" )) {
            pdfData.setAlignment( Alignment.CENTER, tok.getSource() );
        }
        else
        if( tok.getParameter().getString().equals( "just" ) ||
            tok.getParameter().getString().equals( "justified" )) {
            pdfData.setAlignment( Alignment.JUST, tok.getSource() );
        }
        else
        if( tok.getParameter().getString().equals( "left" )) {
            pdfData.setAlignment( Alignment.LEFT, tok.getSource() );
        }
        else
        if( tok.getParameter().getString().equals( "right" )) {
            pdfData.setAlignment( Alignment.RIGHT, tok.getSource() );
        }
        else
        {
            GDD gdd = pdfData.getGdd();
            gdd.logWarning( gdd.getLit( "FILE#" ) + ": " + tok.getSource().getFileNumber() + " " +
                            gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                            gdd.getLit( "ERROR.INVALID_ALIGNMENT" ) + " " +
                            tok.getParameter().getString() + " " +
                            gdd.getLit( "IGNORED" ));
        }
    }

    @Override
    public String getRoot()
    {
        return( root );
    }
}