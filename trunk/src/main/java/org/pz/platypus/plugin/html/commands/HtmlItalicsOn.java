/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.html.commands;

import org.pz.platypus.Token;
import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.plugin.html.HtmlData;
import org.pz.platypus.plugin.html.HtmlOutfile;

/**
 * Implementation of turning on italics in the Html plugin
 *
 * @author ask
 */
public class HtmlItalicsOn implements OutputCommandable
{
    private String root = "[+i]";

    public int process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }
        HtmlData html = (HtmlData) context;
        html.push(getRoot());        
        HtmlOutfile outfile = html.getOutfile();
        outfile.emitText( "<I>" );
        return 0;
    }

    public String getRoot()
    {
        return( root );
    }
}