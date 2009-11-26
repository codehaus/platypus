/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.utilities;

import org.pz.platypus.Platypus;
import java.net.URL;

/**
 * Gets the home directory for Platypus as set in the user environment.
 *
 * @author alb
 */
public class PlatypusHomeDirectory
{
    private String homeDirectory;

    public PlatypusHomeDirectory()
    {
        this( null );
    }
    /**
     * Creates a string of the home directory from the user environment and
     * makes sure it ends with a file separator.
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
        if ( homeDirectory != null ) {
            final String separator = System.getProperty( "file.separator" );

            if ( ! homeDirectory.endsWith( separator )) {
                homeDirectory += separator;
            }
        }
    }

    /**
     * return the home directory name with a file separator at end
     * @return  the directory name
     */
    public String get()
    {
        return( homeDirectory );
    }
}
