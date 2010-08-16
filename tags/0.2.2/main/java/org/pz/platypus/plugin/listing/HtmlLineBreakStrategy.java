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
 * The "[]" token (line break) processing strategy.
 *
 * @author ask
 */

public class HtmlLineBreakStrategy extends HtmlListingStrategy
{
    private final Token tok;

    public HtmlLineBreakStrategy(Token tok) {
        this.tok = tok;
    }

    public String format(Token tok, GDD gdd) throws IOException {
        final String s = tok.getContent().substring( 0, tok.getContent().length() - 2 );
        final String newLine = "<span title=\"" +
                               gdd.getLit( "NEW_PARAGRAPH" ) +
                               "\"><font color=\"blue\">[]</font></span><br>";
        return s+newLine;        
    }

    public boolean canOutputHtmlEndOfLine() {
        return true;
    }
}
