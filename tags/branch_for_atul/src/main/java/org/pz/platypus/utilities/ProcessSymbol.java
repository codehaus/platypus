/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.utilities;

import org.pz.platypus.*;

/**
 * utility class for processing input symbols
 *
 * @author alb
 */
public class ProcessSymbol
{
    /**
     * Adds the symbol to the end of the token list. Symbols have the following Token layout:
     * @param source file# and line#
     * @param tl list of tokens to which new token will be added
     * @param root the command abbreviation
     * @return Status.OK if all went well; otherwise, error code.
     */
    public static int toToken( Source source, TokenList tl, String root  )
    {
        if( source == null || tl == null || root == null ) {
            return( Status.INVALID_PARAM_NULL );
        }
        
        Token t = new Token( source, TokenType.SYMBOL, root, root, null );
        tl.add( t );
        return( root.length() );
    }
}
