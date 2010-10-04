/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-10 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.utilities;

import java.io.File;

/**
 * Gets the home directory for Platypus as set in the user environment.
 *
 * @author alb
 */
public class PlatypusHomeDirectory
{
    private String homeDirectory;

    /**
     * Creates a string of the home directory from the user environment and
     * makes sure it ends with a file separator. In the event, the home directory
     * is not specified, PlatypusHomeDirectory.get() returns null.
     *
     * @param gddClass used in the commented-out code
     */
    public PlatypusHomeDirectory( final Class gddClass )
    {
        /*-------- the following code works correctly and will eventually be placed into service

        String classResource = gddClass.getName().replace( '.', '/' ) + ".class";
        ClassLoader cLoader = gddClass.getClassLoader();
        URL url =  cLoader.getResource( classResource );
        homeDirectory = url.toString();
        System.out.println( "home dir= " + homeDirectory );
        System.out.println( "classResource= "  + classResource );
        //directory = homeDirectory less leading jar/file: prefix and trailing classResource

        -----------*/

        final String PlatypusEnvString = "PLATYPUS_HOME";

        homeDirectory =  System.getenv( PlatypusEnvString );
        if( homeDirectory == null ) {
            homeDirectory = getHomeDirectoryFromTextFile();
        }

        if ( homeDirectory != null ) {
            final String separator = System.getProperty( "file.separator" );

            if ( ! homeDirectory.endsWith( separator )) {
                homeDirectory += separator;
            }
        }
    }

    /**
     * Looks to see if the current directory holds a file called 'platypus.home'. If it does, and there is
     * a subdirectory called 'config' than the current directory is deemed the home directory.
     *
     * @return home directory for Platypus
     */
    String getHomeDirectoryFromTextFile()
    {
        String homeDir = null;
        final String textFileName = "platypus.home";
        File textFile = new File( textFileName );
        if( textFile.exists() && new File( "config" ).exists() ) {
           homeDir = textFile.getAbsolutePath();
           return( TextTransforms.truncate( homeDir, textFileName.length() ));
        }
        return( homeDir );
    }

    /**
     * return the home directory name with a file separator at end
     * 
     * @return  the directory name
     */
    public String get()
    {
        return( homeDirectory );
    }
}
