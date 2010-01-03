/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf;

import org.pz.platypus.*;
import org.pz.platypus.exceptions.FileCloseException;
import org.pz.platypus.exceptions.InvalidConfigFileException;
import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.Pluggable;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Main line for PDF plugin.
 *
 * Error-handling convention:
 *      Output plugins handle all their own errors. Platypus is made aware of a problem
 *      only by the value returned from calling process(). However, it does not issue
 *      any user message(s) as a result of this.
 *
 * @author alb
 */
public class Start implements Pluggable
{
    /** contains state data for the pdf file */
    private PdfData pdfData;

    /** the outputfile we write to */
    private PdfOutfile outfile = null;

    /** the command table indexed by command root */
    private PdfCommandTable commandTable = null;

    /** the symbols table indexed by root */
    private PdfSymbolsTable symbolsTable = null;

    /**
     * Start() is always called first by Platypus, followed by a call to process()
     * Any plugin initialization code should go in here.
     */
    public Start()
    {
        outfile = new PdfOutfile();
        commandTable = new PdfCommandTable();
    }

    /**
     * Constructor that is only ever used in unit testing
     * @param newOutfile the outfile for this PDF
     * @param newCommandTable command table for this PDF
     */
    public Start( final PdfOutfile newOutfile, final PdfCommandTable newCommandTable)
    {
        outfile = newOutfile;
        commandTable = newCommandTable;
    }

    /**
     * This is the main line of the plug-in.
     * Platypus calls only the Start constructor and this method.
     *
     * @param gdd  The Global Document Data
     * @param clArgs command-line arguments in a hash map (key = argument, value = parameters)
     */
    public void process( final GDD gdd, final CommandLineArgs clArgs )
    {
        assert( gdd != null && clArgs != null );

        gdd.log( "Processing starting in PDF plug-in" );
        setUpDataItems( gdd );
        if( commandTable.getSize() == 0  ) {
            // an error occurred, so get out.
            return;
        }

        final String outFilename = clArgs.lookup( "outputFile" );
        if( outFilename == null ) {
            gdd.logSevere( gdd.getLit( "ERROR.OUTPUT_FILENAME_NOT_SPECIFIED" ));
            return;
        }

        try {
            processTokens( pdfData, outFilename );
            outfile.close();
            gdd.log( "Closed output PDF file" );
        }
        catch( IOException ioe ) {
            return;  //todo: log some kind of error
        }
        catch( FileCloseException fce ) {
            gdd.logWarning( gdd.getLit( "ERROR.CLOSING_OUTPUT_FILE" ) + ": " +
                           fce.getMessage() );
        }
    }

    /**
     * set up various data items needed in processing
     * @param gdd the GDD
     */
    void setUpDataItems( final GDD gdd )
    {
        pdfData = new PdfData( gdd );
        try {
            pdfData.init();
        }
        catch( InvalidConfigFileException icfe ) {
            return;  // the error message has already been displayed; so just exit.
        }

        pdfData.setOutfile( outfile );
        outfile.setPdfData( pdfData );
        commandTable.load( gdd );
        gdd.log( "Command table in PDF plug-in loaded with " + commandTable.getSize() +
                 " commands");
    }

    /**
     * Where the token stream is translated into text and actions.
     *
     * @param pdfData state info about the PDF file
     * @param outfileName the file being written to
     * @throws IOException in the event the file can't be written to
     */
    public void processTokens( final PdfData pdfData, final String outfileName )
           throws IOException
    {
        Token tok;
        GDD gdd = pdfData.getGdd();
        TokenList tokenList = gdd.getInputTokens();

        for( int i = 0; i < tokenList.size(); i++ ) {
            tok = tokenList.get( i );
            switch( tok.getType() )
            {
                case BLOCK_COMMENT:
                case LINE_COMMENT:
                    break;
                case COMPOUND_COMMAND:
                    int j = processCompoundCommand( gdd, i );
                    i += j;
                    break;
                case COMMAND:
                    processCommand( tok, pdfData, i );

                    // if the current command is [] and it's followed by EOL, don't process the EOL.
                    if( tok.getRoot().equals( "[]" ) && isNextTokenCr( i, tokenList )) {
                        ++i;
                    }
                    break;
                case TEXT:
                case MACRO_TEXT:
                    processText( gdd, outfileName, tok.getContent() );
                    break;
                case SYMBOL:
                    processSymbol( outfileName, tok, i, pdfData );
                    break;
                case MACRO:
                    processMacro();
                    break;
                case EOF:
                    return;
                default:
                    gdd.logWarning( gdd.getLit( "ERROR.INVALID_TOKEN" ) + ": " +
                                    tok.getRoot() + " " +
                                    gdd.getLit( "IGNORED" ));
                    return;
            }
        }
    }

