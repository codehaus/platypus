/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.utilities;

/**
 * Miscellaneous routines for manipulating text in various ways
 *
 * @author alb
 */
public class TextTransforms
{
    /**
     * converts a char[] to a string. Note the toString() method of a char[] does not do this.
     * @param chars the char array
     * @return the equivalent string, or null if an error occurred
     */
    public static String charArrayToString( final char[] chars )
    {
        if( chars == null ) {
            return( null );
        }

        if( chars.length == 0 ) {
            return "";
        }

        return( charArrayToString( chars, 0, chars.length - 1 ));
    }

    /**
     * converts part (or all) of a char[] to a string.
     * Note: the toString() method os a char[] does not do this.
     * @param chars the char array
     * @param start starting point
     * @param end ending point
     * @return the string for the section of chars; or null, if an error occurred
     */
    public static String charArrayToString( final char[] chars, final int start, final int end )
    {
        if( chars == null || start < 0 || end < start || end > chars.length - 1 ) {
            return( null );
        }

       if( chars.length == 0 ) {
            return( "" );
        }

        final StringBuffer text = new StringBuffer( end - start + 1 );
        for( int i = start; i <= end; i++ ) {
            text.append( chars[i] );
        }
        return( text.toString() );
    }


    /**
     * Replace a substring at a specific location in a String with a new substring. Note that
     * the location is needed because the base string could include multiple instances of the
     * substring to be replaced.
     *
     * @param baseString the basic string in which the substitutions are made
     * @param oldSubstring the substring to be replaced
     * @param newSubstring the replacement substring
     * @param startPoint location of the substing
     * @return the new string with the substitution completed
     */
    public static String replaceSubstringAtLocation( String baseString,
                                            final String oldSubstring,
                                            final String newSubstring,
                                            int startPoint )
    {
        assert( baseString != null );
        assert( oldSubstring != null && newSubstring != null );
        assert( startPoint >= 0 );

        StringBuilder sb = new StringBuilder( baseString.length() + newSubstring.length() );
        if( startPoint > 0 ) {
            sb.append( baseString.substring( 0, startPoint ));
        }

        sb.append( newSubstring );
        sb.append( baseString.substring( startPoint + oldSubstring.length() ));

        return( sb.toString() );
    }
}
