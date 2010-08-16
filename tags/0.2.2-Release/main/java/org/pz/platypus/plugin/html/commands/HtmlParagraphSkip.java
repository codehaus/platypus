/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.html.commands;

import org.pz.platypus.GDD;
import org.pz.platypus.Token;
import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.plugin.html.HtmlData;

/**
 * Implementation of Paragraph Skip command for Html plugin. Currently designed so that the
 * value only refers to lines. (Lines can be fractional.)
 *
 * @author ask
 */
public class HtmlParagraphSkip implements OutputCommandable
{
    private String root = "[paraskip:";

    public int process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        HtmlData htmlData = (HtmlData) context;

        float newSkip =  tok.getParameter().getAmount();
        // skip must be positive and (skip * leading) must be less than the height of the page
        if ( newSkip < 0 ||
             ( newSkip * htmlData.getLeading() ) >= ( htmlData.getPageHeight() - htmlData.getMarginTop() -
                                        htmlData.getMarginBottom() )) {
            GDD gdd = htmlData.getGdd();
            gdd.logWarning( gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                            gdd.getLit( "ERROR.INVALID_PARAGRAPH_SKIP" ) + " " +
                            gdd.getLit( "IGNORED" ));

            return 0;
        }

        float currParaSkip = htmlData.getParagraphSkip();

        if ( newSkip != currParaSkip ) {
            htmlData.setParagraphSkip( newSkip, tok.getSource() );
        }

        return 0;
    }

    public String getRoot()
    {
        return( root );
    }
}