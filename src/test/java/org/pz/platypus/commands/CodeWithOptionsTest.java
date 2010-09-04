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
import org.pz.platypus.plugin.common.DocData;
import org.pz.platypus.*;
import org.pz.platypus.test.mocks.MockLiterals;

import java.util.logging.Level;


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

        listing.preProcess( null, new Token( new Source(), TokenType.TEXT, " "), 1 );
    }

    @Test
    public void testPreprocessWithNullTokenParam()
    {
        final GDD gdd = new GDD();
        gdd.initialize();
        gdd.setLits( new MockLiterals() );
        gdd.setupLogger( ( "org.pz.platypus.Platypus" ));
        gdd.getLogger().setLevel( Level.OFF );

        class DocumentData extends DocData
        {
            public DocumentData() { super( gdd ); }
        };

        DocumentData docData = new DocumentData();

        CommandParameter cp = new CommandParameter();
        cp.setString( null );

        assertEquals( 0, listing.preProcess( docData, new Token( new Source(), TokenType.TEXT, "", "", cp ), 1 ));
    }

    @Test
    public void testPreprocessWhenTokenParamNotLines()
    {
        final GDD gdd = new GDD();
        gdd.initialize();
        gdd.setLits( new MockLiterals() );
        gdd.setupLogger( ( "org.pz.platypus.Platypus" ));
        gdd.getLogger().setLevel( Level.OFF );

        CommandParameter cp = new CommandParameter();
        cp.setString( "lanes:1,1" );

        class DocumentData extends DocData
        {
            public DocumentData() { super( gdd ); }
        };

        DocumentData docData = new DocumentData();

        assertEquals( 0, listing.preProcess( docData, new Token( new Source(), TokenType.TEXT, "", "", cp ), 1 ));
    }

    @Test
    public void testPreprocessWhenInCodeSection()
    {
        final GDD gdd = new GDD();
        gdd.initialize();

        CommandParameter cp = new CommandParameter();
        cp.setString( "lines:1,1" );

        class DocumentData extends DocData
        {
            public DocumentData() { super( gdd ); }
        };

        DocumentData docData = new DocumentData();
        docData.setInCodeSection( true, new Source() );

        assertEquals( 0, listing.preProcess( docData, new Token( new Source(), TokenType.TEXT, "", "", cp ), 1 ));
    }

    @Test
    public void testPreprocessInvalidLineParam()
    {
        final GDD gdd = new GDD();
        gdd.initialize();
        gdd.setLits( new MockLiterals() );
        gdd.setupLogger( ( "org.pz.platypus.Platypus" ));
        gdd.getLogger().setLevel( Level.OFF );

        CommandParameter cp = new CommandParameter();
        cp.setString( "lines:1" );

        class DocumentData extends DocData
        {
            public DocumentData() { super( gdd ); }
        };

        DocumentData docData = new DocumentData();

        assertEquals( 0, listing.preProcess( docData, new Token( new Source(), TokenType.TEXT, "", "", cp ), 1 ));
    }

    @Test
    public void testPreprocessValid1_1()
    {
        final GDD gdd = new GDD();
        gdd.initialize();
        gdd.setLits( new MockLiterals() );
        gdd.setupLogger( ( "org.pz.platypus.Platypus" ));
        gdd.getLogger().setLevel( Level.OFF );

        CommandParameter cp = new CommandParameter();
        cp.setString( "lines:1,1" );

        class DocumentData extends DocData
        {
            public DocumentData() { super( gdd ); }
        };

        DocumentData docData = new DocumentData();

        assertEquals( 1, listing.preProcess( docData, new Token( new Source(), TokenType.TEXT, "", "", cp ), 1 ));
        assertEquals( 0, docData.getLineNumberLast() );
        assertEquals( 1, docData.getLineNumberSkip() );
    }

   @Test
    public void testPreprocessValid0_5()
    {
        final GDD gdd = new GDD();
        gdd.initialize();
        gdd.setLits( new MockLiterals() );
        gdd.setupLogger( ( "org.pz.platypus.Platypus" ));
        gdd.getLogger().setLevel( Level.OFF );

        CommandParameter cp = new CommandParameter();
        cp.setString( "lines:0,5" );

        class DocumentData extends DocData
        {
            public DocumentData() { super( gdd ); }
        };

        DocumentData docData = new DocumentData();
        docData.setLineNumberLast( 24, new Source() );

        assertEquals( 1, listing.preProcess( docData, new Token( new Source(), TokenType.TEXT, "", "", cp ), 1 ));
        assertEquals( 24, docData.getLineNumberLast() );
        assertEquals( 5, docData.getLineNumberSkip() );
    }

    @Test
    public void testInvalidStartingLineNumbers()
    {
        final GDD gdd = new GDD();
        gdd.initialize();
        gdd.setLits( new MockLiterals() );
        gdd.setupLogger( ( "org.pz.platypus.Platypus" ));
        gdd.getLogger().setLevel( Level.OFF );

        Source src = new Source();

        assertEquals( 1, listing.parseStartingLineNumber( "-26", gdd, src ));
        assertEquals( 1, listing.parseStartingLineNumber( "1111111", gdd, src ));
        assertEquals( 1, listing.parseStartingLineNumber( "xx", gdd, src ));
    }

    @Test
    public void testValidStartingLineNumbers()
    {
        final GDD gdd = new GDD();
        gdd.initialize();
        gdd.setLits( new MockLiterals() );
        gdd.setupLogger( ( "org.pz.platypus.Platypus" ));
        gdd.getLogger().setLevel( Level.OFF );

        Source src = new Source();

        assertEquals( 0, listing.parseStartingLineNumber( "0", gdd, src ));
        assertEquals( 1, listing.parseStartingLineNumber( "1", gdd, src ));
        assertEquals( 23, listing.parseStartingLineNumber( "23", gdd, src ));
    }

    @Test
    public void testInvalidSkipLineNumbers()
    {
        final GDD gdd = new GDD();
        gdd.initialize();
        gdd.setLits( new MockLiterals() );
        gdd.setupLogger( ( "org.pz.platypus.Platypus" ));
        gdd.getLogger().setLevel( Level.OFF );

        Source src = new Source();

        assertEquals( 1, listing.parseSkipLineNumber( "-26", gdd, src ));
        assertEquals( 1, listing.parseSkipLineNumber( "0", gdd, src ));
        assertEquals( 1, listing.parseSkipLineNumber( "111", gdd, src ));
        assertEquals( 1, listing.parseSkipLineNumber( "xx", gdd, src ));
    }

    @Test
    public void testValidSkipLineNumbers()
    {
        final GDD gdd = new GDD();
        gdd.initialize();
        gdd.setLits( new MockLiterals() );
        gdd.setupLogger( ( "org.pz.platypus.Platypus" ));
        gdd.getLogger().setLevel( Level.OFF );

        Source src = new Source();

        assertEquals( 1, listing.parseSkipLineNumber( "1", gdd, src ));
        assertEquals( 23, listing.parseSkipLineNumber( "23", gdd, src ));
    }
}
