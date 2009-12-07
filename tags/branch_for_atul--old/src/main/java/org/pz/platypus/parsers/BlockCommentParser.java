/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.parsers;

/**
 * Handles identification of block comments (and does some limited processing)
 *
 * @author alb
 */
public class BlockCommentParser
{
    /**
     * Computes the closing marker for a block comment based on the opening marker.
     * This is needed because block comments can nest, so their opening and closing
     * markers necessarily can be variable length. Specifically, the basic open/close
     * marker pair are: [% and %]. But the number of % can be incremented to any amount,
     * so long as the closing marker matches. So, [%%%% and %%%%] are a legal pair.
     *
     * @param input the text containing the opening marker
     * @param startPoint where the opening marker begins
     * @return a string containing the closing marker, or null if an error occurred.
     */
    public String computeClosingMarker( char[] input, int startPoint )
    {
        if( input == null || startPoint < 0 ) {
            return( null );
        }

        int percentChars = 0;
        while( input[++startPoint] == '%' ) {
            percentChars++;
        }

        StringBuilder closingCommentSymbol = new StringBuilder( percentChars + 1 );
        for( int i = 0; i < percentChars; i++ )
        {
            closingCommentSymbol.append( '%' );
        }
        closingCommentSymbol.append( ']' );
        return( closingCommentSymbol.toString() );
    }
}
