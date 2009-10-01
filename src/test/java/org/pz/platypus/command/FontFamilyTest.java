/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.command;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.*;
import org.pz.platypus.test.mocks.MockLiterals;

/**
 * Test FontFamily commands that are loaded into the command table
 *
 * @author alb
 */
public class FontFamilyTest
{
    private FontFamily ff;
    private GDD gdd;

    @Before
    public void setUp()
    {
        ff = new FontFamily();
        gdd = new GDD();
        gdd.initialize();

        Literals lits = new MockLiterals();
        gdd.setLits( lits );

        CommandTable ct = new CommandTable( gdd );
//      Insert the following commands that are needed for this test
        ct.add( new CommandV( "[font|", 'n' ));
        ct.add( new CommandS( "[font|face:", 'n' ));
        ct.add( new CommandV( "[font|size:", 'n' ));
        ct.add( new CommandR( "[fsize:", "r [font|size:", ct ));
        gdd.setCommandTable( ct );
    }

    @Test
    public void validComplexCommand1()
    {
        String contentStr = "[font|size:12pt|face:Arial]";
        TokenList tl = new TokenList();

        int i = ff.process( gdd, new ParseContext( gdd, new Source( 16 ), contentStr + "\n", 0 ),
                           tl, false );

        assertEquals( 4, tl.size() );
        assertEquals( contentStr, tl.get( 0 ).getContent() );
    }
}
