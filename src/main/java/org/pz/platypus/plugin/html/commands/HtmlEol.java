/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.html.commands;

import org.pz.platypus.Token;
import org.pz.platypus.TokenList;
import org.pz.platypus.commandTypes.EolTreatment;
import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.plugin.html.HtmlData;
import org.pz.platypus.plugin.html.HtmlOutfile;

/**
 * End of line command (auto-inserted by Platypus at end of input line) for PDF plugin
 *
 * @author alb
 */
public class HtmlEol implements OutputCommandable
{
    private String root = "[cr]";

    public int process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null ) {
            throw new IllegalArgumentException();
        }

        HtmlData htmlData = (HtmlData) context;
        HtmlOutfile outfile = htmlData.getOutfile();
        TokenList tl = htmlData.getGdd().getInputTokens();
        Token nextTok = tl.getNextToken( tokNum );

        // if we're at the last input token...
        if( nextTok == null ) {
            htmlData.getOutfile().endCurrentParagraphIfAny();
            return 0;
        }

        // if the [cr] occurs at the end of a line that consisted entirely of commands,
        // then ignore it (so that the following text does not have an unwanted leading character).
        // so with a line consisting entirely of [fsize:12pt] simply changes font size, but
        // adds no character to the ouput stream
        if( ! tl.lineSoFarEmitsText( tokNum  )) {
            return 0;
        }

        // if EolTreatment = hard, issue CR/LF
        if( ! new EolTreatment().isSoft( htmlData.getEolTreatment() )) {
            outfile.emitText( "<br>" );            
            return 0;
        }

        outfile.emitText( " " );
        return 0;
    }

    public String getRoot()
    {
        return( root );
    }
}