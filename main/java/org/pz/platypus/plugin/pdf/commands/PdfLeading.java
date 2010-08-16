/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.GDD;
import org.pz.platypus.Token;
import org.pz.platypus.CommandParameter;
import org.pz.platypus.UnitType;
import org.pz.platypus.utilities.Conversions;
import org.pz.platypus.interfaces.*;
import org.pz.platypus.plugin.pdf.*;

/**
 * Implementation of changing the size of the leading (space between two baselines of text)
 *
 * @author alb
 */
public class PdfLeading implements OutputCommandable
{
    private String root = "[leading:";

    public int process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdf = (PdfData) context;
        CommandParameter parameter = tok.getParameter();

        float leading;
        if( parameter.getUnit() != UnitType.LINE ) {
            leading = Conversions.convertParameterToPoints( parameter, pdf );
        }
        else {
            leading = parameter.getAmount() * pdf.getLineHeight();
        }

        if ( leading < 0 || leading >= pdf.getPageHeight() ) {
            GDD gdd = pdf.getGdd();
            gdd.logWarning( gdd.getLit( "FILE#" ) + ": " + tok.getSource().getFileNumber() + " " +
                            gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                            gdd.getLit( "ERROR.INVALID_LEADING" ) + ": " + leading + " " +
                            gdd.getLit( "IGNORED" ));
            return 0;
        }

        float currLeading = pdf.getLeading();

        // if leading is set using units other than line (1 li, 2 li, etc.), then we
        // set line height to the new leading. However, if line is the unit, we need
        // to avoid changing the line size. This enables a user to say [leading:2li]
        // at one point and then later [leading:1li] to go back to the previous line
        // height.
        if ( Math.abs( leading - currLeading ) > 0.01f ) {  // comparing FP's for inequality
            pdf.setLeading( leading, tok.getSource() );
            if( parameter.getUnit() != UnitType.LINE ) {
                pdf.setLineHeight( leading, tok.getSource() );
            }
        }
        return 0;
    }

    public String getRoot()
    {
        return( root );
    }
}