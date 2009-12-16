/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.listing;

import org.pz.platypus.*;
import org.pz.platypus.interfaces.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Simple, illustrative plugin for creating Platypus listing files in HTML.
 * Note: Methods order: constructor, methods in alphabetical order, getters and setters
 *
 * Error-handling convention:
 *      Output plugins handle all their own error messages. Platypus is made aware of a problem
 *      only by the value returned from calling process(). However, it does not issue any user
 *      messages as a result of this.
 *
 * @author alb
 */
public class Start implements Pluggable
{
    private Logger logger;

    /**
     * Start() is always called first by Platypus, followed by a call to process()
     * Any plugin initialization code should go in here.
     */
    public Start()
    {
    }

    /**
     * Close the file
     * @param outputFile file to close
     * @param gdd the GDD. Only the literals are used
     * @throws IOException in case of error
     */
    public void closeOuputFile( FileWriter outputFile, final GDD gdd ) throws IOException
    {
        if ( outputFile == null ) {
            logger.severe( gdd.getLit( "ERROR.CLOSING_NULL_OUTPUT_FILE" ));
            throw new IOException();
        }

        try {
            outputFile.close();
        }
        catch( IOException ioe ) {
            logger.severe( gdd.getLit( "ERROR.CLOSING_OUTPUT_FILE" ));
            throw new IOException();
        }
    }

    /** TODO: This is used only in unit tests - refactor unit tests and remove this.
     *  This is moved to the Strategy base class.
     * replaces reserved HTML characters to make text printable
     * @param text text to transform
     * @return text string with the transformation applied
     */
    public String convertToHtmlText( final String text )
    {
        char c;

        if( text == null || text.isEmpty() ) {
            return( "" );
        }

        StringBuilder sb = new StringBuilder( 2 * text.length() );

        for( int i = 0; i < text.length(); i++ )
        {
            switch( c = text.charAt( i ))
            {
                case '<':   sb.append( "&lt;" );    break;
                case '>':   sb.append( "&gt;" );    break;
                case '(':   sb.append( "&#40;" );   break;
                case ')':   sb.append( "&#41;" );   break;
                case '"':   sb.append( "&quot;" );  break;
                case '\'':  sb.append( "&#39;" );   break;
                case '&':   sb.append( "&amp;" );   break;
                case '#':   sb.append( "&#35;" );   break;
                default:    sb.append( c );         break;
            }
        }
        return( sb.toString() );
    }

    /**
     * Emits the HTML closing code
     * @param outputFile the HTML is written to this file
     * @param gdd GDD, only the literals are used in this method
     * @throws IOException in the rare event the file cannot be written to
     */
    public void emitClosingHtml( final FileWriter outputFile, final GDD gdd ) throws IOException
    {
        final String s = "</div>\n" + "</ol>\n" + "</BODY>\n" + "</HTML>\n";
        outputIt( outputFile, gdd, s);
    }

    /**
     * Emit the HTML needed at the beginning of the listing file
     *
     * @param inputFile filename of the file being converted into a listing
     * @param outputFile the file to write the HTML to
     * @param gdd the GDD. Only the literals are used in this method
     * @throws IOException in the rare event the file cannot be written to
     */
    public void emitHtmlHeader( final String inputFile,
                                final FileWriter outputFile,
                                final GDD gdd ) throws IOException
    {
        final String s = getHeaderHtml( gdd, inputFile );
        outputIt( outputFile, gdd, s );
    }

    /**
     * Where the content of the listing file is written out
     *
     * @param outfile the file being written to
     * @param gdd the GDD. Only the literals are used
     * @throws IOException in the event the file can't be written to
     */
    public void emitListing( final FileWriter outfile, final GDD gdd )
           throws IOException
    {

        Token tok;
        int lineNumber = 0;

        final TokenList tokensList = gdd.getInputTokens();
        for( int i = 0; i < tokensList.size(); i++ ) {
            tok = tokensList.get( i );

            if(isItANewHtmlLine(tok, lineNumber))
                lineNumber = startANewHtmlLine(outfile, gdd, tok);

            if (skipThisToken(tok, tokensList, i))
                continue;

            i += skipNextTokens(i, tok, gdd, tokensList);

            printToken(outfile, gdd, tok);
        }
    }

