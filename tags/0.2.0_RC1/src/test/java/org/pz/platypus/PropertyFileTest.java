/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.pz.platypus.test.mocks.MockLiterals;

import java.util.logging.Level;

/**
 *
 * @author alb
 */
public class PropertyFileTest {

    private Literals lits;
    private GDD gdd;
    private PropertyFile pf;

    @Before
    public void setUp() {
        lits = new Literals( "Platypus" );
        gdd = new GDD();
        gdd.setLits( lits );
        gdd.setupLogger( ( "org.pz.platypus.Platypus" ));
        gdd.getLogger().setLevel( Level.OFF );
    }
    
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