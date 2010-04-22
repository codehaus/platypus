/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.test.mocks;

import org.pz.platypus.plugin.pdf.PdfOutfile;
import com.lowagie.text.Paragraph;

/**
 * Mock of PdfOutfile class for use in unit tests
 *
 * @author alb
 */
public class MockPdfOutfile extends PdfOutfile
{
    private String content = null;
    private Paragraph para = new Paragraph();
    private int newParagraphCount = 0;

    @Override
    public void emitChar( final String s, final String fontName ) {
        if( content == null ) {
            content = s;
        }
        else {
            content += s;
        }
    }

    @Override
    public void emitText( final String s ) {
        if( content == null ) {
            content = s;
        }
        else {
            content += s;
        }
    }

    @Override
    public void setItPara( Paragraph par )
    {
        // the paragraph is already set at startup
    }

    @Override
    public Paragraph getItPara() {
        return( para );
    }

    @Override
    public void startNewParagraph() {
        newParagraphCount += 1;
    }

    //=== get activity results ===//

    public String getContent()
    {
        return( content );
    }

    public int getNewParagraphCount()
    {
        return( newParagraphCount );
    }
}
