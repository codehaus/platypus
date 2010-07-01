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
 * The "[cr]" token processing strategy.
 * Decides how the carriage return token will be outputted as Html.
 *
 * @author ask
 *
 */
public class HtmlCRListingStrategy extends HtmlListingStrategy {
    private final Token tok;

    public HtmlCRListingStrategy(Token tok) {
        this.tok = tok;
    }

    public String format(Token tok, GDD gdd) throws IOException {
        return "";
    }

    public boolean canOutputHtmlEndOfLine() {
        return true;
    }
}
