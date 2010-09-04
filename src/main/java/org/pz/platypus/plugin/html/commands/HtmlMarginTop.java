/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.html.commands;

import org.pz.platypus.GDD;
import org.pz.platypus.Token;
import org.pz.platypus.interfaces.IOutputCommand;
import org.pz.platypus.interfaces.IOutputContext;
import org.pz.platypus.plugin.html.HtmlData;
import org.pz.platypus.plugin.pdf.Limits;
import org.pz.platypus.utilities.Conversions;

/**
 * Implementation of changing the size of the top margin
 *
 * @author alb
 */
public class HtmlMarginTop implements IOutputCommand
{
    private String root = "[tmargin:";

    public int process( final IOutputContext context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        HtmlData htmlData = (HtmlData) context;

        float tMargin = Conversions.convertParameterToPoints( tok.getParameter(), htmlData );
        if ( tMargin < 0 || tMargin > Limits.PAGE_HEIGHT_MAX || tMargin < Limits.PAGE_HEIGHT_MIN ) {
            GDD gdd = htmlData.getGdd();
            gdd.logWarning( gdd.getLit( "FILE#" ) + ": " + tok.getSource().getFileNumber() + " " +
                            gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                            gdd.getLit( "ERROR.INVALID_TOP_MARGIN" ) + ": " + tMargin + " " +
                            gdd.getLit( "IGNORED" ));
            return 0;
        }

        float currTMargin = htmlData.getMarginTop();

        if ( tMargin != currTMargin ) {
            htmlData.setMarginTop( tMargin, tok.getSource() );
            htmlData.getHtmlDocContext().getOutfile().setMarginTop();
            return 0;
        }
        return 0;
    }

    public String getRoot()
    {
        return( root );
    }
}