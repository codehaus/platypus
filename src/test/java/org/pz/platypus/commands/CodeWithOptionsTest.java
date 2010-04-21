/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.commands;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.plugin.common.DocData;
import org.pz.platypus.Token;
import org.pz.platypus.Source;
import org.pz.platypus.TokenType;


/**
 * Test of abstract class for handling code listings
 *
 * @author alb
 */
public class CodeWithOptionsTest
{
    class Concrete extends CodeWithOptions{}

    Concrete listing;

    @Before
    public void setUp()
    {
        listing = new Concrete();
    }

    @Test
    public void testConstructor()
    {
        assertEquals( "[code|", listing.getRoot() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPreprocessWithNullArg1()
    {
        class DocummentData extends DocData {};
        DocummentData docData = new DocummentData();

        listing.preProcess( docData, null, 1 );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPreprocessWithNullArg2()
    {
        class DocummentData extends DocData {};
        DocummentData docData = new DocummentData();

        listing.preProcess( null, new Token( new Source(), TokenType.TEXT, " "), 1 );
    }

    

}
