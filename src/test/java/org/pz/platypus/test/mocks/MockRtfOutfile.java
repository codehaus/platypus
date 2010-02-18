/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.test.mocks;

import org.pz.platypus.plugin.rtf.RtfOutfile;
import org.pz.platypus.plugin.rtf.RtfData;

/**
 * Mock of RtfOutfile class for use in unit tests
 *
 * @author alb
 */
public class MockRtfOutfile extends RtfOutfile
{
    private boolean openStatus = false;
    private String content = null;

    public MockRtfOutfile( String filename,  RtfData rtd )
    {
        super( filename, rtd );
    }

    public void emitChar( final String s ) {
        content = s;
    }

    public void emitText( final String s ) {
        content = s;
    }

    public String getContent() {
        return( content );
    }

    public void setOpenStatus( boolean tf )
    {
        openStatus = tf;
    }

    public boolean isOpen()
    {
        return( openStatus );
    }
}