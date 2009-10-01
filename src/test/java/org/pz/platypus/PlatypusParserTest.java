/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.test.mocks.MockLiterals;
import org.pz.platypus.test.mocks.MockLogger;
import org.pz.platypus.test.mocks.MockPropertyFile;
import org.pz.platypus.command.CommandV;
import org.pz.platypus.command.CommandS;
import org.pz.platypus.command.CommandR;

//import static org.hamcrest.CoreMatchers.*;
//import static org.junit.matchers.JUnitMatchers.*;

/**
 * Unit tests for Platypus parser
 *
 * @author alb
 */
public class PlatypusParserTest
{
    private PlatypusParser pp;
    private TokenList toks;
    private GDD gdd;
    private MockLiterals lits;

    @Before
    public void setUp()
    {
        gdd = new GDD();
        gdd.initialize();
        gdd.setLogger( new MockLogger() );
        lits = new MockLiterals();
        gdd.setLits( lits );

        CommandTable ct = new CommandTable( gdd );
//      Insert the following commands that are needed for testing the font family compound commands
//        [font|=vn
//        [font|face=sn
//        [font|size=vn
//        [fsize:=r [font|size:
        ct.add( new CommandV( "[font|:", 'n' ));
        ct.add( new CommandS( "[font|face:", 'n' ));
        ct.add( new CommandV( "[font|size:", 'n' ));
        ct.add( new CommandR( "[fsize:", "r [font|size:", ct ));
        gdd.setCommandTable( ct );        

        pp = new PlatypusParser( gdd );
        toks = new TokenList();
    }

    /**
     * When an invalid command is specified, if the command root cannot be located,
     * Platypus issues an error message and then assumes the root it can't find is
     * actually text. So, it returns the length of the root only.
     */
    @Test
    public void testProcessCommandWithInvalidCommand()
    {
        final String contentStr = "[fsuze:12pt]";
        final int lineNum = 16;
        final Source source = new Source( lineNum );
        final ParseContext context = new ParseContext( gdd, source, contentStr + "\n", 0 );

        int len = pp.processCommand( context, toks );

        assertEquals( "[fsuze:".length(), len );
        assertEquals( 1, toks.size() );  // the root is now emitted as a text token
        assertEquals( "[fsuze:", toks.get( 0 ).getContent() );
    }

    @Test
    public void testParseSegmentWithInvalidCompoundCommand()
    {
        String contentStr = "[fsuze:12pt]";
        final int lineNum = 16;
        final Source source = new Source( lineNum );
        final ParseContext context = new ParseContext( gdd, source, contentStr + "\n", 0 );
        int len = pp.parseSegment( context, toks );

        assertEquals( "[fsuze:".length(), len );
        assertEquals( 1, toks.size() );  // the root is now emitted as a text token
        assertEquals( "[fsuze:", toks.get( 0 ).getContent() );
    }

    @Test
    public void testProcessCommandWithValidCompoundCommand()
    {
        final String contentStr = "[fsize:12pt]";
        final Source source = new Source( 16 );
        final ParseContext context = new ParseContext( gdd, source, contentStr + "\n", 0 );

        int len = pp.processCommand( context, toks );

        assertEquals( contentStr.length(), len );
        assertEquals( 1, toks.size() );
    }

    @Test
    public void testParseSegmentWithValidCompoundCommand()
    {       
        String contentStr = "[fsize:12pt]";
        int r = pp.parseSegment(
                    new ParseContext( gdd, new Source( 16 ), contentStr + "\n", 0), toks );

        assertEquals( contentStr.length(), r );
        assertEquals( 1, toks.size() );
    }

    @Test
    public void parseWithNullParms()
    {
        assertEquals( Status.INVALID_PARAM_NULL,
                      pp.parse( null, toks,
                      new PropertyFile( null, null ), "a"  ));

        assertEquals( Status.INVALID_PARAM_NULL,
                      pp.parse( new LineList(), null,
                      new PropertyFile( null, null ), "a"  ));

        assertEquals( Status.INVALID_PARAM_NULL,
                      pp.parse( new LineList(), toks,
                      null, "a"  ));

        assertEquals( Status.INVALID_PARAM_NULL,
                      pp.parse( new LineList(), toks,
                      new PropertyFile( null, null ), null ));
    }

    @Test
    public void parseWithPlatypusNotParseOption()
    {
        MockPropertyFile mpf = new MockPropertyFile();
        mpf.lookupShouldReturnNo();

        LineList ll = new LineList();

        assertEquals( Status.OK,
                pp.parse( ll, toks, mpf, "prefix"));
    }

    @Test
    public void parseWithPlatypusDoesParseOption()
    {
        MockPropertyFile mpf = new MockPropertyFile();
        mpf.lookupShouldReturnYes();

        LineList ll = new LineList();
        ll.add( new InputLine( 2, "inputLine content\n" ));

        assertEquals( Status.OK,
                pp.parse( ll, toks, mpf, "prefix"));
    }

