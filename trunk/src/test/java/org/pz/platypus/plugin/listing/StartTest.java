/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.listing;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.*;
import org.pz.platypus.test.mocks.*;

import java.io.FileWriter;
import java.io.IOException;

/**
 * TestRoutines for ListingOut plug-in
 *
 * @author alb
 */
public class StartTest
{
    private GDD gdd;
    private Start start;

    @Before
    public void setUp()
    {
        start = new Start();
        gdd = new GDD();
        gdd.initialize();

        start.setLogger( new MockLogger() );
        gdd.setLits( new MockLiterals() );
    }

// TODO: rewrite so it does not depend on return values to determine correctness.
//    @Test
//    public void invalidProcessCalls()
//    {
//        String[] args = { "" };
//        assertEquals( Status.INVALID_PARAM_NULL,
//                                start.process( null, new CommandLineArgs( args )));
//        assertEquals( Status.INVALID_PARAM_NULL,
//                                start.process( gdd, null ));
//    }

    @Test
    public void testCloseOfUnopenedFile()
    {
        try {
            FileWriter f = new MockFileWriter();
            f.close();
            start.closeOuputFile( f , gdd );
        }
        catch( IOException ioe ) {
            assertTrue( 2 != 1 ); //force the pass if the exception is thrown
            return;
        }
       fail(
            "expected exception for closing a file in Start.closeOutputFile() did not occur" );
    }

    @Test
    public void testCloseOfNullFile()
    {
        try {
            start.closeOuputFile( null, gdd );
        }
        catch( IOException ioe ) {
            assertTrue( 2 != 1 ); //force the pass if the exception is thrown
            return;
        }
        fail( "did not throw exception on close of null file in Start.closeOuputFile()" );
    }

    @Test
    public void testCloseWithException()
    {
        MockFileWriter fw = new MockFileWriter();
        fw.setCloseShouldGenerateException( true );
        try {
            start.closeOuputFile( fw, gdd );
        }
        catch( IOException ioe ) {
            assertTrue( 2 != 1 );
            return;
        }

        fail( "did not throw expected exception in Start.closeOuputFile()" );
    }

    @Test
    public void testCloseOfValidFile()
    {
        MockFileWriter fw = new MockFileWriter();

        try {
            start.closeOuputFile( fw, gdd );
        }
        catch( IOException ioe ) {
            fail();
        }

        assertTrue( ! fw.getOpenStatus() );
    }

    @Test
    public void testConvertToHtmlTextNull()
    {
        assertEquals( "", HtmlListingStrategy.convertToHtmlText( null ));
    }

    @Test
    public void testConvertToHtmlTextEmptyString()
    {
        assertEquals( "", HtmlListingStrategy.convertToHtmlText( "" ));
    }

    @Test
    public void testConvertToHtmlTextValid()
    {
        assertEquals( "&lt;&gt;&quot; &amp;hello",
                      HtmlListingStrategy.convertToHtmlText( "<>\" &hello" ));
    }

    @Test
    public void testEmitHtmlHeaderValid()
    {
        MockFileWriter fw = new MockFileWriter();
        try {
            start.emitHtmlHeader( "inputFilename", fw, gdd );
        }
        catch( IOException ioe ) {
            fail();
        }

        final String header =
                "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http:" +
                "//www.w3.org/TR/html4/loose.dtd\">\n" +
                "<HTML>\n" +
                "<HEAD>\n" +
                "<!-- CREATED_BY_PLATYPUS VERSION  AVAILABLE_AT_PZ_ORG-->\n" +
                "<TITLE>\n" +
                "Playtpus File  inputFilename\n" +
                "</TITLE>\n" +
                "</HEAD>\n" +
                "<BODY>\n" +
                "<div style=\"background-color:#EEEEEE;font-size: 10.5pt;font-family: " +
                "Consolas, 'Courier New',Courier, monospace;font-weight: normal;\">\n" +
                "<ol>";

        assertEquals( header, fw.getText());
    }

    @Test
    public void testEmitHtmlHeaderException()
    {
        MockFileWriter fw = new MockFileWriter();
        fw.setWriteShouldGenerateException( true );

        try {
            start.emitHtmlHeader( "inputFile", fw, gdd );
        }
        catch( IOException ioe ) {
            assertTrue( 2 != 1 );
            return;
        }
        fail( "didn't throw expected exception on Start.emitHtmlHeader()" );
    }

    @Test
    public void testEmitClosingHtmlException()
    {
        MockFileWriter fw = new MockFileWriter();
        fw.setWriteShouldGenerateException( true );

        try {
            start.emitClosingHtml( fw, gdd );
        }
        catch( IOException ioe ) {
            assertTrue( 2 != 1 );
            return;
        }
        fail( "didn't throw expected exception on Start.emitClosingHtml()" );
    }

