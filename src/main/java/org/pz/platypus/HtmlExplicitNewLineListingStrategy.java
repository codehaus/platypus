package org.pz.platypus;

import java.io.IOException;

/**
 * Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 * <p/>
 * Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 * Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */
public class HtmlExplicitNewLineListingStrategy extends HtmlListingStrategy {
    private final Token tok;

    public HtmlExplicitNewLineListingStrategy(Token tok) {
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
