/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.GDD;
import org.pz.platypus.TypefaceMap;
import org.pz.platypus.exceptions.InvalidConfigFileException;
import org.pz.platypus.test.mocks.MockLogger;
import org.pz.platypus.test.mocks.MockLiterals;

import java.util.logging.Level;
import java.util.LinkedList;
import java.util.HashMap;

/**
 * Unit tests of TypefaceMap class
 *
 * @author alb
 */
public class PdfTypefaceMapTest
{
    private GDD gdd;
    TypefaceMap tfm;

    @Before
    public void setUp()
    {
        gdd = new GDD();
        gdd.initialize();
        gdd.setLogger( new MockLogger() );
        gdd.setLits( new MockLiterals() );
        gdd.getLogger().setLevel( Level.OFF );

        tfm = new TypefaceMap( gdd );
    }

    @Test
    public void testConstructor()
    {
        HashMap<String, LinkedList<String>> hmap = tfm.getMap();
        assertTrue( hmap.isEmpty() );
    }

    /**
     * Fails because it's trying to load the map from a non-existent file in PLATYPUS_HOME,
     * which has not been initialized due to the GDD we passed to TypefaceMap constructor.
     *
     * @throws InvalidConfigFileException
     */
    @Test (expected = InvalidConfigFileException.class)
    public void testLoadMapFromNonExistentFile() throws InvalidConfigFileException
    {
        tfm.loadMapFromFile();
    }

    @Test
    public void testExtractFamilyWithNonExistentFile()
    {
        String[] fontNames = tfm.extractFamilyNames( "", null );
        assertEquals( 0, fontNames.length);
    }



}