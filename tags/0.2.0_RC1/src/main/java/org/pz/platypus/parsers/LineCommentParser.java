/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.parsers;

/**
 * Parser that recognizes a line comment. Note: sole method is static.
 *
 * @author alb
 */
public class LineCommentParser
{
    public LineCommentParser() {}

    /**
     * Static method that determines whether a line is a line comment; that is, one whose first
     * non-whitespace characters are %%. If it find this character combo, we must make sure it's
     * not the closing symbol for a block comment (which can, in fact, start with multiple %%
     * characters). If it is the close to a block comment, then it's not a line comment.
     *
     * @param line line to isLineComment for line comment
     * @param closingBlockCommentSymbol null if we're not in a block comment; the symbol, if we are.
     * @return true if this is a line comment, false if it's not
     * @throws IllegalArgumentException if passed a null line
     */
    public static boolean isLineComment( final String line, final String closingBlockCommentSymbol )
           throws IllegalArgumentException
    {
        /** line comments begin with this symbol, as the first non-whitespace characters */
        final String lineCommentSymbol = "%%";

        if ( line == null ) {
            throw new IllegalArgumentException();
        }
       
        String trimmedLine = line.trim();
        if ( ! trimmedLine.startsWith( lineCommentSymbol )) {
            return( false );
        }

        // if we're here it's because we found a match. Now, is it actually the ending
        // marker for a block comment? If so, then this is not a line comment. First,
        // are we in a block comment. The closingBlockCommentSymbol != null, if we are.
        if ( closingBlockCommentSymbol == null ) {
            return( true );
        }

        if ( trimmedLine.startsWith( closingBlockCommentSymbol )) {
            return( false );
        }
        else {
            return( true );
        }
    }
}
