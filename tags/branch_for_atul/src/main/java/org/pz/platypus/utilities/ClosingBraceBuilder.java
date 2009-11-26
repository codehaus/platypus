/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.utilities;

/**
 * Builds a string that shows what the matching closing brace(s) should look be for
 * a given open set of braces.
 *
 * So, if the opening braces are '{', this class returns '}'
 * If the opening braces are '{{{', this class returns '}}}'
 *
 * @author alb
 */
public class ClosingBraceBuilder
{
    private String closingBrace = ""; // empty string = error occurred

    public ClosingBraceBuilder( final String text )
    {
        int braceCount;

        braceCount = countOpeningBraces( text );
        if( braceCount > 0 ) {
            closingBrace = makeClosingBrace( braceCount );
        }
    }

    /**
     * Counts the number of opening braces at the start of text
     * @param text the text that should begin with opening brace or braces ({)
     * @return number of opening braces, or 0 in case of error
     */
    private int countOpeningBraces( final String  text )
    {
        int braceCount = 0;   // 0 = error

        if( text != null && ! text.isEmpty() && text.charAt( 0 ) == '{' ) {
            for( int i = 0; i < text.length(); i++ ) {
                if( text.charAt( i ) == '{' )
                    braceCount++;
            }
        }
        return( braceCount );
    }

    /**
     * Generate a string containing as many closing braces as specified in braceCount
     * Note: the number of opening braces is guaranteed to be > 0
     * 
     * @param braceCount number of closing brace characters (}) in the returned string
     * @return string with brace counts
     */
    private String makeClosingBrace( final int braceCount )
    {
        StringBuilder closingBraces = new StringBuilder( braceCount );
        for( int i = 0; i < braceCount; i++ ) {
              closingBraces.append( '}' );
        }

        return( closingBraces.toString() );
    }

    public String getClosingBrace()
    {
        return( closingBrace );
    }
}