    @Test
    public void parseLineWithNull()
    {
        assertEquals( Status.INVALID_PARAM_NULL,
                      pp.parseLine( toks, null ));

        assertEquals( Status.INVALID_PARAM_NULL,
                      pp.parseLine( null, new InputLine() ));
    }

    @Test
    public void parseLineWithCRLF()
    {
        assertEquals( Status.OK,
                pp.parseLine( toks, new InputLine( 2, "\n" ) ));
    }

    @Test
    public void parseLineWithText()
    {
        assertEquals( Status.OK,
                pp.parseLine( toks, new InputLine( 3, "hello\n" ) ));
        assertEquals( 2, toks.size() );  // "hello" + [cr]
    }
    @Test
    public void noParseWithNullParams()
    {
        LineList ll = new LineList();
        TokenList tl = new TokenList();

        assertEquals( Status.INVALID_PARAM_NULL, new PlatypusParser( gdd ).noParse( null, null ));
        assertEquals( Status.INVALID_PARAM_NULL, new PlatypusParser( gdd ).noParse( ll, null ));
        assertEquals( Status.INVALID_PARAM_NULL, new PlatypusParser( gdd ).noParse( null, tl ));
    }

    @Test
     public void noParseWithValidParams()
     {
         LineList ll = new LineList();
         InputLine il = new InputLine( 2, "test text for line 2");
         ll.add( il );

         TokenList tl = new TokenList();

         assertEquals( Status.OK, new PlatypusParser( gdd ).noParse( ll, tl ));
         assertEquals( 1, tl.size() );
     }

    @Test
    public void doesPlatypusParseTestNo()
    {
        final MockPropertyFile pfile = new MockPropertyFile();

        pfile.lookupShouldReturnNo();
        assertFalse( pp.doesPlatypusParse( pfile, "test"));
    }

    @Test
    public void doesPlatypusParseTestYes()
    {
        final MockPropertyFile pfile = new MockPropertyFile();

        pfile.lookupShouldReturnYes();
        assertTrue( pp.doesPlatypusParse( pfile, "test"));
    }

    @Test
    public void doesPlatypusParseTestNull()
    {
        final MockPropertyFile pfile = new MockPropertyFile();

        pfile.lookupShouldReturnNull();
        assertTrue( pp.doesPlatypusParse( pfile, "test"));
    }

    @Test
    public void addTokenWithNullParams()
    {
        TokenList tl = new TokenList();

        assertEquals( 0, tl.size() );

        pp.addToken( null, TokenType.COMMAND, "[]", new Source( 6 ));
        assertEquals( 0, tl.size() );

        pp.addToken( tl, null, "[]", new Source( 6 ));
        assertEquals( 0, tl.size() );

        pp.addToken( tl, TokenType.COMMAND, null, new Source( 6 ));
        assertEquals( 0, tl.size() );

        pp.addToken( tl, TokenType.COMMAND, null, new Source( -6 ));
        assertEquals( 0, tl.size() );
    }

    @Test
    public void addTokenWithValidParams()
    {
        TokenList tl = new TokenList();

        assertEquals( 0, tl.size() );

        pp.addToken( tl, TokenType.COMMAND, "[]", new Source( 6 ));
        assertEquals( 1, tl.size() );
    }

    @Test
    public void writeOutTextNulls()
    {
        assertEquals( Status.INVALID_PARAM_NULL,
                pp.writeOutText( 0, 1, null, toks, new Source( 1 )));

        assertEquals( Status.INVALID_PARAM_NULL,
                pp.writeOutText( 0, 1, "text sample".toCharArray(), null, new Source( 1 )));
    }

    @Test
    public void writeOutTextInvalidIntegers()
    {
        // invalid start
        assertEquals( Status.INVALID_PARAM,
                pp.writeOutText( -1, 0, "sample".toCharArray(), toks, new Source( 1 )));

        // invalid end
        assertEquals( Status.INVALID_PARAM,
                pp.writeOutText( 0, -1, "sample".toCharArray(), toks, new Source( 1 )));

        // s/fail because start > end
        assertEquals( Status.INVALID_PARAM,
                pp.writeOutText( 2, 1, "sample".toCharArray(), toks, new Source( 1 )));
    }

