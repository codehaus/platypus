/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import java.util.*;
import java.io.File;

import org.pz.platypus.GDD;

/**
 * List of directories containing font files.
 *
 * @author alb
 */
public class FontDirectoryList
{
    /** list of directories containing font files */
    private LinkedList<String> fontDirs;

    /** the GDD. Used to obtain the PLATYPUS_HOME directory */
    private GDD gdd;

    /**
     * We only ever load the default list of directories. At some future point, we will
     * allow users to specify directories that contain additional fonts.
     *
     * @param Gdd the GDD
     */
    public FontDirectoryList( GDD Gdd )
    {
        gdd = Gdd;

        fontDirs = new LinkedList<String>();
        loadDefaultFontDirs( fontDirs );
    }

    /**
     * Create a list of the directories on the system that contain fonts. We use the directories
     * that are employed by iText and we add the Platypus font directory (in PLATYPUS_HOME) and
     * the font files included with the Java JVM.
     *
     * @param dirs the linked list of directories to add fonts to
     */
    public void loadDefaultFontDirs( LinkedList<String> dirs )
    {
        // these are the font directories that iText uses by default
        addFontDir( dirs, "c:/windows/fonts" );
        addFontDir( dirs, "c:/winnt/fonts" );
        addFontDir( dirs, "d:/windows/fonts" );
        addFontDir( dirs, "d:/winnt/fonts" );
        addFontDir( dirs, "/usr/X/lib/X11/fonts/TrueType" );
        addFontDir( dirs, "/usr/openwin/lib/X11/fonts/TrueType" );
        addFontDir( dirs, "/usr/share/fonts/default/TrueType" );
        addFontDir( dirs, "/usr/X11R6/lib/X11/fonts/ttf" );
        addFontDir( dirs, "/Library/Fonts" );
        addFontDir( dirs, "/System/Library/Fonts" );

        // add the fonts in PLATYPUS_HOME (if it's been defined)
        String s = gdd.getHomeDirectory();
        if( s != null ) {
            addFontDir( fontDirs, s + "fonts" );
        }

        // add the fonts in the JVM font directory
        String jvmDir = System.getProperty( "java.home" );
        if( jvmDir != null ) {
            addFontDir( fontDirs, jvmDir + "/lib/fonts" );
        }
    }

    /**
     * verifies that the directory name exists on this system. If so, adds it to
     * the directory list; otherwise, does nothing.
     *
     * @param dirList list of directories to which the new directory is added
     * @param dirName new directory to add. If an error occurs, no directory is added.
     */
    private void addFontDir( LinkedList<String> dirList, final String dirName )
    {
        if( dirList == null || dirName == null ) {
            return;
        }

        File dir = new File( dirName );
        if( dir.isDirectory() ) {
            dirList.add( dirName );
        }
    }

    /**
     * Get the names of the default locations for font files and return those locations
     * as an array of strings
     *
     * @return an array of strings, each one containing the name of a directory holding font files.
     */
    public String[] getDirs()
    {
        if( fontDirs.size() == 0 ) {
            loadDefaultFontDirs( fontDirs );
        }

        String[] dirStrings = new String[fontDirs.size()];

        int i = 0;
        for( String dir : fontDirs )
        {
            dirStrings[i++] = dir;
        }

        return( dirStrings );
    }
}
