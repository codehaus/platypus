/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.html.commands;

import org.pz.platypus.Token;
import org.pz.platypus.interfaces.IOutputCommand;
import org.pz.platypus.interfaces.IOutputContext;
import org.pz.platypus.plugin.html.HtmlData;

/**
 * Implementation of turning on bold in the Html plugin
 *
 * @author ask
 */
public class HtmlBoldOff implements IOutputCommand
{
    private String root = "[-b]";

    public int process( final IOutputContext context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        HtmlData htmlData = (HtmlData) context;
        htmlData.getHtmlDocContext().push(getRoot());
        htmlData.getHtmlDocContext().outputTags();
        return 0;
    }

    public String getRoot()
    {
        return( root );
    }
}