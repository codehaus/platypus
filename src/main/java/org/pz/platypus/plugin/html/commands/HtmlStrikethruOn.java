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
import org.pz.platypus.plugin.html.HtmlOutfile;

/**
 * Implementation of turning on strikethrough in the PDF plugin
 *
 * @author ask
 */
public class HtmlStrikethruOn implements IOutputCommand
{
    private String root = "[+st]";

    public int process( final IOutputContext context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        HtmlData htmlData = (HtmlData) context;
        htmlData.getHtmlDocContext().push(getRoot());
        HtmlOutfile outfile = htmlData.getHtmlDocContext().getOutfile();
        outfile.emitText( "<s>" );
        return 0;
    }

    public String getRoot()
    {
        return( root );
    }
}