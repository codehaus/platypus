/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-10 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.utilities;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Simple test of PlatypusHomeDirectory. Alas, not much to test here.
 *
 * @author alb
 */
public class PlatypusHomeDirectoryTest
{
    private PlatypusHomeDirectory phd;

    @Before
    public void setUp()
    {
        phd = new PlatypusHomeDirectory( getClass() );
    }

    @Test
    public void testConstructor()
    {
        String homeDir = phd.get();

        if( homeDir == null ) {
            // if the home directory is not set, there's not much to test.
            fail( "*** PLATYUS_HOME MUST BE SET TO RUN UNIT TESTS. PLEASE SPECIFY PLATYPUS_HOME." );
        }
        else {
            assertTrue( homeDir.endsWith( System.getProperty( "file.separator" )));
        }
    }

}
