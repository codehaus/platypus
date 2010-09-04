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
import org.pz.platypus.interfaces.IOutputContext;
import org.pz.platypus.plugin.common.DocData;
import org.pz.platypus.*;
import org.pz.platypus.test.mocks.MockLiterals;

import java.util.logging.Level;


/**
 * Test of abstract class for end of text in URLs with cover text
 *
 * @author alb
 */
public class UrlWithCoverTextEndTest
{
    class Concrete extends UrlWithCoverTextEnd  // class is abstract, so first get a concrete implementation.
    {
    }

    Concrete urlTextEnd;

    @Before
    public void setUp()
    {
        urlTextEnd = new Concrete();
    }

    @Test
    public void testConstructor()
    {
        assertEquals( "[-url]", urlTextEnd.getRoot() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProcessWithNullArg1()
    {
        urlTextEnd.process( null, new Token( new Source(), TokenType.TEXT, "allo!" ), 6 );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProcessWithNullArg2()
    {
        class DocummentData extends DocData {};
        IOutputContext oc = new DocummentData();
        urlTextEnd.process( oc, null, 6 );
    }

    @Test
    public void testValid()
    {
        final GDD gdd = new GDD();
        gdd.initialize();
        MockLiterals mockLits = new MockLiterals();
        gdd.setLits( mockLits );
        gdd.setupLogger(( "org.pz.platypus.Platypus" ));
        gdd.getLogger().setLevel( Level.OFF );

        class DocumentData extends DocData {
            public DocumentData( GDD gdd ){
                super( gdd );
            }
        };
         IOutputContext oc = new DocumentData( gdd );

        int i = urlTextEnd.process( oc, new Token( new Source(), TokenType.COMMAND, "[-url]", "[-url]", null ), 6 );

        assertEquals( 0, i );

        // was an error message emitted?
        assertEquals( "IGNORED", mockLits.getLastLit() );
    }
}