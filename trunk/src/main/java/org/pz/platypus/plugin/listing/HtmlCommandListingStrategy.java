/**
 * Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 * <p/>
 * Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 * Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.listing;

import org.pz.platypus.Token;
import org.pz.platypus.GDD;

import java.io.IOException;

/**
 * The Command token listing strategy.
 * 
 * @author: ask
 */

public class HtmlCommandListingStrategy extends HtmlListingStrategy {

    private final Token tok;

    public HtmlCommandListingStrategy(Token tok) {
        this.tok = tok;
    }
    
    public String format(Token tok, GDD gdd) throws IOException {
        final String s;

        if( tok.getRoot() != null && tok.getRoot().equals( "[CR]" )) { // print [CR] as a blank line
            s = "<br>\n";
        }
        else {
            s = "<span title=\"" +
                gdd.getLit( "COMMAND" ) +
                "\"><font color=\"blue\">" +
                convertToHtmlText( tok.getContent() ) +
                "</font></span>";
        }
        return s;        
    }

    public boolean canOutputHtmlEndOfLine() {
        return false;  
    }

}
