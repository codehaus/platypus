/**
 * Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 * Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 * Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.listing;

import org.pz.platypus.Token;
import org.pz.platypus.GDD;

import java.io.IOException;

/**
 * The "%%" token (line Comment) processing strategy.
 *
 * @author ask
 */

public class HtmlLineCommentListingStrategy extends HtmlListingStrategy
{
    private final Token tok;

    public HtmlLineCommentListingStrategy(Token tok) {
        this.tok = tok;
    }

    public boolean canOutputHtmlEndOfLine() {
        return true;  
    }

    public String format(Token tok, GDD gdd) throws IOException {
        final String s = "<span title=\"" +
                         gdd.getLit( "COMMENT" ) +
                         "\"><font color=\"green\">" +
                         convertToHtmlText( tok.getContent() ) +
                         "</font></span>" +
                         "<br>" ;
        return s;        
    }
}
