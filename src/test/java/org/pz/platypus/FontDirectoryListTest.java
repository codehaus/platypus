/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.utilities.TextTransforms;

import java.util.*;
import java.io.*;

/**
 * Test of the capability to read fontdir.txt files, which contain a list
 * of directories (one to a line) that contain fonts that Platypus should
 * include when making up fontlist.txt.
 */

public class FontDirectoryListTest
{
    /** list of directories containing font files */
    private LinkedList<String> fontDirs;

    /** the GDD. Used to obtain the PLATYPUS_HOME directory */
    private GDD gdd;

    /** font directory list class */
    FontDirectoryList fdl;


    @Before
    public void setUp()
    {
        fontDirs = new LinkedList<String>();
        fdl = new FontDirectoryList( new GDD() );
    }

    @Test
    public void testAddFontDirNull()
    {
        fdl.addFontDir(fontDirs, null);
        assertEquals(0, fontDirs.size());

        fdl.addFontDir(null, "test" );
        assertEquals( 0, fontDirs.size() );
    }

    @Test
    public  void testAddFontDirWithNoEndingSlash()
    {
        String dirName;
        String currDirName;
        File currDir = new File( "." );

        try {
            currDirName = currDir.getCanonicalPath();
        }
        catch( Exception e ) {
            fail( "could not obtain name of current directory" );
            return;
        }

        if( currDirName.endsWith( "/" ) || currDirName.endsWith( "\\" )) {
            dirName = TextTransforms.truncate( currDirName, 1 );
        }
        else {
            dirName = currDirName;
        }

        fdl.addFontDir( fontDirs, dirName );
        assertEquals( 1, fontDirs.size() );

        Object[] dirs = fontDirs.toArray();
        assertEquals( dirs[0].toString(), dirName );
    }

    @Test
    public  void testAddFontDirWithAnEndingSlash()
    {
        String dirName;
        String currDirName;
        File currDir = new File( "." );

        try {
            currDirName = currDir.getCanonicalPath();
        }
        catch( Exception e ) {
            fail( "could not obtain name of current directory" );
            return;
        }

        if( currDirName.endsWith( "/" ) || currDirName.endsWith( "\\" )) {
            dirName = currDirName;
        }
        else {
            dirName = currDirName + "/";
        }

        fdl.addFontDir( fontDirs, dirName );
        assertEquals( 1, fontDirs.size() );

        Object[] dirs = fontDirs.toArray();
        assertEquals( dirs[0].toString(), TextTransforms.truncate( dirName, 1 ));
    }
}
