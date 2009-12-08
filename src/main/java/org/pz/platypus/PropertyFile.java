/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.Map;

/**
 * Reads a properties file and parses it into a Map<String, String>. The Java library
 * has routines for doing this, but they have some constraints, such as naming, that
 * are not found here.
 *
 * @author alb
 */
public class PropertyFile
{
    /** the filename of the property file */
    private String filename;

    /** the Map of key value pairs taken from the property file */
    private final HashMap<String, String> contents;

    /** input reader for file */
    private BufferedReader inReader;

    private final GDD gdd;

    public PropertyFile( final String propertyFilename, final GDD Gdd )
    {
        filename = propertyFilename;
        gdd = Gdd;
        contents = new HashMap<String, String>();
    }

    /**
     * This constructor is used only when extending PropertyFile
     * for testing purposes.
     */
    public PropertyFile()
    {
        filename = null;
        gdd = null;
        contents = new HashMap<String, String>();
    }

    /**
     * read the file into a map. Involves reading the file, trimming the entries, removing the
     * comments, and splitting the lines into key,value pairs at the first = sign
     *
     * @return Status.OK, if all went well; else Status.IO_ERR
     */
    public int load()
    {
        String line;

        if ( open() != Status.OK ) {
            if( gdd == null || gdd.getLits() == null ) {
                // only occur if Literals file is being set up, so we have to
                // hard-code literals to output the error message.
                System.err.println( "Could not find/open: " + filename );
            }
            else {
                gdd.logSevere( gdd.getLit( "ERROR.MISSING_CONFIG_FILE" ) + " " + filename );
            }
            return( Status.IO_ERR );
        }

        try {
            while (( line = inReader.readLine()) != null ) {
                loadLine( line.trim() );
            }
        }
        catch ( IOException ie ) {
            if( gdd != null && gdd.getLogger() != null ) {
                gdd.logSevere( gdd.getLit( "ERROR.PROCESSING_FILE" ) + " " + filename );
            }
            else {
                System.err.println( "Error processing property file: " + filename );
            }
            return( Status.IO_ERR );
        }

        if( gdd != null && gdd.getLogger() != null ) {
            gdd.log( "Property file loaded with " + contents.size() + " entries: " + filename );
        }
        return( Status.OK );
    }

    /**
     * Find the lines with properties; break them into key-value pairs, load them into hash map
     *
     * @param line input lines from the property file
     */
    public void loadLine( final String line )
    {
        String key, value;

        if ( line == null || line.isEmpty() ) {
            return;
        }

        if ( line.startsWith( "#" )) { // comment lines start with #
            return;
        }

        int equalsSign = line.indexOf( "=" );
        if ( equalsSign < 0 ) {
            return; //curr: issue err msg here
        }

        key = line.substring( 0, equalsSign );
        value = line.substring( equalsSign + 1, line.length() );
        contents.put( key, value );        
    }

    /**
     * open the property file
     * @return Status.OK, if all went well; Status.FILE_NOT_FOUND_ERR if file not found.
     */
    int open()
    {
        try {
              inReader = new BufferedReader( new FileReader( filename ));
        }
        catch ( FileNotFoundException e ) {
            return( Status.FILE_NOT_FOUND_ERR );
        }

        return( Status.OK );
    }

    /**
     * Look up a property based on a search key
     * @param key the key used to look up the property
     * @return the property if found; otherwise, null
     */
    public String lookup( final String key )
    {
        try {
            return( contents.get( key ));
        }
        catch( MissingResourceException mre )  {
            return( null );
        }
    }

    //=== getters and setters ===


    public Map<String,String> getContents()
    {
        return( contents );
    }

    public void setFilename( final String newFilename )
    {
        filename = newFilename;
    }

    public Set<String> keySet()
    {
        return( contents.keySet() );
    }

    public int getSize()
    {
        if( contents == null ) {
            return( -1 );
        }
        
        return( contents.size() );
    }

}
