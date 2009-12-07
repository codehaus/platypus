/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import org.pz.platypus.utilities.PlatypusHomeDirectory;
import java.util.MissingResourceException;

/**
 * Handles getting literal strings from resource bundle. The default resource bundle, called
 * Platypus.properties, must be present or the program aborts. It is in US English. If another
 * language is used, please see documentation to access the appropriate resource bundle.
 *
 * @author alb
 */
public class Literals extends PropertyFile
{
    /**
     * This constructor is included here only because it facilitates writing
     * mock Literals class for testing. It's not called from Platypus working code.
     */
    public Literals()
    {
        super();    
    }
    
    /**
     * Open a resource bundle with the name: resourceName.properties
     * In the event the resource bundle cannot be found, the program
     * shuts down. Clearly, this is a fatal error.
     * 
     * @throws MissingResourceException if resource is not found.
     * @param resourceName  name of the resource bundle
     */
    public Literals( final String resourceName ) throws MissingResourceException
    {
        super();
        String homeDir = new PlatypusHomeDirectory( this.getClass() ).get();
        if( homeDir == null ) {
            System.err.println( "PLATYPUS_HOME is not defined in your environment." +
                                "See Documentation to fix. Exiting..." );
            throw new MissingResourceException( null, null, null );
        }

        String litsFilename =  homeDir + "config" + '\\' + resourceName + ".properties";
        setFilename( litsFilename );

        // Because Literals is created and loaded before a logger has been set up,
        // errors in finding the Literals file need to be written to stderr.
        try {
            if ( load() != Status.OK ) {
                System.err.println( "Missing file: " + litsFilename + " which is required.\n" +
                        "Please place it as indicated and restart Platypus.\n" );
                throw new MissingResourceException( null, null, null );
            }
        }
        catch ( NullPointerException npe ) {
            throw new MissingResourceException( null, null, null );
        }
    }

    /**
     * Looks up a literal in the resource bundle. In the event of error,
     * it returns a string consisting of a single blank. In this manner,
     * the program will continue to work if someone has diddled with the
     * resource file.
     *
     * @param key the name of the literal to be looked up
     * @return the literal String that was searched for
     */
    public String getLit( final String key )
    {
        String lit;

        if ( key == null ) {
            return( " " );
        }

        return(( lit = lookup( key )) == null ? " " : lit );
    }
}
