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
 * Test add, reading strings from user strings table.
 *
 * @author alb
 */
public class UserStringsTest
{
    @Test
    public void testUserStringsConstructor()
    {
        final UserStrings us = new UserStrings();
        assertNotNull( us );
        assertEquals( 0, us.getSize() );
    }

    @Test
    public void testNullAdds()
    {
        final UserStrings us = new UserStrings();
        assertNotNull( us );
        assertEquals( 0, us.getSize() );
        int ret;

        ret = us.add( null, "abc" );
        assertEquals( Status.INVALID_PARAM_NULL, ret );
        assertEquals( 0, us.getSize() );

        ret = us.add( "_abc", null );
        assertEquals( Status.INVALID_PARAM_NULL, ret );
        assertEquals( 0, us.getSize() );
    }

    @Test
    public void testInvalidAddInvalidFirstChar()
    {
        final UserStrings us = new UserStrings();
        assertNotNull( us );
        assertEquals( 0, us.getSize() );
        int ret;

        ret = us.add( "_abc", "abc" );
        assertEquals( Status.INVALID_PARAM, ret );
        assertEquals( 0, us.getSize() );

        ret = us.add( "#abc", "bcd" );
        assertEquals( Status.INVALID_PARAM, ret );
        assertEquals( 0, us.getSize() );
    }

    @Test
    public void testInvalidAddInvalidSecondChar()
    {
        final UserStrings us = new UserStrings();
        assertNotNull( us );
        assertEquals( 0, us.getSize() );
        int ret;

        ret = us.add( "$_abc", "abc" );
        assertEquals( Status.INVALID_PARAM, ret );
        assertEquals( 0, us.getSize() );

        ret = us.add( "$$abc", "bcd" );
        assertEquals( Status.INVALID_PARAM, ret );
        assertEquals( 0, us.getSize() );
    }

    @Test
    public void testInvalidAddKeyTooShort()
    {
        final UserStrings us = new UserStrings();
        assertNotNull( us );
        assertEquals( 0, us.getSize() );
        int ret;

        ret = us.add( "$", "abc" );
        assertEquals( Status.INVALID_PARAM, ret );
        assertEquals( 0, us.getSize() );
    }

    @Test
    public void testGetNull()
    {
        final UserStrings us = new UserStrings();
        assertNotNull( us );
        assertTrue( " ".equals( us.getString( null )));
    }

    @Test
    public void testGetNonExistentString()
    {
        final UserStrings us = new UserStrings();
        assertNotNull( us );
        assertNull( us.getString( "this string is not in UserStrings" ));
    }

    @Test
    public void testValidAdd()
    {
        final UserStrings us = new UserStrings();
        assertNotNull( us );
        assertEquals( 0, us.getSize() );
        int ret;

        ret = us.add( "$abc", "Value for $abc" );
        assertEquals( Status.OK, ret );
        assertEquals( 1, us.getSize() );
    }

    @Test
    public void testValidAdds()
    {
        final UserStrings us = new UserStrings();
        assertNotNull( us );
        assertEquals( 0, us.getSize() );
        int ret;

        ret = us.add( "$xx1", "abc" );
        assertEquals( Status.OK, ret );
        assertEquals( 1, us.getSize() );

        ret = us.add( "$xx2", "def" );
        assertEquals( Status.OK, ret );
        assertEquals( 2, us.getSize() );

    }

    @Test
    public void testValidRead()
    {
        final UserStrings us = new UserStrings();
        assertNotNull( us );
        assertEquals( 0, us.getSize() );
        int ret;

        ret = us.add( "$ghi", "Value for $ghi" );
        assertEquals( Status.OK, ret );
        assertEquals( 1, us.getSize() );

        assertTrue( us.getString( "$ghi").equals( "Value for $ghi"));
    }

    @Test
    public void testValidDump()
    {
        final UserStrings us = new UserStrings();
        assertNotNull( us );
        assertEquals( 0, us.getSize() );
        int ret;

        ret = us.add( "$abc", "Value for $abc" );
        assertEquals( Status.OK, ret );
        assertEquals( 1, us.getSize() );

        MockLiterals mockLits = new MockLiterals();
        mockLits.setGetLitShouldReturnKey( true );
        String dumpResults = us.dump( mockLits );
        assertTrue( dumpResults.contains( "USER_DEFINED_STRINGS: \n\t$abc: Value for $abc\n" ));
    }
}