    @Test
    public void testEmitClosingHtmlValid()
    {
        MockFileWriter fw = new MockFileWriter();

        try{
            start.emitClosingHtml( fw, gdd );
        }
        catch( IOException ioe ) {
            fail( "threw exception when none expected in Start.emitClosingHtml()" );
        }

        assertEquals( "</div>\n</ol>\n</BODY>\n</HTML>\n",
                      fw.getText() );
    }

    @Test
    public void testOpenOutputFileInvalidName()
    {
        try{
            start.openOutputFile( "", gdd );
        }
        catch( IOException ioe ) {
         assertTrue( 2 != 1 );
            return;
        }
        fail( "didn't throw expected exception on Start.openOutputFile()" );
    }

    @Test
    public void testOutputCommandValid()
    {
        MockFileWriter fw = new MockFileWriter();
        final String leading15= "[leading:15pt]";

        final Token tok = new Token( new Source( 6 ), TokenType.COMMAND,
                                    "[leading:", leading15,  null );
        try {
            start.writeStringToFile( fw, gdd, tok.getContent() );
        }
        catch( IOException ioe ) {
            fail( "threw unexpected exception in Start.outputCommand()" );
        }
        assertEquals(
                "<span title=\"COMMAND\"><font color=\"blue\">[leading:15pt]</font></span>",
                fw.getText() );
    }

    @Test
    public void testOutputCommandException()
    {
        MockFileWriter fw = new MockFileWriter();
        fw.setWriteShouldGenerateException( true );

        final String leading15= "[leading:15pt]";

        final Token tok = new Token( new Source( 6 ), TokenType.COMMAND,
                                     "[leading:", leading15, null );
        try {
            start.writeStringToFile( fw, gdd, tok.getContent() );
        }
        catch( IOException ioe ) {
         assertTrue( 2 != 1 );
            return;
        }
        fail( "didn't throw expected exception on Start.outputCommand()" );
    }

    /**
     * test the output when the line number changes.
     */
    @Test
    public void testEmitListingValidLi1()
    {
        MockFileWriter fw = new MockFileWriter();

        final String leading15= "[leading:15pt]";

        final Token tok = new Token( new Source( 6 ), TokenType.COMMAND,
                                     "[leading:", leading15, null );

        TokenList tl = new TokenList();
        tl.add( tok );
        gdd.setInputTokens( tl );
        try {
            start.emitListing( fw, gdd );
        }
        catch( IOException ioe ) {
            fail( "threw unexpected exception in Start.emitListing()" );
        }

        assertTrue( fw.getText().startsWith( "<li>" ));
    }

    /**
     * Now, token line number = 0, so line should not start with <li>
     */
    @Test
    public void testEmitListingValidLi2()
    {
        MockFileWriter fw = new MockFileWriter();

        final String leading15= "[leading:15pt]";

        final Token tok = new Token( new Source( 0 ), TokenType.COMMAND,
                                     "[leading:", leading15, null );

        TokenList tl = new TokenList();
        tl.add( tok );
        gdd.setInputTokens( tl );
        try {
            start.emitListing( fw, gdd );
        }
        catch( IOException ioe ) {
            fail( "threw unexpected exception in Start.emitListing()" );
        }

        assertFalse( fw.getText().startsWith( "<li>" ));
    }

    /**
     * Test [cr], which generates an empty string, as [cr] is the soft CR/LF
     */
    @Test
    public void testEmitListingValidSoftCr()
    {
        MockFileWriter fw = new MockFileWriter();

        final String cr= "[cr]";

        final Token tok = new Token( new Source( 0 ), TokenType.COMMAND,
                                     "[cr]", cr, null );

        TokenList tl = new TokenList();
        tl.add( tok );
        gdd.setInputTokens( tl );
        try {
            start.emitListing( fw, gdd );
        }
        catch( IOException ioe ) {
            fail( "threw unexpected exception in Start.emitListing()" );
        }

        // this string is written at the end of every line, so it appears after the empty string
        // generated by the [cr] command
        assertEquals( "</li>\n", fw.getText() );
    }

//    @Test
//    public void testOutputNewLineException()
//    {
//        MockFileWriter fw = new MockFileWriter();
//        fw.setWriteShouldGenerateException( true );
//
//        try {
//            start.outputNewLine( fw, gdd );
//        }
//        catch( IOException ioe ) {
//         assertTrue( 2 != 1 );
//            return;
//        }
//        fail( "didn't throw expected exception on Start.outputNewLine()" );
//    }

    @Test
    public void testEmitListingValidHardCr()
    {
        MockFileWriter fw = new MockFileWriter();

        final String cr= "[]";

        final Token tok = new Token( new Source( 0 ), TokenType.COMMAND,
                                     "[]", cr, null );

        TokenList tl = new TokenList();
        tl.add( tok );
        gdd.setInputTokens( tl );
        try {
            start.emitListing( fw, gdd );
        }
        catch( IOException ioe ) {
            fail( "threw unexpected exception in Start.emitListing()" );
        }

        assertEquals(
                "<span title=\"NEW_PARAGRAPH\"><font color=\"blue\">[]</font></span><br></li>\n",
                fw.getText() );
    }
}
