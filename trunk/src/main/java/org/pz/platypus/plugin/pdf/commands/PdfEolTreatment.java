/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.GDD;
import org.pz.platypus.Token;
import org.pz.platypus.commandTypes.EolTreatment;
import org.pz.platypus.exceptions.EolTreatmentException;
import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.plugin.pdf.PdfData;

/**
 * End of line treatment command for PDF plugin
 *
 * @author alb
 */
public class PdfEolTreatment implements OutputCommandable
{
    private String root = "[eol:";

    /**
     * Validate the specified EOL treatment and set it in pdfData if the setting is valid
     * @param context the PDF data
     * @param tok the token carrying the new treatment
     * @param tokNum token number
     */
    public int process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null || tok.getParameter().getString() == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdfData = (PdfData) context;
        final String treatment = tok.getParameter().getString();
        final EolTreatment eolHandler = new EolTreatment();

        try {
            if( eolHandler.isValid( treatment )) {
                int i = eolHandler.toInteger( treatment );
                if( i != pdfData.getEolTreatment() ) {
                    pdfData.setEolTreatment( i, tok.getSource());
                }
            }
            else {
                showError( tok, pdfData );
            }
        } catch( EolTreatmentException eole ) {
            showError( tok, pdfData );
        }

        return 0;
    }

    /**
     * Show an error message in case a wrong value is specified
     * @param tok the original eol treatment token
     * @param pdata contains PDF state data
     */
    private void showError( final Token tok, final PdfData pdata )
    {
        GDD gdd = pdata.getGdd();
        gdd.logInfo( gdd.getLit( "LINE#" ) + tok.getSource().getLineNumber() + " " +
                gdd.getLit( "ERROR.INVALID_EOL_TREATMENT" ) + " " +
                tok.getParameter().getString() + " " +
                gdd.getLit( "IGNORED" ));
    }

    public String getRoot()
    {
        return( root );
    }
}