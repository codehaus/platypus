/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.rtf.commands;

import org.pz.platypus.*;
import org.pz.platypus.interfaces.*;
import org.pz.platypus.plugin.rtf.*;

import java.io.IOException;

/**
 * Implementation of changing the size of the left margin
 *
 * @author alb
 */
public class RtfMarginLeft implements OutputCommandable
{
    private String root = "[lmargin:";

    public void process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        RtfData rtd = (RtfData) context;

        float lMargin = Conversions.convertParameterToPoints( tok.getParameter(), rtd );
        if ( lMargin < 0 || lMargin > Limits.PAGE_WIDTH_MAX || lMargin < Limits.PAGE_WIDTH_MIN ) {
            GDD gdd = rtd.getGdd();
            gdd.logWarning( gdd.getLit( "FILE#" ) + ": " + tok.getSource().getFileNumber() + " " +
                            gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                            gdd.getLit( "ERROR.INVALID_RIGHT_MARGIN" ) + ": " + lMargin + " " +
                            gdd.getLit( "IGNORED" ));
            return;
        }

        float currLMargin = rtd.getMarginLeft();

        if ( lMargin != currLMargin ) {
            RtfOutfile outfile = rtd.getOutfile();
            if( ! outfile.isOpen() ) {
                rtd.setMarginLeft( lMargin, tok.getSource() );
            }
            else {
                GDD gdd = rtd.getGdd();
                gdd.logWarning( gdd.getLit( "FILE#" ) + ": " + tok.getSource().getFileNumber() + " " +
                                gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                                gdd.getLit( "ERROR.MARGIN_MUST_BE_SET_BEFORE_TEXT_IN_RTF" ) + ": " + root + " " +
                                gdd.getLit( "IGNORED" ));
            }
        }
    }

    public String getRoot()
    {
        return( root );
    }
}