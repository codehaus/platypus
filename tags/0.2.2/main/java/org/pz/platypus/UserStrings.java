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
 * Manages the user-defined strings  (essentially macros w/out parameters, currently)
 * System strings have the format of starting with: $ followed by an alphanumeric and
 * then alphanumerics or the _ character
 * They are defined with [def:$macro{meaning}]. They are printed by the command operator *,
 * as in: [*$macro]
 *
 * @author alb (Andrew Binstock)
 */
public class UserStrings
{
    @SuppressWarnings("unchecked")  // don't issue spurious unchecked warnings on tree accesses
                                    // works only with Java 5 compiler and later releases
    private final TreeMap userStrings;

    public UserStrings()
    {
        userStrings = new TreeMap<String, String>();
    }

    /**
     * Add a string to user strings after validating they don't start with $_.
     * @param key string that should not start with $_
     * @param value where any legal string is OK except null
     * @return  Status.OK; on error: Status.INVALID_PARAM or Status.INVALID_PARAM_NULL
     */
    public int add( final String key, final String value )
    {
        if ( key == null || value == null ) {
            return( Status.INVALID_PARAM_NULL );
        }

        if( key.length() < 2 ) {
            return( Status.INVALID_PARAM );    
        }

//        if ( ! ( key.startsWith( "$" ) && Character.isLetterOrDigit( key.codePointAt( 1 ) ))) {
        if ( ! Character.isLetterOrDigit( key.codePointAt( 0 ) )) {
            return( Status.INVALID_PARAM );
        }

        try {
            userStrings.put( key, value );
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

        sb.append( lits.getLit( "USER_DEFINED_STRINGS" ))
          .append( ": ")
          .append( '\n' );

        Set set = userStrings.entrySet();
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
        return( userStrings.size() );
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

        Object retObject = (String) userStrings.get( key );
        if ( retObject != null ) {
            return( retObject.toString() );
        }
        else {
            return( null );
        }
    }
}