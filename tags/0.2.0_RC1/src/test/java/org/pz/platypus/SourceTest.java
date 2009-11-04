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

public class SourceTest
{

    @Test
    public void testAbbrevConstructor()
    {
        final Source s = new Source( 6 );
        assertEquals( 0, s.getFileNumber() );
        assertEquals( 6, s.getLineNumber() );
    }

    @Test
    public void testFullConstructor()
    {
        final Source s = new Source( 5, 6 );
        assertEquals( 5, s.getFileNumber() );
        assertEquals( 6, s.getLineNumber() );
    }

    @Test
    public void testCloneEquals()
    {
        final Source s1 = new Source( 5, 6 );
        final Source s2 = s1.clone();
        assertTrue( s2.equals( s1 ));
    }

    @Test
    public void testEqualsSelf()
    {
        final Source s = new Source( 5, 6);
        assertTrue( s.equals( s ));
    }

    @Test
    public void testEqualsNull()
    {
        final Source s = new Source( 5, 6);
        assertFalse( s.equals( null ));
    }

    @Test
    public void testNotEquals()
    {
        final Source s1 = new Source( 5, 6 );
        final Source s2 = new Source( 7, 6 );
        assertFalse( s2.equals( s1 ));
    }

    @Test
    public void testHashCode()
    {
        final Source s1 = new Source( 5, 6 );
        assertEquals( 161, s1.hashCode() );
    }
}