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
 * The compound command processing strategy.
 *
 * @author ask
 */

public class HtmlCompoundCommandListingStrategy extends HtmlListingStrategy
{
    private final Token tok;

    public HtmlCompoundCommandListingStrategy(Token tok) {
        this.tok = tok;
    }
    
    public boolean canOutputHtmlEndOfLine() {
        return false;  
    }

    public String format(Token tok, GDD gdd) throws IOException {
        final String s = "<span title=\"" +
                         gdd.getLit( "COMPOUND_COMMAND" ) +
                         "\"><font color=\"blue\">" +
                         convertToHtmlText( tok.getContent() ) +
                         "</font></span>";
        return s;
    }        
}
