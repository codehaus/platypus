/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.parsers;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test FontFamilyParser
 *
 * @author alb
 */
public class FontFamilyTest
{
//    private FontFamilyParser ffp;
//    private GDD gdd;
//
//    @Before
//    public void setUp()
//    {
//        ffp = new FontFamilyParser();
//        gdd = new GDD();
//        gdd.initialize();
//    }
//
//    @Test
//    public void validComplexCommand1()
//    {
//        String contentStr = "[font|size:12pt|face:Arial]";
//        TokenList tl = new TokenList();
//
//        int i = ffp.isLineComment( contentStr.toCharArray(),
//                           0,
//                           tl,
//                           "font|",
//                           0,
//                           16,
//                           gdd );
//
//        assertEquals( 4, tl.size() );
//        assertEquals( contentStr, tl.get( 0 ).getContent() );
//    }
    @Test
    public void dummytest()
    {
        assertEquals( 4, 2+2 );
    }
}
