/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.utilities;

import org.junit.Test;
import static org.junit.Assert.*;
import org.pz.platypus.*;

/**
 * Test symbol addition to token list in ProcessSymbol.
 *
 * @author alb
 */
public class ProcessSymbolTest
{
    // note ProcessSymbol exists only for its single static method, toToken.

    @Test
    public void testConstructor()
    {
        ProcessSymbol ps = new ProcessSymbol();
        assertNotNull( ps );
    }

    @Test
    public void testNullToToken()
    {
        TokenList tl = new TokenList();
        assertEquals( 0, tl.size() );

        assertEquals( Status.INVALID_PARAM_NULL, ProcessSymbol.toToken( null, tl , "[E^]" ));
        assertEquals( Status.INVALID_PARAM_NULL, ProcessSymbol.toToken( new Source(), null, "[E^]" ));
        assertEquals( Status.INVALID_PARAM_NULL, ProcessSymbol.toToken( new Source(), tl, null ));
        assertEquals( 0, tl.size() );
    }

    @Test
    public void testValidToToken()
    {
        String symbol = "[E^]";

        TokenList tl = new TokenList();
        assertEquals( 0, tl.size() );

        assertEquals( symbol.length(),
                       ProcessSymbol.toToken( new Source(), tl , "[E^]" ));
        assertEquals( 1, tl.size());
    }

    @Test
    public void testValidResultingToken()
    {
        String symbol = "[E^]";

        TokenList tl = new TokenList();
        assertEquals( 0, tl.size() );

        assertEquals( symbol.length(),
                       ProcessSymbol.toToken( new Source(), tl , "[E^]" ));
        assertEquals( 1, tl.size());

        Token tok = tl.get( 0 );
        assertEquals( TokenType.SYMBOL, tok.getType() );
        assertEquals( symbol, tok.getRoot() );
    }
}