    private void printToken(FileWriter outfile, GDD gdd, Token tok) throws IOException {
        HtmlListingStrategy strategy = HtmlListingStrategy.getFormatStrategy( tok );
        final String s = strategy.format(tok, gdd);
        outputIt(outfile, gdd, s);
        if (strategy.canOutputHtmlEndOfLine())
            outfile.write( "</li>\n" );
    }

    /**
     * Start a new Html line. This amounts to outputting an Html <li> token.
     * @param outfile
     * @param gdd
     * @param tok
     * @return the new / next line number.
     * @throws IOException
     */
    private int startANewHtmlLine(FileWriter outfile, GDD gdd, Token tok) throws IOException {
        outputIt(outfile, gdd, "<li>");
        int lineNumber = tok.getSource().getLineNumber();
        return lineNumber;
    }

    /**
     *  Should we start a new Html line? Every token carries with it
     *  the original source line number. If it changes, we start a new
     *  Html line. 
     * @param tok
     * @param lineNumber
     * @return
     */
    private boolean isItANewHtmlLine(Token tok, int lineNumber) {
        return tok.getSource().getLineNumber() != lineNumber;
    }

    /**    will the next record indicate the present command is a replacement?
     *     If so, don't print this command, just skip it. The next token
     *     (containing the original command) will be printed on the next loop cycle.
     *
     * @param tok
     * @param tokensList
     * @param i
     * @return
     */
    private boolean skipThisToken(Token tok, TokenList tokensList, int i) {
        if ( tok.getType().equals( TokenType.COMMAND )) {
            if ( tokensList.isNextToken( i, TokenType.REPLACED_COMMAND ) )
                return true;
        }
        return false;
    }

    /**
     *  We skip next one or many tokens at times. For example, we process
     * the explicit newline command i.e. "[]", and output a newline.
     * If this token appears at the end of a line (i.e. next immediate token is "[cr]",
     * we don't want to output one more Html newline. 
     * @param currTokIndex
     * @param tok
     * @param gdd
     * @param tokensList
     * @return
     * @throws IOException
     */
    private int skipNextTokens(int currTokIndex, Token tok, GDD gdd, TokenList tokensList) throws IOException {
        if ( tok.getContent().endsWith( "[]" )) {
            if ( tokensList.areNextTokenContentsEqualTo(currTokIndex, "[cr]") )
                return 1;                
        } else if ( tok.getType().equals( TokenType.COMPOUND_COMMAND )) {
            return tokensToSkip( gdd.getInputTokens(), currTokIndex );
        }

        return 0;
    }

