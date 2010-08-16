/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.parsers;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * Unit tests for LineCommentParser parser
 *
 * @author alb
 */
public class LineCommentParserTest
{
    private LineCommentParser lc;
    
    @Before
    public void setUp()
    {
        lc = new LineCommentParser();
    }

    @Test
    public void parseNullLine()
    {
        try {
            lc.isLineComment( null, null );
        }
        catch( IllegalArgumentException ioe ) {
            assertTrue( "test should generate exception", true );
            return;
        }
        fail();
    }

    @Test
    public void testNonCommentLineWithNoCommentBlockInEffect()
    {
        assertFalse( "should return false (not a line comment)",
                     lc.isLineComment(  "xyz", null ));
    }

    @Test
    public void testNonCommentLineWithCommentBlockInEffect()
    {
        assertFalse( "should return false (not a line comment)",
                     lc.isLineComment(  "xyz", "%%]" ));
    }

    @Test
    public void testValidCommentLine()
    {
        assertTrue( "should return true (it's a line comment)",
                     lc.isLineComment(  "%% xyz", null ));
    }

    @Test
    public void testValidCommentLineWithLeadingWhitspace()
    {
        assertTrue( "should return true (it's a line comment)",
                     lc.isLineComment(  "    \t%% xyz", null ));
    }

    @Test
    public void testValidCommentLineInsideACommentBlock()
    {
        assertTrue( "should return true (it's a line comment)",
                     lc.isLineComment(  "%% xyz", "%%]" ));
    }

    @Test
    public void testCommentLineWhenItIsTheCloseForACommentBlock()
    {
        assertFalse( "is a comment block end, not a line comment",
                    lc.isLineComment( "%%]and so...", "%%]" ));
    }
}
