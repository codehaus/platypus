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
 * Implementation of changing the size of the right margin
 *
 * @author alb
 */
public class RtfMarginRight implements IOutputCommand
{
    private String root = "[rmargin:";

    public int process( final IOutputContext context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        RtfData rtd = (RtfData) context;

        float rMargin = Conversions.convertParameterToPoints( tok.getParameter(), rtd );
        if ( rMargin < 0 || rMargin > Limits.PAGE_WIDTH_MAX || rMargin < Limits.PAGE_WIDTH_MIN ) {
            GDD gdd = rtd.getGdd();
            gdd.logWarning( gdd.getLit( "FILE#" ) + ": " + tok.getSource().getFileNumber() + " " +
                            gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                            gdd.getLit( "ERROR.INVALID_RIGHT_MARGIN" ) + ": " + rMargin + " " +
                            gdd.getLit( "IGNORED" ));
            return 0;
        }

        float currRMargin = rtd.getMarginRight();

        if ( rMargin != currRMargin ) {
            RtfOutfile outfile = rtd.getOutfile();
            if( ! outfile.isOpen() ) {
                rtd.setMarginRight( rMargin, tok.getSource() );
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