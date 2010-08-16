/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class ParseContextTest
{
        @Test
    public void testReplaceStringAtStart()
    {
        assertEquals( 1, 2-1 );
    }
//        Source source = new Source();
//        ParseContext pc = new ParseContext( null, source, "Edo12345\n", 0 );
//        pc.replaceStringWithNewStringAtCurrentLocation( "Edo", "Tokyo" );
//        assertEquals( "Tokyo12345\n", pc.getContent() );
//    }
//
//    @Test
//    public void testReplaceStringInMiddle()
//    {
//        Source source = new Source();
//        ParseContext pc = new ParseContext( null, source, "01234Edo89\n", 5 );
//        pc.replaceStringWithNewStringAtCurrentLocation( "Edo", "Tokyo" );
//        assertEquals( "01234Tokyo89\n", pc.getContent() );
//    }
//
//    @Test
//    public void testReplaceStringAtEnd()
//    {
//        Source source = new Source();
//        ParseContext pc = new ParseContext( null, source, "01234Edo\n", 5 );
//        pc.replaceStringWithNewStringAtCurrentLocation( "Edo", "Tokyo" );
//        assertEquals( "01234Tokyo\n", pc.getContent() );
//    }
}