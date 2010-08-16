/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.html.commands;

import org.pz.platypus.Token;
import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.plugin.html.HtmlData;

/**
 * End of line command (auto-inserted by Platypus at end of input line) for Html plugin
 *
 * @author ask
 */
public class HtmlEoParagraph implements OutputCommandable
{
    private String root = "[CR]";

    public int process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null ) {
            throw new IllegalArgumentException();
        }

        HtmlData htmlData = (HtmlData) context;

        // Close current paragraph, if any, and start a new one
        htmlData.getOutfile().endCurrentParagraphIfAny();
        htmlData.getOutfile().startNewParagraph();

        return 0;
    }

    public String getRoot()
    {
        return( root );
    }
}