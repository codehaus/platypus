/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Manages the user-visible Platypus system strings  (version #, etc.)
 * System strings have the format of starting with: _ followed by any number of alphanumerics or _
 * They can be read by the document processing, but they are not modifiable by the document.
 *
 * @author alb (Andrew Binstock)
 */
public class SystemStrings
{
    @SuppressWarnings("unchecked")  // don't issue spurious unchecked warnings on tree accesses
                                    // works only with Java 5 compiler, later releases
    private final TreeMap sysStrings;

    public SystemStrings()
    {
        sysStrings = new TreeMap<String, String>();
    }

    /**
     * Add a string to collection of Platypus system strings. Only Platypus can do
     * this internally. The user cannot add strings.
     * @param key string that should start with _
     * @param value where any legal string is OK except null
     * @return  Status.OK; on error: Status.INVALID_PARAM or Status.INVALID_PARAM_NULL
     */
    public int add( final String key, final String value )
    {
        if ( key == null || value == null ) {
            return( Status.INVALID_PARAM_NULL );
        }

        if ( ! key.startsWith( "_" )) {
            return( Status.INVALID_PARAM );
        }

        try {
            sysStrings.put( key, value );
        }
        catch( ClassCastException cce ) {
            return( Status.INVALID_PARAM );
        }
        catch( NullPointerException npe ) {
            // a theoretically impossible failure given null-test above
            return( Status.INVALID_PARAM_NULL );
        }

        return( Status.OK );
    }

    /**
     * Returns the string associated to a given key
     * @param key the key to use for the lookup
     * @return the string; null, if an error occurs
     */
    public String getString( final String key )
    {
        if ( key == null ) {
            return( " " );
        }

        Object retObject = (String) sysStrings.get( key );
        if ( retObject != null ) {
            return( retObject.toString() );
        }
        else {
            return( null );
        }
    }

    /**
     * Returns a string containing all the keys and values formatted
     * one-to-a-line for printing to console or document.
     * 
     * @param lits literals to use in the dump output
     * @return a printable/displayable string with all the strings, 1 per line.
     */
    public String dump( final Literals lits )
    {
        int counter = 0;
        final StringBuffer sb = new StringBuffer( 300 );

        sb.append( lits.getLit( "PLATYPUS_STRINGS" ))
          .append( ": \n" );

        Set set = sysStrings.entrySet();
        Iterator iterator = set.iterator();
        while ( iterator.hasNext() )
        {
            Map.Entry entry =  ( Map.Entry ) iterator.next();
            sb.append( '\t' )
              .append( entry.getKey() )
              .append( ": " )
              .append( entry.getValue() )
              .append( '\n' );

            counter++;
        }

        if ( counter == 0 ) {
            sb.append( '\t' )
              .append( lits.getLit( "NONE" ));
        }
        return( sb.toString() );
    }

    /**
     * How many system Strings are defined
     * @return number of key value pairs.
     */
    public int getSize()
    {
        return( sysStrings.size() );
    }
}
