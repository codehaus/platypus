/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-10 Pacific Data Works LLC. All Rights Reserved.
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
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;

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

    @Test (expected = InvalidConfigFileException.class)
    public void testLoadFromNullFile() throws InvalidConfigFileException
    {
        tfm.loadFromFile( null );
    }

    @Test
    public void testLoadValidOneLineFile() throws InvalidConfigFileException
    {
        String filename = "test-file_OK_to_delete";
        BufferedWriter bw;
        HashMap<String, LinkedList<String>> hmap = tfm.getMap();

        File fin = new File( filename );
        try {
            bw = new BufferedWriter( new FileWriter( filename ));
        }
        catch( Exception e ) {
            bw = null;
        }

        if( bw == null ) {
            fail();
            return;
        }

        try {
            bw.write( "one test line=not a lot\n");
            bw.flush();
            tfm.loadFromFile( filename );
            hmap = tfm.getMap();
        }
        catch( Exception e) {}
        finally{
            if( fin.exists() ) {
                fin.delete();
            }
        }

        assertEquals( 1, hmap.size() );
    }

    @Test
    public void testLoadCommentLine()
    {
        String filename = "test-file_OK_to_delete";
        BufferedWriter bw;
        HashMap<String, LinkedList<String>> hmap = tfm.getMap();

        File fin = new File( filename );
        try {
            bw = new BufferedWriter( new FileWriter( filename ));
        }
        catch( Exception e ) {
            bw = null;
        }

        if( bw == null ) {
            fail();
            return;
        }

        try {
            bw.write( "#one test line=not a lot\n");
            bw.flush();
            tfm.loadFromFile( filename );
            hmap = tfm.getMap();
        }
        catch( Exception e) {}
        finally{
            if( fin.exists() ) {
                fin.delete();
            }
        }

        // line that's a comment (has # as first char) should not load, so size should be = 0
        assertEquals( 0, hmap.size() );
    }

    @Test
    public void testLoadLineWithNoEqualsSign()
    {
        String filename = "test-file_OK_to_delete";
        BufferedWriter bw;
        HashMap<String, LinkedList<String>> hmap = tfm.getMap();

        File fin = new File( filename );
        try {
            bw = new BufferedWriter( new FileWriter( filename ));
        }
        catch( Exception e ) {
            bw = null;
        }

        if( bw == null ) {
            fail();
            return;
        }

        try {
            bw.write( "one test line with no equals sign\n");
            bw.flush();
            tfm.loadFromFile( filename );
            hmap = tfm.getMap();
        }
        catch( Exception e) {}
        finally{
            if( fin.exists() ) {
                fin.delete();
            }
        }

        // line that's contains no = sign should not load, so size should be = 0
        assertEquals( 0, hmap.size() );
    }

    @Test
    public void testExtractFamilyWithNonExistentFile()
    {
        String[] fontNames = tfm.extractFamilyNames( "", null );
        assertEquals( 0, fontNames.length);
    }
}