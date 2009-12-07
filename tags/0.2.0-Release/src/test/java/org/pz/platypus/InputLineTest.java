/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 *
 */
package org.pz.platypus;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author alb
 */
public class InputLineTest 
{
    @Test
    public void testInitializedConstructor()
    {
        final String content = "Text content of a hypothetical line";
        final Source source = new Source( 3, 16 );
        
        InputLine newIL = new InputLine( source, content );
        assertNotNull( newIL );
        assertEquals( 3, newIL.getSource().getFileNumber() );
        assertEquals( 16, newIL.getSource().getLineNumber() );
        assertEquals( content, newIL.getContent() );
    }

    @Test
    public void testConstructorWithEmptyContentString()
    {
        final String content = "";
        final int linenumber = 237;

        InputLine newIL = new InputLine();
        Source source = new Source( 12, 3456 );
        newIL.setSource( source );
        newIL.setContent( content );

        assertEquals( "12 - 3456:  ", newIL.dump() );
    }

    @Test
    public void testInlineDump()
    {
        final String content = "Text content of a hypothetical line";
        final int linenumber = 6;

        InputLine newIL = new InputLine( linenumber, content );
        assertNotNull( newIL );
        assertEquals( 0, newIL.getSource().getFileNumber() );
        assertEquals( content, newIL.getContent() );
        assertEquals( linenumber, newIL.getSource().getLineNumber() );
        //System.err.println(newIL.dump());
        assertTrue( "00 - 0006: Text content of a hypothetical line".equals( newIL.dump() ));
    }


    @Test
    public void testInlineDumpWithLongContent()
    {
        final String content = "Text content of a hypothetical somewhat long line";
        final int linenumber = 10;

        InputLine newIL = new InputLine( linenumber, content );
        assertNotNull( newIL );
        Source source = new Source( 2, 10 );
        newIL.setSource( source );
        assertEquals( 2, newIL.getSource().getFileNumber() );
        assertEquals( content, newIL.getContent() );
        assertEquals( linenumber, newIL.getSource().getLineNumber() );
        //System.err.println(newIL.dump());
        assertTrue( "02 - 0010: Text content of a hypothetical somewhat ...".equals( newIL.dump() ));
    }
}
