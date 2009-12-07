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

    /**
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
        if ( outputFile == null ) {
            return;
        }

        try {
            outputFile.write(
                              "</div>\n" +
                              "</ol>\n" +
                              "</BODY>\n" +
                              "</HTML>\n" );
        }
        catch( IOException ioe ) {
            logger.severe( gdd.getLit( "ERROR.WRITING_TO_OUTPUT_FILE" ));
            throw new IOException();
        }
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
        if( outputFile == null ) {
            return;
        }

        try {
            outputFile.write( getHeaderHtml( gdd, inputFile ));
        }
        catch( IOException ioe ) {
            logger.severe( gdd.getLit( "ERROR.WRITING_TO_OUTPUT_FILE" ));
            throw new IOException();
        }
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

        for( int i = 0; i < gdd.getInputTokens().size(); i++ ) {
            tok = gdd.getInputTokens().get( i );

            if( tok.getSource().getLineNumber() != lineNumber ){
                outfile.write( "<li>");
                lineNumber = tok.getSource().getLineNumber();
            }

            if( tok.getContent().equals("[cr]" )) {
                outfile.write( "" );
            }
            else if ( tok.getContent().endsWith( "[]" )) {
                outfile.write( tok.getContent().substring( 0, tok.getContent().length() - 2 ));
                outputNewLine( outfile, gdd );
                if( gdd.getInputTokens().size() > i+1 ) {
                    Token t =  gdd.getInputTokens().get( i+1 );
                    if ( t != null && t.getContent().equals("[cr]" )) {
                        i += 1; // skip the [cr] token after a []
                    }
                }
            }
            else if ( tok.getType().equals( TokenType.COMMAND )) {
                // will the next record indicate the present command is a replacement?
                // If so, don't print this command, just skip it. The next token
                // (containing the original command) will be printed on the next loop cycle.
                Token nextTok = gdd.getInputTokens().getNextToken( i );
                if( nextTok != null && nextTok.getType().equals( TokenType.REPLACED_COMMAND )) {
                    continue;
                }
                else {
                    outputCommand( outfile, tok, gdd );
                }
                continue;
            }
            else if ( tok.getType().equals( TokenType.REPLACED_COMMAND )) {
                outputCommand( outfile, tok, gdd );
                continue;
            }
            else if ( tok.getType().equals( TokenType.COMPOUND_COMMAND )) {
                i += outputCompoundCommand( outfile, tok, gdd, i );
                continue;
            }
            else if ( tok.getType().equals( TokenType.MACRO )) {
                outputMacro( outfile, tok, gdd );
                continue;
            }
            else if ( tok.getType().equals( TokenType.LINE_COMMENT )) {
                outputComment( outfile, tok, gdd );
            }
            else if ( tok.getType().equals( TokenType.BLOCK_COMMENT )) {
                outputBlockComment( outfile, tok, gdd );
                continue;
            }
            else if ( tok.getType().equals( TokenType.SYMBOL )) {
                outputSymbol( outfile, tok, gdd );
                continue;
            }
            else if ( tok.getType().equals( TokenType.TEXT )) {
                outfile.write( convertToHtmlText( tok.getContent() ));
                continue;
            }
            else {
                System.err.println( "Unrecognized command type in Listing for: " + tok.getContent() );
                continue;
            }

            outfile.write( "</li>\n" );
        }
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

        try {
            outfile.write( s );
        }
        catch( IOException ioe ) {
            logger.severe( gdd.getLit( "ERROR.WRITING_TO_OUTPUT_FILE" ));
            throw new IOException();
        }

        return( s );
    }

    /**
     * Output the HTML literals for a new command, including the pop-up title words
     * @param outfile file to write the HTML to
     * @param tok the token for the command
     * @param gdd the GDD. Only the literals are used
     * @param tokenNumber which token in the input stream tok is
     * @throws IOException in event of exception in writing to file
     * @return how many tokens to skip over for this command.
     */
    public int outputCompoundCommand( final FileWriter outfile, final Token tok, GDD gdd, int
                                          tokenNumber)
            throws IOException
    {
        final String s = "<span title=\"" +
                         gdd.getLit( "COMPOUND_COMMAND" ) +
                         "\"><font color=\"blue\">" +
                         convertToHtmlText( tok.getContent() ) +
                         "</font></span>";
        try {
            outfile.write( s );
        }
        catch( IOException ioe ) {
            logger.severe( gdd.getLit( "ERROR.WRITING_TO_OUTPUT_FILE" ));
            throw new IOException();
        }

        int i;
        for( i = tokenNumber + 1; i < gdd.getInputTokens().size(); i++ )
        {
            Token nextTok = gdd.getInputTokens().get( i );
            if( nextTok.getType() == TokenType.COMPOUND_COMMAND_END ) {
                break;
            }
        }
        return( i - tokenNumber  );
    }

    /**
     * Output the HTML literals for a line comment, including the pop-up title words
     * @param outfile file to write the HTML to
     * @param tok the token for the command
     * @param gdd the GDD. Only the literals are used
     * @throws IOException in event of exception in writing to file
     * @return string written to file, or null on error
     */
    public String outputComment( final FileWriter outfile, final Token tok, GDD gdd )
            throws IOException
    {
        final String s = "<span title=\"" +
                         gdd.getLit( "COMMENT" ) +
                         "\"><font color=\"green\">" +
                         convertToHtmlText( tok.getContent() ) +
                         "</font></span>" +
                         "<br>" ;

        try {
            outfile.write( s );
        }
        catch( IOException ioe ) {
            logger.severe( gdd.getLit( "ERROR.WRITING_TO_OUTPUT_FILE" ));
            throw new IOException();
        }

        return( s );
    }

    /**
     * Output the HTML literals for a block comment, including the pop-up title words
     * @param outfile file to write the HTML to
     * @param tok the token for the command
     * @param gdd the GDD. Only the literals are used
     * @throws IOException in event of exception in writing to file
     * @return string written to file, or null on error
     */
    public String outputBlockComment( final FileWriter outfile, final Token tok, GDD gdd )
            throws IOException
    {
        final String s = "<span title=\"" +
                         gdd.getLit( "BLOCK_COMMENT" ) +
                         "\"><font color=\"green\">" +
                         convertToHtmlText( tok.getContent() ) +
                         "</font></span>";

        try {
            outfile.write( s );
        }
        catch( IOException ioe ) {
            logger.severe( gdd.getLit( "ERROR.WRITING_TO_OUTPUT_FILE" ));
            throw new IOException();
        }

        return( s );
    }

    /**
     * Output the HTML literals for a new macro, including the pop-up title words
     * @param outfile file to write the HTML to
     * @param tok the token for the command
     * @param gdd the GDD. Only the literals are used
     * @throws IOException in event of exception in writing to file
     * @return string written to file, or null on error
     */
    public String  outputMacro( final FileWriter outfile, final Token tok, GDD gdd )
            throws IOException
    {
        final String s = "<span title=\"" +
                         gdd.getLit( "MACRO" ) +
                         "\"><font color=\"brown\"><b>" +
                         convertToHtmlText( tok.getContent() ) +
                         "</b></font></span>";
        try {
            outfile.write( s );
        }
        catch( IOException ioe ) {
            logger.severe( gdd.getLit( "ERROR.WRITING_TO_OUTPUT_FILE" ));
            throw new IOException();
        }

        return( s );
    }
    /**
     * Output the HTML literals for a new paragraph including the pop-up title words
     * @param outfile file to write the HTML to
     * @param gdd the GDD. Only the literals are used
     * @throws IOException in event of exception in writing to file
     * @return string written to the file, or null on error
     */
    public String outputNewLine( final FileWriter outfile, final GDD gdd )
            throws IOException
    {
        String newLine;

        try {
            newLine =  "<span title=\"" +
                       gdd.getLit( "NEW_PARAGRAPH" ) +
                       "\"><font color=\"blue\">[]</font></span><br>";
            outfile.write( newLine );
        }
        catch( IOException ioe ) {
            logger.severe( gdd.getLit( "ERROR.WRITING_TO_OUTPUT_FILE" ));
            throw new IOException();
        }

        return( newLine );
    }

    /**
     * Output the HTML literals for a line comment, including the pop-up title words
     * @param outfile file to write the HTML to
     * @param tok the token for the command
     * @param gdd the GDD. Only the literals are used
     * @throws IOException in event of exception in writing to file
     * @return string written to file, or null on error
     */
    public String outputSymbol( final FileWriter outfile, final Token tok, GDD gdd )
            throws IOException
    {
        final String s = "<span title=\"" +
                         gdd.getLit( "SYMBOL" ) +
                         "\"><font color=\"blue\">" + "<b>" +
                         convertToHtmlText( tok.getContent() ) +
                         "</b></font></span>";

        try {
            outfile.write( s );
        }
        catch( IOException ioe ) {
            logger.severe( gdd.getLit( "ERROR.WRITING_TO_OUTPUT_FILE" ));
            throw new IOException();
        }

        return( s );
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
