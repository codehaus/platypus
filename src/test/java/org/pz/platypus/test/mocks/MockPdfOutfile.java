/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.test.mocks;

import org.pz.platypus.plugin.pdf.PdfOutfile;

/**
 * Mock of PdfOutfile class for use in unit tests
 *
 * @author alb
 */
public class MockPdfOutfile extends PdfOutfile
{

    private String content = null;

    public void emitChar( final String s ) {
        content = s;
    }

    public void emitText( final String s ) {
        content = s;
    }

    public String getContent() {
        return( content );
    }
}