    /**
     * Build the string of HTML needed as the prelude to output of the code
     * @param gdd the GDD. Only the literals are used
     * @param inputFile filename of the file being converted into a listing
     * @return the string of HTML to emit
     */
    public String getHeaderHtml( final GDD gdd, final String inputFile )
    {
        if ( gdd == null ) {
            return( null );
        }

        StringBuilder header = new StringBuilder( 60 );
        header.append( "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" " )
              .append( "\"http://www.w3.org/TR/html4/loose.dtd\">\n" )
              .append( "<HTML>\n" )
              .append( "<HEAD>\n" )
              .append( "<!-- " )
              .append( gdd.getLit( "CREATED_BY_PLATYPUS" ))
              .append( " " )
              .append( gdd.getLit( "VERSION" ))
              .append( "  " )
              .append( gdd.getLit( "AVAILABLE_AT_PZ_ORG" ))
              .append( "-->\n" );

        if( inputFile != null ) {
            header.append( "<TITLE>\n" )
                  .append( "Playtpus File  " )
                  .append( inputFile )
                  .append( "\n</TITLE>\n" );
        }

        header.append( "</HEAD>\n" )
              .append( "<BODY>\n" )
              .append( "<div style=\"background-color:#EEEEEE;" )
              .append( "font-size: 10.5pt;" )
              .append( "font-family: Consolas, 'Courier New',Courier, monospace;" )
              .append( "font-weight: normal;\">\n<ol>" );

        return( header.toString() );
    }

    /**
     *  Get an instance of FileWriter, to which we will emit HTML output
     *
     *  @param filename of file to open (obtained from the command line)
     *  @param gdd GDD. Only the literals are used in this method.
     *  @throws IOException in the event the file can't be opened
     *  @return the open FileWriter
     */
    public FileWriter openOutputFile( final String filename,
                                      final GDD gdd ) throws IOException
    {
        if( filename == null || filename.isEmpty() ) {
            logger.severe( gdd.getLit( "ERROR.OPENING_OUTPUT_FILE") + " " + filename );
            throw new IOException();
        }

        FileWriter fwOut;
        try {
            fwOut = new FileWriter( filename );
        }
        catch ( IOException e ) {
            logger.severe( gdd.getLit( "ERROR.OPENING_OUTPUT_FILE") + " " + filename );
            throw new IOException();
        }

        return( fwOut );
    }

    /** TODO: This is used only in unit tests - refactor unit tests and remove this. 
     * Output the HTML literals for a new command, including the pop-up title words
     * @param outfile file to write the HTML to
     * @param tok the token for the command
     * @param gdd the GDD. Only the literals are used
     * @throws IOException in event of exception in writing to file
     * @return string written to file, or null on error
     */
    public String  outputCommand( final FileWriter outfile, final Token tok, GDD gdd )
            throws IOException
    {
        final String s;

        if( tok.getRoot() != null && tok.getRoot().equals( "[CR]" )) { // print [CR] as a blank line
            s = "<br>\n";
        }
        else {
            s = "<span title=\"" +
                gdd.getLit( "COMMAND" ) +
                "\"><font color=\"blue\">" +
                convertToHtmlText( tok.getContent() ) +
                "</font></span>";
        }

        return outputIt(outfile, gdd, s);
    }

    /** Write a String to the output - deals with the business of actual writing.
     *  If any exceptions happen while writing - are logged and rethrown. 
     *
     * @param outfile
     * @param gdd
     * @param s
     * @return
     * @throws IOException
     */
    private String outputIt(FileWriter outfile, GDD gdd, String s) throws IOException {
        try {
            if ( outfile != null ) {
                outfile.write( s );
            }
        }
        catch( IOException ioe ) {
            logger.severe( gdd.getLit( "ERROR.WRITING_TO_OUTPUT_FILE" ));
            throw new IOException();
        }

        return( s );
    }

    /**
     * We skip all compound commands tokens. 
     * @param tokList
     * @param tokenNumber which token in the input stream tok is
     * @throws IOException in event of exception in writing to file
     * @return how many tokens to skip over for this command.
     */
    public int tokensToSkip( TokenList tokList, int tokenNumber)
            throws IOException
    {
        int i;
        for( i = tokenNumber + 1; i < tokList.size(); i++ )
        {
            Token nextTok = tokList.get( i );
            if( nextTok.getType() == TokenType.COMPOUND_COMMAND_END ) {
                break;
            }
        }
        return( i - tokenNumber  );
    }

    /** TODO: This is used only in unit tests - refactor unit tests and remove this.
     * Output the HTML literals for a new paragraph including the pop-up title words
     * @param outfile file to write the HTML to
     * @param gdd the GDD. Only the literals are used
     * @throws IOException in event of exception in writing to file
     * @return string written to the file, or null on error
     */
    public String outputNewLine( final FileWriter outfile, final GDD gdd )
            throws IOException
    {
        final String newLine = "<span title=\"" +
                               gdd.getLit( "NEW_PARAGRAPH" ) +
                               "\"><font color=\"blue\">[]</font></span><br>";
        outputIt( outfile, gdd, newLine );
        return( newLine );
    }

    /**
     * This is the main line of the plug-in.
     * Platypus calls only the Start constructor and this method.
     *
     * @param gdd  The Global Document Data
     * @param clArgs command-line arguments in a hash map (key = argument, value = parameters)
     */
    public void process( GDD gdd, final CommandLineArgs clArgs )
    {
        FileWriter outputFile;
        String inputFile;

        assert( gdd != null && clArgs != null );

        logger = gdd.getLogger();

        try {
            inputFile = clArgs.lookup( "inputFile" );
            outputFile = openOutputFile( clArgs.lookup( "outputFile" ), gdd );

            emitHtmlHeader( inputFile, outputFile, gdd );
            emitListing( outputFile, gdd );
            emitClosingHtml( outputFile, gdd );

            closeOuputFile( outputFile, gdd );
        }
        catch( IOException ioe ) {
            return; //todo: log an error message
        }
    }

    //=== getters and setters ===

    public void setLogger( final Logger newLogger )
    {
        logger = newLogger;
    }
}