    /**
     * Determines if the next token in the token list is the command [cr]
     *
     * @param currTokNumber the number of the current token
     * @param tokList the list of tokens
     * @return true if == [cr]; false if not, or if an error occurred.
     */
    boolean isNextTokenCr( int currTokNumber, TokenList tokList )
    {
        assert( currTokNumber >= 0 );

        if( tokList == null ) {
            return( false );
        }

        Token nextTok = tokList.getNextToken( currTokNumber );
        if( nextTok == null ) {
            return( false );
        }

        if( nextTok.getType().equals( TokenType.COMMAND ) &&
            nextTok.getRoot().equals( "[cr]" )) {
            return( true );
        }

        return( false );
    }

    /**
     * Principal method for implementing command tokens
     * @param tok command token to process
     * @param pdfData document state data
     * @param tokNum the number of the token in the token list
     */
    void processCommand( final Token tok, final PdfData pdfData, final int tokNum )
    {
        assert( tok != null && pdfData != null );
        assert( commandTable!= null && commandTable.getSize() > 0 );

        // lookup the command in the PDF command table
        OutputCommandable oc = commandTable.getCommand( tok.getRoot() );
        if( oc != null ) {
             oc.process( pdfData, tok, tokNum );
        }
        else {
            errMsgUnrecognizedCommand( tok, pdfData.getGdd() );
        }
    }

    /**
     * Output error message to logger for unrecognized command
     * @param tok the command token
     * @param gdd the GDD
     */
    private void errMsgUnrecognizedCommand( final Token tok, final GDD gdd )
    {
        pdfData.getGdd().logWarning(
                gdd.getLit( "FILE#" ) + " "  + tok.getSource().getFileNumber()  + " " +
                gdd.getLit( "LINE#" ) + " "  + tok.getSource().getLineNumber()  + " " +
                gdd.getLit( "COMMAND_UNRECOGNIZED" ) +  " " +
                gdd.getLit( "FOR" )  + " " +
                gdd.getSysStrings().getString( "_format" ) + ": " +
                tok.getRoot() + " " );
    }

    /**
     * Handle compount commands. Essentially, extract the individual commands and process those.
     * @param gdd the GDD
     * @param startTokNumber the number of the opening token in the compound command
     * @return the number of tokens to skip over because they're processed here
     */
    int processCompoundCommand( final GDD gdd, final int startTokNumber )
    {
        int i;
        Token tok;
        TokenList inputTokens;

        ArrayList<Token> commandList = new ArrayList<Token>();
        inputTokens = gdd.getInputTokens();

        for( i = startTokNumber+1; i < inputTokens.size(); i++ )
        {
            tok = inputTokens.get( i );
            if( tok.getType() == TokenType.COMMAND ) {
                commandList.add( tok );
            }
            else {
                if ( tok.getType() == TokenType.COMPOUND_COMMAND_END ) {
                    break;
                }
                else {
                    gdd.logWarning( "COMPOUND COMMAND MISSING CLOSING TOKEN. COMPLETE COMMAND IGNORED" );
                    return( commandList.size() );
                }
            }
        }

        i = startTokNumber+1;
        for( Token t : commandList ) {
            processCommand( t, pdfData, i++ );
        }

        return( commandList.size() + 1 ); // the +1 is for the COMPOUND_COMMAND_END token
    }

    void processMacro()
    {

    }

    /**
     * Process a symbol or foreign character
     *
     * @param filename name of the output file (in the event the output file is not open yet)
     * @param tok the Token containing the symbol info
     * @param tokNum the number of the token
     * @param pdd the PDF document data
     * @throws IOException if any error occurred
     */
    void processSymbol( final String filename, final Token tok, final int tokNum, final PdfData pdd )
            throws IOException
    {
        if( filename == null || tok == null || pdd == null ) {
            return; // should we log a warning here?
        }

        String sym = tok.getRoot();

        // we don't open the file until we have text to output.
        if( ! outfile.isOpen() ) {
            try {
                outfile.open( filename, pdfData );
            }
            catch( IOException ioe ) {
                throw new IOException();
            }
        }

        // lookup the command in the PDF command table
        OutputCommandable oc = commandTable.getCommand( sym );
        if( oc != null ) {
             oc.process( pdfData, tok, tokNum );
        }
        else {
            errMsgUnrecognizedCommand( tok, pdfData.getGdd() );
        }
    }

    /**
     * Process a token consisting of a text item
     * @param gdd the GDD
     * @param filename name of the output file (in the event the output file is not open yet
     * @param text the text to output
     * @throws IOException if any error occurred
     */
    void processText( final GDD gdd, final String filename, final String text ) throws IOException
    {
        if( gdd == null || text == null || text.isEmpty() ) {
            return; // should we log a warning here?
        }

        // we don't open the file until we have text to output.
        if( ! outfile.isOpen() ) {
            try {
                outfile.open( filename, pdfData );
            }
            catch( IOException ioe ) {
                throw new IOException();
            }
        }
        outfile.emitText( text );
    }
}
