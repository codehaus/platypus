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
 * Test of abstract class for URLs with cover text
 *
 * @author alb
 */
public class UrlWithCoverTextTest
{
    class Concrete extends UrlWithCoverText  // UrlWithCoverText is abstract, so first get a concrete implementation.
    {
        String urlOut = null;
        String textOut = null;

        protected void outputUrl( final IOutputContext context, final String url, final String text )
        {
            urlOut = url;
            textOut = text;
        }

        protected String getUrlOut()
        {
            return( urlOut );
        }

        protected String getTextOut()
        {
            return( textOut );
        }
    }

    Concrete urlCumText;

    @Before
    public void setUp()
    {
        urlCumText = new Concrete();
    }

    @Test
    public void testConstructor()
    {
        assertEquals( "[+url:", urlCumText.getRoot() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProcessWithNullArg1()
    {
        urlCumText.process( null, new Token( new Source(), TokenType.TEXT, "allo!"), 6 );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProcessWithNullArg2()
    {
        class DocummentData extends DocData {};
        IOutputContext oc = new DocummentData();
        urlCumText.process( oc, null, 6 );
    }

    @Test
    public void testGetCoverTextWhenEndOfTextIsFirstToken()
    {
        final GDD gdd = new GDD();
        gdd.initialize();
        MockLiterals mockLits = new MockLiterals();
        gdd.setLits( mockLits );
        gdd.setupLogger(( "org.pz.platypus.Platypus" ));
        gdd.getLogger().setLevel( Level.OFF );
        TokenList tl = createTokenListWithEndOfTextOnly();
        gdd.setInputTokens( tl );

        class DocumentData extends DocData {
            public DocumentData( GDD gdd ){
                super( gdd );
            }
        };

        IOutputContext oc = new DocumentData( gdd );
        String coverTextFromCommand = urlCumText.getCoverText( oc, 0 );
        assertTrue( coverTextFromCommand.isEmpty() );
    }

    @Test
    public void testValidGetCoverText()
    {
        final GDD gdd = new GDD();
        gdd.initialize();
        MockLiterals mockLits = new MockLiterals();
        gdd.setLits( mockLits );
        gdd.setupLogger(( "org.pz.platypus.Platypus" ));
        gdd.getLogger().setLevel( Level.OFF );
        TokenList tl = createValidTokenList();
        gdd.setInputTokens( tl );

        class DocumentData extends DocData {
            public DocumentData( GDD gdd ){
                super( gdd );
            }
        };

        IOutputContext oc = new DocumentData( gdd );
        String coverTextFromCommand = urlCumText.getCoverText( oc, 0 );
        assertEquals( "CNN", coverTextFromCommand );
    }

   @Test
    public void testErrorWhenOtherCommandPrecedesEndOfTextCommand()
    {
        final GDD gdd = new GDD();
        gdd.initialize();
        MockLiterals mockLits = new MockLiterals();
        gdd.setLits( mockLits );
        gdd.setupLogger(( "org.pz.platypus.Platypus" ));
        gdd.getLogger().setLevel( Level.OFF );
        TokenList tl = createTokenListWithOtherCommandBeforeEndOfTextCommand();
        gdd.setInputTokens( tl );

        class DocumentData extends DocData {
            public DocumentData( GDD gdd ){
                super( gdd );
            }
        };

        IOutputContext oc = new DocumentData( gdd );
        urlCumText.process( oc, tl.get( 0 ), 0 );
        assertEquals( "CNN", urlCumText.getTextOut() );
        assertEquals( "www.cnn.com", urlCumText.getUrlOut() );
        assertEquals( "ERROR.URL_COVER_TEXT_NOT_PROPERLY_ENDED", mockLits.getLastLit() );
    }

    @Test
    public void testValidProcess()
    {
        final GDD gdd = new GDD();
        gdd.initialize();
        MockLiterals mockLits = new MockLiterals();
        gdd.setLits( mockLits );
        gdd.setupLogger(( "org.pz.platypus.Platypus" ));
        gdd.getLogger().setLevel( Level.OFF );
        TokenList tl = createValidTokenList();
        gdd.setInputTokens( tl );

        class DocumentData extends DocData {
            public DocumentData( GDD gdd ){
                super( gdd );
            }
        };

        IOutputContext oc = new DocumentData( gdd );
        urlCumText.process( oc, tl.get( 0 ), 0 );
        assertEquals( "CNN", urlCumText.getTextOut() );
        assertEquals( "www.cnn.com", urlCumText.getUrlOut() );
    }

    @Test
    public void testValidProcessWithMacroInLieuOfText()
    {
        final GDD gdd = new GDD();
        gdd.initialize();
        MockLiterals mockLits = new MockLiterals();
        gdd.setLits( mockLits );
        gdd.setupLogger(( "org.pz.platypus.Platypus" ));
        gdd.getLogger().setLevel( Level.OFF );

        UserStrings us = gdd.getUserStrings();
        us.add( "Winston", "Churchill" );

        TokenList tl = createValidTokenListWithMacro();
        gdd.setInputTokens( tl );

        class DocumentData extends DocData {
            public DocumentData( GDD gdd ){
                super( gdd );
            }
        };

        IOutputContext oc = new DocumentData( gdd );
        urlCumText.process( oc, tl.get( 0 ), 0 );
        assertEquals( "Churchill", urlCumText.getTextOut() );
        assertEquals( "www.cnn.com", urlCumText.getUrlOut() );
    }

    //=== support functions ===//
    
    // creates a token list where the second token is [-url], which closes the URL
    // the first token is skipped by the token list processor (as it's assumed to be [+url:... )
    private TokenList createTokenListWithEndOfTextOnly()
    {
        TokenList tl = new TokenList();
        Source src = new Source();
        String URL = "www.cnn.com";
        String command = "[url:" + URL + "]";

        Token skippedToken = new Token( src, TokenType.COMMAND, command, command, null );
        Token eoTextToken = new Token( src, TokenType.COMMAND, "[-url]", "[-url]", null );
        tl.add( skippedToken );
        tl.add( eoTextToken );

        return( tl );
    }

    private TokenList createTokenListWithOtherCommandBeforeEndOfTextCommand()
    {
        TokenList tl = new TokenList();
        Source src = new Source();
        String URL = "www.cnn.com";
        String command = "[url:" + URL + "]";
        CommandParameter param = new CommandParameter();
        param.setString( URL );

        Token skippedToken = new Token( src, TokenType.COMMAND, command, command, param );
        Token textToken = new Token( src, TokenType.TEXT, "CNN" );
        Token otherCommandToken = new Token( src, TokenType.COMMAND, "[+b]", "[+b]", null );
        tl.add( skippedToken );
        tl.add( textToken );
        tl.add( otherCommandToken );

        return( tl );
    }

    // creates a valid list with the skipped start of URL command, some text, and the close of URL command.
    private TokenList createValidTokenList()
    {
        TokenList tl = new TokenList();
        Source src = new Source();
        String URL = "www.cnn.com";
        String command = "[url:" + URL + "]";
        CommandParameter param = new CommandParameter();
        param.setString( URL );

        Token skippedToken = new Token( src, TokenType.COMMAND, command, command, param );
        Token textToken = new Token( src, TokenType.TEXT, "CNN" );
        Token eoTextToken = new Token( src, TokenType.COMMAND, "[-url]", "[-url]", null );
        tl.add( skippedToken );
        tl.add( textToken );
        tl.add( eoTextToken );

        return( tl );
    }

    // creates a valid list with the skipped start of URL command, a valid text macro, and
    // the close of URL command.
    private TokenList createValidTokenListWithMacro()
    {
        TokenList tl = new TokenList();
        Source src = new Source();
        String URL = "www.cnn.com";
        String command = "[url:" + URL + "]";
        CommandParameter param = new CommandParameter();
        param.setString( URL );

        String macro = "[$Winston]";

        Token skippedToken = new Token( src, TokenType.COMMAND, command, command, param );

        CommandParameter param2 = new CommandParameter();
        param2.setString("Winston" );

        Token macroToken = new Token( src, TokenType.MACRO, macro, macro, param2 );
        Token eoTextToken = new Token( src, TokenType.COMMAND, "[-url]", "[-url]", null );
        tl.add( skippedToken );
        tl.add( macroToken );
        tl.add( eoTextToken );

        return( tl );
    }
}