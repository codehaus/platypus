package org.pz.platypus;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 * <p/>
 * Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 * Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */
public class HtmlTextListingStrategy extends HtmlListingStrategy {

    private final Token tok;

    public HtmlTextListingStrategy(Token tok) {
        this.tok = tok;
    }

    public boolean canOutputHtmlEndOfLine() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String format(Token tok, GDD gdd) throws IOException {
        return convertToHtmlText( tok.getContent() );
    }

}
