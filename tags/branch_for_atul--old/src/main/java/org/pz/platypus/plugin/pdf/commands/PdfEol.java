/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.*;
import org.pz.platypus.command.EolTreatment;
import org.pz.platypus.interfaces.*;
import org.pz.platypus.plugin.pdf.*;
import com.lowagie.text.*;

/**
 * End of line command (auto-inserted by Platypus at end of input line) for PDF plugin
 *
 * @author alb
 */
public class PdfEol implements OutputCommandable
{
    private String root = "[cr]";

    public void process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdf = (PdfData) context;
        PdfOutfile outfile = pdf.getOutfile();
        TokenList tl = pdf.getGdd().getInputTokens();
        Token nextTok = tl.getNextToken( tokNum );

        // if we're at the last input token, then do nothing
        if( nextTok == null ) {
            return;
        }

        // if the [cr] occurs at the end of a line that consisted entirely of commands,
        // then ignore it (so that the following text does not have an unwanted leading character).
        // so with a line consisting entirely of [fsize:12pt] simply changes font size, but
        // adds no character to the ouput stream
        if( ! tl.lineSoFarEmitsText( tokNum  )) {
            return;
        }

        // if EolTreatment = hard, issue CR/LF
        if( ! new EolTreatment().isSoft( pdf.getEolTreatment() )) {
            Paragraph para = outfile.getItPara();
            if( para != null ) {
                para.add( new Chunk( Chunk.NEWLINE ));
            }
            return;
        }

        outfile.emitText( " " );
    }

    public String getRoot()
    {
        return( root );
    }
}