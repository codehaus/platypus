/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import java.util.ArrayList;

/**
 * The ArrayList of Tokens that we generate in the parser and pass
 * to the output plug-in.
 *
 * @author alb
 */
public class TokenList extends ArrayList<Token>
{
    public TokenList() {};


    /**
     * Gets the next token after the token pointed to by the tokenNumber
     * 
     * @param tokNumber the number of the current token
     * @return the token after the current token. Null, if there's an error or the current token
     *         is the last token
     */
    public Token getNextToken( int tokNumber )
    {
        if( tokNumber < 0 ) {
            return( null );
        }

        Token nextTok;
        try {
            nextTok = get( tokNumber + 1 );
        }
        catch( IndexOutOfBoundsException ioe ) {
            return( null);
        }

        return( nextTok );
    }

    /**
     * Returns the token previous to the one whose number is passed in
     *
     * @param tokNum the number of the current token
     * @return the token priort to the current token, or null if an error occurred.
     */
    public Token getPrevToken( int tokNum )
    {
        if( tokNum <= 0 ) {
            return( null );
        }

        return( get( tokNum - 1 ));
    }

    /**
     * Gets all the preceding tokens in this input line and sees whether any
     * of them contain text.
     *
     * @param tokNum the present token
     * @return true if text is emitted false if not
     */
    public boolean lineSoFarEmitsText( int tokNum )
    {
        assert( tokNum >= 0 && tokNum < this.size() );

        int i;

        Token currTok = (Token) get( tokNum );
        Token newTok = null;

        for( i = tokNum; i >= 0; i-- )
        {
            newTok = (Token) get( i );
            if( ! currTok.sourceEquals( newTok )) {
                break;
            }
        }

        // if i is not a valid 0 that caused the end of the for loop, increase it by 1
        if( ! ( i == 0 && ( newTok != null && currTok.sourceEquals( newTok )))) {
            i++;
        }

        if( i == tokNum ) {
            return( false );
        }

        for( int j = i; j < tokNum; j++ )
        {
            Token tok = get( j );
            if( tok.getType() == TokenType.TEXT ) {
                return( true );
            }
        }

        return( false );
    }
    /**
     * dumps all the tokens in the output token list to System.out
     *
     * @param gdd the GDD
     */
    public void dump( final GDD gdd )
    {
        Token t;

        final Object[] toks = this.toArray();
        for( int i = 0; i < toks.length; i++ )
        {
            t = (Token) toks[i];
            System.out.println( t.toString( gdd ));
        }
    }
}

