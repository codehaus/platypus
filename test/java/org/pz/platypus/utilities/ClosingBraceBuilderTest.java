/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.utilities;

import static org.junit.Assert.*;
import org.junit.Test;


/**
 * Test various text transformations
 *
 * @author alb
 */
public class ClosingBraceBuilderTest
{
    @Test
    public void testValid1Brace()
    {
        String str = "{1brace}";
        ClosingBraceBuilder cbb = new ClosingBraceBuilder( str );
        assertEquals( "}", cbb.getClosingBrace() );
    }

    @Test
    public void testValid3Braces()
    {
        String str = "{{{3braces}}}";
        ClosingBraceBuilder cbb = new ClosingBraceBuilder( str );
        assertEquals( "}}}", cbb.getClosingBrace() );
    }

    @Test
    public void testInvalid1Brace_EmptyString()
    {
        String str = "";
        ClosingBraceBuilder cbb = new ClosingBraceBuilder( str );
        assertTrue( cbb.getClosingBrace().isEmpty() );
    }

    @Test
    public void testInvalid1Brace_Null()
    {
        String str = null;
        ClosingBraceBuilder cbb = new ClosingBraceBuilder( str );
        assertTrue( cbb.getClosingBrace().isEmpty() );
    }

    @Test
    public void testInvalid1Brace_NoOpeningBrace()
    {
        String str = "nobrace}}";
        ClosingBraceBuilder cbb = new ClosingBraceBuilder( str );
        assertTrue( cbb.getClosingBrace().isEmpty() );
    }
}