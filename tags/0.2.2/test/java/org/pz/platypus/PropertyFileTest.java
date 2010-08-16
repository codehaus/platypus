/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-10 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author alb
 */
public class PropertyFileTest
{

//    private Literals lits;
    private GDD gdd;
    private PropertyFile pf;
    
    @Test
    public void testOpenOfInvalidFile()
    {
        pf = new PropertyFile( "this file does not exist", gdd );
        assertEquals( Status.IO_ERR, pf.load() );
        assertNull( pf.lookup( "file" ));
    }

    @Test
    public void testExistingFile()
    {
        pf = new PropertyFile( "", gdd );
        assertNull( pf.lookup( "file" ));
    }
}