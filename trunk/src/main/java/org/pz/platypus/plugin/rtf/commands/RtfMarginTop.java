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
 * Implementation of changing the size of the top margin
 *
 * @author alb
 */
public class RtfMarginTop implements IOutputCommand
{
    private String root = "[tmargin:";

    public int process( final IOutputContext context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        RtfData rtd = (RtfData) context;

        float tMargin = Conversions.convertParameterToPoints( tok.getParameter(), rtd );
        if ( tMargin < 0 || tMargin > Limits.PAGE_WIDTH_MAX || tMargin < Limits.PAGE_WIDTH_MIN ) {
            GDD gdd = rtd.getGdd();
            gdd.logWarning( gdd.getLit( "FILE#" ) + ": " + tok.getSource().getFileNumber() + " " +
                            gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                            gdd.getLit( "ERROR.INVALID_TOP_MARGIN" ) + ": " + tMargin + " " +
                            gdd.getLit( "IGNORED" ));
            return 0;
        }

        float currtMargin = rtd.getMarginTop();

        if ( tMargin != currtMargin ) {
            RtfOutfile outfile = rtd.getOutfile();
            if( ! outfile.isOpen() ) {
                rtd.setMarginTop( tMargin, tok.getSource() );
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