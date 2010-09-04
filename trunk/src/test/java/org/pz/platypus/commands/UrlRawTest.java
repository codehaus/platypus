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
 * Test of abstract class for raw URLs (that is, those with no cover text)
 *
 * @author alb
 */
public class UrlRawTest
{
    class Concrete extends UrlRaw  // UrlRaw is abstract, so first we need concrete implementation.
    {
        String urlOut = null;

        protected void outputUrl( final IOutputContext context, final String url )
        {
            urlOut = url;
        }

        protected String getUrlOut()
        {
            return( urlOut );
        }
    }

    Concrete rawUrl;

    @Before
    public void setUp()
    {
        rawUrl = new Concrete();
    }

    @Test
    public void testConstructor()
    {
        assertEquals( "[url:", rawUrl.getRoot() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProcessWithNullArg1()
    {
        rawUrl.process( null, new Token( new Source(), TokenType.TEXT, "allo!"), 6 );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProcessWithNullArg2()
    {
        class DocummentData extends DocData {};
        IOutputContext oc = new DocummentData();
        rawUrl.process( oc, null, 6 );
    }

    @Test
    public void testProcessWithNullUrl()
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

        Token tok = new Token( new Source(), TokenType.TEXT, "allo", "allo!", new CommandParameter() );
        rawUrl.process( oc, tok, 6 );

        // was an error message issued? 
        assertEquals( "IGNORED", mockLits.getLastLit() );
    }

    @Test
    public void testValid()
    {
        final String testUrl = "http://www.cnn.com";

        class DocummentData extends DocData {};
        IOutputContext oc = new DocummentData();
        CommandParameter param = new CommandParameter();
        param.setString( testUrl );
        Token tok = new Token( new Source(), TokenType.TEXT, "allo", "allo!", param );
        rawUrl.process( oc, tok, 6 );
        assertEquals( testUrl, rawUrl.getUrlOut() );
    }
}