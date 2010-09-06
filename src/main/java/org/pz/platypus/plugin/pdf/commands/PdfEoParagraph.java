/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-10 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.Token;
import org.pz.platypus.interfaces.IOutputCommand;
import org.pz.platypus.interfaces.IOutputContext;
import org.pz.platypus.plugin.pdf.PdfData;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Chunk;

/**
 * End of Paragraph for PDF plugin
 *
 * @author alb
 */
public class PdfEoParagraph implements IOutputCommand
{
    private String root = "[CR]";

    public int process( final IOutputContext context, final Token tok, final int tokNum )
    {
        if( context == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdf = (PdfData) context;

        // if we're in an paragraph with content, then close it and start a new one,
        // else skip a line.
        Paragraph currPar = pdf.getOutfile().getItPara();
        if( currPar == null || ! currPar.isEmpty() ) {
            pdf.getOutfile().startNewParagraph();
        }
        else {
            currPar.add( new Chunk( Chunk.NEWLINE ));
        }
        return 0;

    }

    public String getRoot()
    {
        return( root );
    }
}