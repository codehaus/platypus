/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.html.commands;

import org.pz.platypus.Token;
import org.pz.platypus.interfaces.IOutputCommand;
import org.pz.platypus.interfaces.IOutputContext;
import org.pz.platypus.plugin.html.HtmlData;

/**
 * Implementation of fsize (in font family of commands) for PDF plugin
 *
 * @author alb
 */
public class HtmlFface implements IOutputCommand
{
    private String root = "[font|face:";

    public int process( final IOutputContext context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        HtmlData htmlData = (HtmlData) context;

        String newFontFace = tok.getParameter().getString();
        htmlData.setFontFace( newFontFace, tok.getSource() );
        htmlData.getHtmlDocContext().handleFontFace();
        return 0;
    }

    public String getRoot()
    {
        return( root );
    }
}