    @Test
    public void writeOutTextValidParams()
    {
        final int toksStartSize = toks.size();

        assertEquals( Status.OK,
                pp.writeOutText( 0, 5, "sample text".toCharArray(), toks,new Source(  1 )));

        assertEquals( toksStartSize + 1, toks.size() );
        assertTrue( toks.get( 0 ).getContent().equals( "sample" ));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseContextWithNullContent()
    {
        new ParseContext( gdd, new Source( 1 ), null, 1 );
    }

    @Test
    public void parseSegmentWithNullTokens()
    {
        assertEquals( Status.INVALID_PARAM_NULL,
                pp.parseSegment( new ParseContext( gdd, new Source( 1 ), "sample\n", 1 ), null ));
    }

    @Test
    public void parseSegmentWithNullContext()
    {
        assertEquals( Status.INVALID_PARAM_NULL,
                pp.parseSegment( null, toks ));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseContextInvalidStartPosition()
    {
        new ParseContext( gdd, new Source( 1 ), "sample\n", -1 );
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseContextInvalidContent()
    {
        new ParseContext( gdd, new Source( 1 ), "sample", 1 );
    }

    @Test
    public void parseSegmentValidTextParams1()
    {
        final int toksStartSize = toks.size();
        final String text = "sample text\n";
        final int textLength = text.length();

        assertEquals( textLength - 1, // the -1 because \n is not parsed
                pp.parseSegment( new ParseContext( gdd, new Source( 1 ), text, 0 ), toks ));

        assertEquals( toksStartSize + 1, toks.size() );
        assertTrue( toks.get( 0 ).getContent().equals( "sample text" ));
    }

    /**
     * Tests that a string of text immediately followed by a command,
     * results in a token of the text preceding the command. (The command
     * itself is parsed later.)
     */
    @Test
    public void parseSegmentValidTextParams2()
    {
        final int toksStartSize = toks.size();
        final String text = "text[]\n";

        assertEquals( 4,
                pp.parseSegment(new ParseContext( gdd, new Source( 1 ), text, 0 ), toks ));

        assertEquals( toksStartSize + 1, toks.size() );
        assertTrue( toks.get( 0 ).getContent().equals( "text" ));
    }

    /**
     * Tests that an invalid command (here, incomplete) is treated as text.
     */
    @Test
    public void parseSegmentValidTextParams3()
    {
        final int toksStartSize = toks.size();
        final String text = "text[\n";

        assertEquals( 5,
                pp.parseSegment( new ParseContext( gdd, new Source( 1 ), text, 0 ), toks ));

        assertEquals( toksStartSize + 1, toks.size() );
        assertTrue( toks.get( 0 ).getContent().equals( "text[" ));
    }


//    Commented out because it depends on the loading of the command.properties file.
//    Need to find a way to do this to conduct complete testing.
//    /**
//     * Tests a valid command.
//     */
//    @Test
//    public void parseSegmentValidCommand1()
//    {
//        final int toksStartSize = toks.size();
//        final String text = "[fsize:12pt]\n";
//
//        assertEquals( text.length()-1, //-1 due to the \n char
//                pp.parseSegment( new ParseContext( gdd, new Source( 1 ), text, 0 ), toks ));
//    }

    /**
     * Tests to see that macros are properly expanded and that the
     * expanded text is injected correctly into the token stream
     */

// ********FOLLOWING TESTS HAVE TO BE STABILIZED WHEN WE FINISH HANDLING MACROS********
//    @Test
//    public void testParseLineValid()
//    {
//        lits.setVersionNumberToReturn( "0.2.0" );
//        lits.setGetLitShouldFindLookupString( false );
//        gdd.setLits( lits );
//        Platypus.storeVersionNumber( gdd );
//
//        InputLine il = new InputLine( 4, "at [_version] now\n");    // w/out \n should fail
//        TokenList tl = new TokenList();
//
//        PlatypusParser pp =  new PlatypusParser( gdd );
//        assertEquals( Status.OK, pp.parseLine( tl, il ));
//
//        assertEquals( 3, tl.size() );
//        assertTrue( "0.2.0 now".equals( tl.get(1).getContent() ));
//        assertEquals( TokenType.TEXT, tl.get(1).getType() );
//    }
//
//    @Test
//    public void testProcessMacroWithMacroProcessingSetToNo()
//    {
//        final String macro = "[$testmacro]";
//        final MockPropertyFile mpf = new MockPropertyFile();
//        mpf.lookupShouldReturnNo();
//        gdd.setConfigFile( mpf );
//        TokenList tl = new TokenList();
//
//        PlatypusParser pp =  new PlatypusParser( gdd );
//        // the +1 at the end is because pp slices off the opening [ before processing the macro
//        assertEquals( macro.length(), pp.processMacro( new ParseContext( gdd, new Source( 6 ), macro + "\n", 0 ), tl) +1 );
//
//        Token tok = tl.get( 0 );
//        assertEquals( TokenType.MACRO, tok.getType() );
//        assertTrue ( macro.equals( tok.getContent() ));
//    }

    @Test
    public void testeProcessBlockCommentValidOpeningAndClosing()
    {
        final String contentStr = "[%block macro%] text\n";
        int parsePoint = 0;
        Source source = new Source( 6 );

        int ret = pp.processBlockComment( new ParseContext( gdd, source, contentStr, parsePoint ), toks );

//        assertThat( ret, is( 15 ));
        assertEquals( ret, contentStr.length() - " text\n".length() );
        assertEquals( 1, toks.size() );

        Token tok1 = toks.get( 0 );
        Token tok2 = new Token( source, TokenType.BLOCK_COMMENT,
                        contentStr.substring( 0, contentStr.length() - " text\n".length() ));
        assertTrue( tok1.equals( tok2 ));
    }
}
