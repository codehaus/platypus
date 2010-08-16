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
public class TextTransformsTest
{
    @Test
    public void testCharArrayToStringFullArrayValid1()
    {
        char[] chars = { 'a', 'b', 'c' };
        String str = TextTransforms.charArrayToString( chars );
        assertEquals( "abc", str );
    }

    @Test
    public void testEmptyCharArray()
    {
        char[] chars = {};
        String str = TextTransforms.charArrayToString( chars );
        assertTrue( str.isEmpty() );
    }

    @Test
    public void testNullCharArray1()
    {
        String str = TextTransforms.charArrayToString( null );
        assertNull( str );
    }

    @Test
    public void testInvalidStartEnd1()
    {
        char[] chars = { 'a', 'b', 'c' };
        String str = TextTransforms.charArrayToString( chars, 0, 3 );
        assertNull( str );
    }

    @Test
    public void testInvalidStartEnd2()
    {
        char[] chars = { 'a', 'b', 'c' };
        String str = TextTransforms.charArrayToString( chars, 0, 0 );
        assertEquals( "a", str );
    }
}
