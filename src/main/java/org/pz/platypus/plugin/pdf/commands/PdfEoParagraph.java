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

        // if we're in an paragraph with content, then close it and start a new one.
        // If we're not, it's likely because someone has inserted multiple blank lines,
        // so we just skip a line.
        Paragraph currPar = pdf.getOutfile().getItPara();
//        if( currPar == null || ! currPar.isEmpty() ) {
         if( currPar == null || currPar.size() > 0 ) {
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

/*** NOTE ***
 * Due to the way, iText inserts a CR/LF at the end of the outermonst unordered list, the [CR] command is handled
 * specially by PdfBulletListPlainEnd.java when it occurs immediately after the end of a bullet list. If changes
 * are made to this class above, consider whether they apply to that code as well.
 *
 * For more details, consult PLATYPUS-61.
 ***********/