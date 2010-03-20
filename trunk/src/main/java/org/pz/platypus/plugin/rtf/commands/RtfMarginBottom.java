/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.rtf.commands;

import org.pz.platypus.GDD;
import org.pz.platypus.Token;
import org.pz.platypus.interfaces.*;
import org.pz.platypus.plugin.rtf.*;

/**
 * Implementation of changing the size of the bottom margin
 *
 * @author alb
 */
public class RtfMarginBottom implements OutputCommandable
{
    private String root = "[bmargin:";

    public int process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        RtfData rtd = (RtfData) context;

        float bMargin = Conversions.convertParameterToPoints( tok.getParameter(), rtd );
        if ( bMargin < 0 || bMargin > Limits.PAGE_WIDTH_MAX || bMargin < Limits.PAGE_WIDTH_MIN ) {
            GDD gdd = rtd.getGdd();
            gdd.logWarning( gdd.getLit( "FILE#" ) + ": " + tok.getSource().getFileNumber() + " " +
                            gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                            gdd.getLit( "ERROR.INVALID_BOTTOM_MARGIN" ) + ": " + bMargin + " " +
                            gdd.getLit( "IGNORED" ));
            return 0;
        }

        float currtMargin = rtd.getMarginBottom();

        if ( bMargin != currtMargin ) {
            RtfOutfile outfile = rtd.getOutfile();
            if( ! outfile.isOpen() ) {
                rtd.setMarginBottom( bMargin, tok.getSource() );
            }
            else {
                GDD gdd = rtd.getGdd();
                gdd.logWarning( gdd.getLit( "FILE#" ) + ": " + tok.getSource().getFileNumber() + " " +
                                gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                                gdd.getLit( "ERROR.MARGIN_MUST_BE_SET_BEFORE_TEXT_IN_RTF" ) + ": " + root + " " +
                                gdd.getLit( "IGNORED" ));
            }
        }
        return 0;
    }

    public String getRoot()
    {
        return( root );
    }
}