/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.rtf.commands;

import org.pz.platypus.GDD;
import org.pz.platypus.Token;
import org.pz.platypus.DefaultValues;
import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.plugin.rtf.Conversions;
import org.pz.platypus.plugin.rtf.Limits;
import org.pz.platypus.plugin.rtf.RtfData;
import org.pz.platypus.plugin.rtf.RtfOutfile;

import java.io.IOException;

/**
 * Implementation of changing the size of the right margin
 *
 * @author alb
 */
public class RtfMarginRight implements OutputCommandable
{
    private String root = "[rmargin:";

    public void process( final OutputContextable context, final Token tok, final int tokNum )
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
            return;
        }

        float currRMargin = rtd.getMarginRight();

        if ( rMargin != currRMargin ) {
            rtd.setMarginRight( rMargin, tok.getSource() );
            try {
                RtfOutfile outfile = rtd.getOutfile();
                if( outfile.isOpen() ) {
                    outfile.writeCommand( "\\margr" + (int)( rMargin * DefaultValues.TWIPS_PER_POINT ) + " " );
                }
            }
            catch( IOException ioe ) {
                GDD gdd = rtd.getGdd();
                // the error has already been communicated, this notice just provides additional data
                gdd.logWarning( gdd.getLit( "FILE#" ) + ": " + tok.getSource().getFileNumber() + " " +
                                gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                                gdd.getLit( "COMMAND" ) + ": " + root + " " +
                                gdd.getLit( "IGNORED" ));
            }
        }
    }

    public String getRoot()
    {
        return( root );
    }
}