/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import org.junit.Test;
import static org.junit.Assert.*;
import org.pz.platypus.test.mocks.MockLiterals;

/**
 * Test add, reading strings from system strings table.
 *
 * @author alb
 */
public class SystemStringsTest
{
    @Test
    public void testSystemStringsConstructor()
    {
        final SystemStrings ss = new SystemStrings();
        assertNotNull( ss );
        assertEquals( 0, ss.getSize() );
    }

    @Test
    public void testNullAdds()
    {
        final SystemStrings ss = new SystemStrings();
        assertNotNull( ss );
        assertEquals( 0, ss.getSize() );
        int ret;

        ret = ss.add( null, "abc" );
        assertEquals( Status.INVALID_PARAM_NULL, ret );
        assertEquals( 0, ss.getSize() );

        ret = ss.add( "_abc", null );
        assertEquals( Status.INVALID_PARAM_NULL, ret );
        assertEquals( 0, ss.getSize() );
    }

    @Test
    public void testInvalidAdd()
    {
        final SystemStrings ss = new SystemStrings();
        assertNotNull( ss );
        assertEquals( 0, ss.getSize() );
        int ret;

        ret = ss.add( "$abc", "abc" );
        assertEquals( Status.INVALID_PARAM, ret );
        assertEquals( 0, ss.getSize() );

        ret = ss.add( "#abc", "bcd" );
        assertEquals( Status.INVALID_PARAM, ret );
        assertEquals( 0, ss.getSize() );
    }

    @Test
    public void testGetNull()
    {
        final SystemStrings ss = new SystemStrings();
        assertNotNull( ss );
        assertTrue( " ".equals( ss.getString( null )));
    }

    @Test
    public void testGetNonExistentString()
    {
        final SystemStrings ss = new SystemStrings();
        assertNotNull( ss );
        assertNull( ss.getString( "this string is not in SystemStrings" ));
    }

    @Test
    public void testValidAdd()
    {
        final SystemStrings ss = new SystemStrings();
        assertNotNull( ss );
        assertEquals( 0, ss.getSize() );
        int ret;

        ret = ss.add( "_abc", "Value for _abc" );
        assertEquals( Status.OK, ret );
        assertEquals( 1, ss.getSize() );
    }
    
    @Test
    public void testValidAdds()
    {
        final SystemStrings ss = new SystemStrings();
        assertNotNull( ss );
        assertEquals( 0, ss.getSize() );
        int ret;

        ret = ss.add( "_xx1", "abc" );
        assertEquals( Status.OK, ret );
        assertEquals( 1, ss.getSize() );

        ret = ss.add( "_xx2", "def" );
        assertEquals( Status.OK, ret );
        assertEquals( 2, ss.getSize() );        

    }
    
    @Test
    public void testValidRead()
    {
        final SystemStrings ss = new SystemStrings();
        assertNotNull( ss );
        assertEquals( 0, ss.getSize() );
        int ret;

        ret = ss.add( "_ghi", "Value for _ghi" );
        assertEquals( Status.OK, ret );
        assertEquals( 1, ss.getSize() );

        assertTrue( ss.getString( "_ghi").equals( "Value for _ghi"));
    }

@Test
    public void testValidDump()
    {
        final SystemStrings ss = new SystemStrings();
        assertNotNull( ss );
        assertEquals( 0, ss.getSize() );
        int ret;

        ret = ss.add( "_abc", "Value for _abc" );
        assertEquals( Status.OK, ret );
        assertEquals( 1, ss.getSize() );

        MockLiterals mockLits = new MockLiterals();
        mockLits.setGetLitShouldFindLookupString( true );
        String dumpResults = ss.dump( mockLits );
        assertTrue( dumpResults.contains( "PLATYPUS_STRINGS: \n\t_abc: Value for _abc\n" ));
    }
}
