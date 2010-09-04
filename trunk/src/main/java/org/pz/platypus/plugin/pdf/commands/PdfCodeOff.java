/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.interfaces.IOutputCommand;
import org.pz.platypus.interfaces.IOutputContext;
import org.pz.platypus.Token;
import org.pz.platypus.plugin.pdf.*;
import com.lowagie.text.Paragraph;

/**
 * End of a code section or code listing
 *
 * @author alb
 */
public class PdfCodeOff implements IOutputCommand
{
    private String root = "[-code]";

    public int process( final IOutputContext context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdd = (PdfData) context;
        if( ! pdd.inCodeSection() ) {
            return 0; //not currently in a code section
        }

        // must output the paragraph manually for listing, because the [-code] command after a
        // listing implies an end of a paragraph.
        if( pdd.isInCodeListing() ) {
            pdd.setInCodeListing( false );
            PdfOutfile outfile = pdd.getOutfile();
            if( outfile != null ) {
                Paragraph para = outfile.getItPara();
                if( para != null && para.size() > 0 ) {
                    outfile.addParagraph( para, outfile.getItColumn() );
                    outfile.setItPara( new Paragraph( pdd.getLeading() ));
                }
            }
        }

        PdfRestoreFormat.restore( pdd, tok.getSource() );
        pdd.setInCodeSection( false, tok.getSource() );
        return 0;
    }

    public String getRoot()
    {
        return( root );
    }
}