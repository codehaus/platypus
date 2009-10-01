/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.Token;
import org.pz.platypus.GDD;
import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.plugin.pdf.PdfOutfile;
import com.lowagie.text.Rectangle;
import com.lowagie.text.DocumentException;

import java.util.HashMap;

/**
 * Handle changes in page size when specified as a string ( LETTER, LEGAL, A4, etc.)
 *
 * @author alb
 */
public class PdfPageSize implements OutputCommandable
{
    private String root = "[pagesize:";

    public void process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdf = (PdfData) context;
        GDD gdd = pdf.getGdd();

        String pageSize = tok.getParameter().getString();
        if( pageSize == null ) {
            gdd.logWarning(
                    gdd.getLit( "FILE#" ) + " " + tok.getSource().getFileNumber() + " " +
                    gdd.getLit( "LINE#" ) + " " + tok.getSource().getLineNumber() + " " +
                    gdd.getLit( "ERROR.PAGE_SIZE_IS_NULL" ) + " " +
                    gdd.getLit( "IGNORED" ));
            return;
        }
        pageSize = pageSize.toUpperCase();

        HashMap<String, Rectangle> pageSizeLookupTable = createPageSizeLookupTable();
        Rectangle size = pageSizeLookupTable.get( pageSize );
        if( size == null ) {
            gdd.logWarning(
                    gdd.getLit( "FILE#" ) + " " + tok.getSource().getFileNumber() + " " +
                    gdd.getLit( "LINE#" ) + " " + tok.getSource().getLineNumber() + " " +
                    gdd.getLit( "ERROR.PAGE_SIZE_IS_INVALID" ) + ": " + pageSize + " " +
                    gdd.getLit( "IGNORED" ));
        }
        else {
            flushExistingText( pdf );
            pdf.setPageHeight( size.getHeight(), tok.getSource() );
            pdf.setPageWidth( size.getWidth(), tok.getSource() );
        }
    }

    /**
     * Current text must be flushed to PDF file with current page size before
     * the new page size takes effect.
     *
     * @param pdf
     */
    public void flushExistingText( final PdfData pdf )
    {
        PdfOutfile outfile = pdf.getOutfile();
        if( outfile == null ) {
            return;
        }

        if( outfile.isOpen() ) {
            outfile.addColumnsContentToDocument();
            try {
                outfile.getItColumn().go();
            }
            catch ( DocumentException de ) {
                System.out.println( "DocException in PdfPageSize()" );
            }
        }
    }

    /**
     * Load the page names and the corresponding iText rectangles into a hash table.
     * for more info on page sizes see: http://en.wikipedia.org/wiki/Paper_size#Loose_sizes
     * for info on ID cards sizes, see: http://en.wikipedia.org/wiki/ISO_7810
     *
     * @return the loaded up HashMap containing all supported page-size names
     */
    HashMap<String, Rectangle> createPageSizeLookupTable()
    {
        HashMap<String, Rectangle> pageNames = new HashMap<String, Rectangle>( 50 );

        pageNames.put( "4A0",           new Rectangle( 4768, 6741 ));           // 1682mm x 2378mm DIN476
        pageNames.put( "2A0",           new Rectangle( 3370, 4768 ));           // 1189mm x 1682mm DIN476
        pageNames.put( "A0",            com.lowagie.text.PageSize.A0 );         //  841mm x 1189mm
        pageNames.put( "A1",            com.lowagie.text.PageSize.A1 );         //  594mm x  841mm
        pageNames.put( "A2",            com.lowagie.text.PageSize.A2 );         //  420mm x  594mm
        pageNames.put( "A3",            com.lowagie.text.PageSize.A3 );         //  297mm x  420mm
        pageNames.put( "A4",            com.lowagie.text.PageSize.A4 );         //  210mm x  297mm
        pageNames.put( "A5",            com.lowagie.text.PageSize.A5 );         //  148mm x  210mm
        pageNames.put( "A6",            com.lowagie.text.PageSize.A6 );         //  105mm x  148mm
        pageNames.put( "A7",            com.lowagie.text.PageSize.A7 );         //   74mm x  105mm
        pageNames.put( "A8",            com.lowagie.text.PageSize.A8 );         //   52mm x   74mm
        pageNames.put( "A9",            com.lowagie.text.PageSize.A9 );         //   37mm x   52mm
        pageNames.put( "A10",           com.lowagie.text.PageSize.A10 );        //   26mm x   37mm
        pageNames.put( "B0",            com.lowagie.text.PageSize.B0 );         // 1000mm x 1414mm
        pageNames.put( "B1",            com.lowagie.text.PageSize.B1 );         //  707mm x 1000mm
        pageNames.put( "B2",            com.lowagie.text.PageSize.B2 );         //  500mm x  707mm
        pageNames.put( "B3",            com.lowagie.text.PageSize.B3 );         //  353mm x  500mm
        pageNames.put( "B4",            com.lowagie.text.PageSize.B4 );         //  250mm x  353mm
        pageNames.put( "B5",            com.lowagie.text.PageSize.B5 );         //  176mm x  250mm
        pageNames.put( "B6",            com.lowagie.text.PageSize.B6 );         //  125mm x  176mm
        pageNames.put( "B7",            com.lowagie.text.PageSize.B7 );         //   88mm x  125mm
        pageNames.put( "B8",            com.lowagie.text.PageSize.B8 );         //   62mm x   88mm
        pageNames.put( "B9",            com.lowagie.text.PageSize.B9 );         //   44mm x   62mm
        pageNames.put( "B10",           com.lowagie.text.PageSize.A10 );        //   31mm x   44mm
        pageNames.put( "BUSCARD-INTL",  new Rectangle( 243 , 153 ));            // Business card int'l
        pageNames.put( "BUSCARD-US",    new Rectangle( 252, 144 ));             // Business card US
        pageNames.put( "ANSI-A",        com.lowagie.text.PageSize.LETTER );     // ANSI Letter
        pageNames.put( "ANSI-B",        com.lowagie.text.PageSize._11X17 );     // ANSI 11 x 17
        pageNames.put( "ANSI-C",        new Rectangle( 1224, 1584 ));           // ANSI 17 x 22
        pageNames.put( "ANSI-D",        new Rectangle( 1584, 2448 ));           // ANSI 22 x 34
        pageNames.put( "ANSI-E",        new Rectangle( 2448, 3168 ));           // ANSI 34 x 44
        pageNames.put( "ARCH-A",        com.lowagie.text.PageSize.ARCH_A );     // architectural A
        pageNames.put( "ARCH-B",        com.lowagie.text.PageSize.ARCH_B );     // architectural B
        pageNames.put( "ARCH-C",        com.lowagie.text.PageSize.ARCH_C );     // architectural C
        pageNames.put( "ARCH-D",        com.lowagie.text.PageSize.ARCH_D );     // architectural D
        pageNames.put( "ARCH-E",        com.lowagie.text.PageSize.ARCH_C );     // architectural E
        pageNames.put( "HALFLETTER",    com.lowagie.text.PageSize.HALFLETTER ); // 5.5" x 8.5"
        pageNames.put( "ID-1",          com.lowagie.text.PageSize.ID_1 );       // ISO 7810 ID-1
        pageNames.put( "ID-2",          com.lowagie.text.PageSize.ID_2 );       // ISO 7810 ID-2
        pageNames.put( "ID-3",          com.lowagie.text.PageSize.ID_3 );       // ISO 7810 ID-3
        pageNames.put( "INDEX3X5",      new Rectangle( 216, 360 ));             //  3" x 5"
        pageNames.put( "INDEX4X6",      new Rectangle( 288, 432 ));             //  4" x 6"
        pageNames.put( "INDEX5X8",      new Rectangle( 360, 576 ));             //  5" x 8"
        pageNames.put( "LEDGER",        com.lowagie.text.PageSize.LEDGER );     // 17"  x 11"
        pageNames.put( "LEGAL",         com.lowagie.text.PageSize.LEGAL );      // 8.5" x 14"
        pageNames.put( "LETTER",        com.lowagie.text.PageSize.LETTER );     // 8.5" x 11"
        pageNames.put( "NOTE",          com.lowagie.text.PageSize.NOTE );       // 7.5" x 10"
        pageNames.put( "11X17",         com.lowagie.text.PageSize._11X17 );     // 11"  x 17"

        return( pageNames );
    }

    public String getRoot()
    {
        return( root );
    }
}