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
 * @author ask
 */
public class Start implements IPlugin
{
    private Logger logger;
    private boolean inCode = false;

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

    /**
     * Emits the HTML closing code
     * @param outputFile the HTML is written to this file
     * @param gdd GDD, only the literals are used in this method
     * @throws IOException in the rare event the file cannot be written to
     */
    public void emitClosingHtml( final FileWriter outputFile, final GDD gdd ) throws IOException
    {
        final String s = "</div>\n" + "</ol>\n" + "</BODY>\n" + "</HTML>\n";
        writeStringToFile( outputFile, gdd, s);
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
        writeStringToFile( outputFile, gdd, s );
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

            i += skipNextTokens(i, tok, tokensList);

            printToken(outfile, gdd, tok);
        }
    }

    /**
     * Print the token contents with all the Html bells and whistles
     * (colors, bold fonts, line breaks etc.)
     * Get the Html string and output it to the output file.
     * @param outfile
     * @param gdd
     * @param tok
     * @throws IOException
     */
    private void printToken(FileWriter outfile, GDD gdd, Token tok) throws IOException {
        HtmlListingStrategy strategy = HtmlListingStrategy.getFormatStrategy( tok );
        String s = strategy.format(tok, gdd);
        if (s.indexOf("[code]") != -1) {
            inCode = true;
            writeIt(outfile, gdd, strategy, s); // do not process whitespace(s)
            return;                             // in front of [code] command itself
        } else if (s.indexOf("[-code]") != -1) {
            inCode = false;
        }
        if (inCode) {
            s = makeInitialSpacesHard(s);
        }
        writeIt(outfile, gdd, strategy, s);
    }

    private void writeIt(FileWriter outfile, GDD gdd, HtmlListingStrategy strategy, String s) throws IOException {
        writeStringToFile(outfile, gdd, s);
        if (strategy.canOutputHtmlEndOfLine())
            outfile.write( "</li>\n" );
    }

    private String makeInitialSpacesHard(String s) {
        StringBuilder sb = new StringBuilder();
        for ( int i = 0; i < s.length(); ++i ) {
            char ch = s.charAt(i);
            if ( ch == ' ') {
                sb.append("&nbsp;");
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
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
        writeStringToFile(outfile, gdd, "<li>");
        int lineNumber = tok.getSource().getLineNumber();
        return lineNumber;
    }

    /**
     *  Should we start a new Html line? Every token carries with it
     *  the original source line number. If it changes, we start a new
     *  HTML line.
     *
     * @param tok the token we're processing
     * @param lineNumber the line number
     * @return whether we should start a new HTML line
     */
    private boolean isItANewHtmlLine(Token tok, int lineNumber)
    {
        return tok.getSource().getLineNumber() != lineNumber;
    }

    /**
     *  Will the next record indicate the present command is a replacement?
     *  If so, don't print this command, just skip it. The next token
     *  (containing the original command) will be printed on the next loop cycle.
     *
     * @param tok the command token, the next token is/isn't a replacement command
     * @param tokensList list of input tokens
     * @param i which token in the token list
     * @return whether to skip the current token in favor of the replacement token
     */
    private boolean skipThisToken( Token tok, TokenList tokensList, int i)
    {
        if ( tok.getType().equals( TokenType.COMMAND )) {
            if ( tokensList.isNextToken( i, TokenType.REPLACED_COMMAND ))
                return true;
        }
        return false;
    }

    /**
     * We skip next one or many tokens at times. For example, we process
     * the explicit newline command i.e. "[]", and output a newline.
     * If this token appears at the end of a line (i.e. next immediate token is "[cr]"),
     * we don't want to output one more Html newline.
     *
     * @param currTokIndex where we are in the token list
     * @param tok current token
     * @param tokensList list of input tokens
     * @return the number of tokens to skip
     * @throws IOException
     */
    private int skipNextTokens( int currTokIndex, Token tok, TokenList tokensList ) throws IOException
    {
        if ( tok.getContent().endsWith( "[]" )) {
            if ( tokensList.areNextTokenContentsEqualTo(currTokIndex, "[cr]") )
                return 1;                
        }
        else if ( tok.getType().equals( TokenType.COMPOUND_COMMAND )) {
            return tokensToSkip( tokensList, currTokIndex );
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

    /**
     * Write a String to the output - deals with the business of actual writing.
     * If any exceptions happen while writing - are logged and rethrown.
     *
     * @param outfile output file
     * @param gdd  global document data
     * @param s string to write to ouput
     * @return the string written out. Needed?
     * @throws IOException
     */
    public String writeStringToFile(FileWriter outfile, GDD gdd, String s) throws IOException {
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
     *
     * @param tokList list of tokens
     * @param tokenNumber which token in the input stream tok is
     * @throws IOException in event of exception in writing to file
     * @return how many tokens to skip over for this command.
     */
    public int tokensToSkip( TokenList tokList, int tokenNumber)
            throws IOException
    {
        int i = tokList.searchAheadFor(tokenNumber, TokenType.COMPOUND_COMMAND_END);
        return i - tokenNumber;
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
