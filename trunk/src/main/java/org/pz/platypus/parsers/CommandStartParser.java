/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.parsers;

/**
 * Determines whether a sequence containing a [ is the beginning of a command.
 * This is a pure lexical analysis. This parser has no awareness of any state,
 * such as in-comment, in-code, or any such thing.
 *
 * Note: sole method is static
 *
 * @author alb
 */
public class CommandStartParser
{
    public CommandStartParser() {};

    /**
     * Static method that determines lexically whether a sequence of characters containing 
     * an embedded [ is a command.
     *
     * It's a command if:
     * 1) the [ is not preceded by a /
     * 2) the [ is not followed by a whitespace or control character
     * 3) the [ is not followed by a digit
     *
     * Anything else, returns true. It might subsequently turn out that the chars are
     * in fact not a valid command, but this will be treated as an error by the parser
     * and then passed through to the final document as text.
     *
     * @param content  char array containing text to analyze
     * @param parsePoint where we begin the analysis
     * @return true if a command; false, if not
     */
    public static boolean isItACommand( char[] content, final int parsePoint )
    {
        if ( content == null || parsePoint < 0 ) {
            return( false );
        }
        
        int lastCharAt = content.length - 1;  // line ends with CR, so this is char before

        // an [ as the last character in a line cannot be the start of a command
        if ( parsePoint == lastCharAt ) {
            return( false );
        }

        // Platypus commands never begin with a [ immediately followed by a digit,
        // whitespace, or a control char. So any such occurrence is not a command.
        char c = content[parsePoint + 1];
        if ( Character.isWhitespace( c ) || Character.isDigit( c ) || Character.isISOControl( c )) {
            return( false );
        }

        // Was the [ preceded by the escape character '/' ?
        if ( isItEscapedCommandStart( content, parsePoint )) {
                return( false );
        }
        return( true );
    }

    /**
     * If the [ command is preceded by a /, then it's not a command. This routine
     * determines whether that sequence of chars exists
     * @param content the text being examined
     * @param parsePoint the point at which the [ occurs
     * @return true if it is escaped, false if not
     */
    public static boolean isItEscapedCommandStart( final char[] content, final int parsePoint )
    {
        if ( content[parsePoint] == '[' &&
                parsePoint > 0 &&
                content[parsePoint-1] == '/' ) {
                return( true );
        }
        return( false );
    }
}
