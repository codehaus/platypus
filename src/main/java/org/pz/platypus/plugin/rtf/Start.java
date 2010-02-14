/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.rtf;

import org.pz.platypus.*;
import org.pz.platypus.interfaces.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Test plugin for converting Platypus files to Rich Text Format (rtf).
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
public class Start implements Pluggable
{
    private Logger logger;
    private RtfOutfile outfile;
    private RtfData rtd;
    private RtfCommandTable ctable;

    /**
     * Start() is always called first by Platypus, followed by a call to process()
     * Any plugin initialization code should go in here.
     */
    public Start()
    {
     //  System.out.println("In the RTF Plugin!");
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
        assert( gdd != null && clArgs != null );

        logger = gdd.getLogger();
        rtd = new RtfData( gdd );
        outfile = new RtfOutfile( clArgs.lookup( "outputFile" ), rtd );
        ctable = new RtfCommandTable();
        ctable.load( gdd );


        try {
            processInputTokens( gdd, outfile );
            outfile.close( gdd.getLogger() );
        }
        catch( IOException ioe ) {
            return; //todo: log an error message
        }
    }

    /**
     * Where the content of the listing file is written out
     *
     * @param outfile the file being written to
     * @param gdd the GDD.
     * @throws java.io.IOException in the event the file can't be written to
     */
    public void processInputTokens( GDD gdd, final RtfOutfile outfile )
           throws IOException
    {
        Token tok;

        final TokenList tokensList = gdd.getInputTokens();
        for( int i = 0; i < tokensList.size(); i++ )
        {
            tok = tokensList.get( i );
            int tokensToSkip = processToken( gdd, tok, outfile, i );
            i += tokensToSkip;
        }
    }

    /**
     * Processes the individual tokens
     *
     * @param gdd  global document data (data structure)
     * @param tok token being processed
     * @param outfile output RTF file being created
     * @return returns the number of tokens to skip. In vast majority of cases, returns 1.
     * @throws IOException in event of an I/O error
     */
    public int processToken( final GDD gdd, final Token tok,
                             final RtfOutfile outfile, final int tokenNumber )
            throws IOException
    {
        int tokensToSkip = 0;

        switch( tok.getType() )
        {
            case BLOCK_COMMENT:
            case LINE_COMMENT:
                break;

            case COMMAND:
                processCommand( gdd, ctable, tok, tokenNumber );
                break;

            case COMPOUND_COMMAND: //TODO
                break;

            case COMPOUND_COMMAND_END: //TODO
                break;

            case REPLACED_COMMAND: //TODO
                break;

            case SYMBOL:  //TODO
                break;

            case TEXT:
                processText( gdd, tok, outfile );
                break;

            default:
                gdd.logWarning( "ERROR.INVALID_TOKEN" ); //TODO: Add file and line # of error
            
        }

        return( tokensToSkip );
    }

    /**
     * Gets the RTF command and executes it.
     *
     * @param gdd the GDD
     * @param commandTable the command table holding the commands for RTF
     * @param tok the token containing text
     * @param tokNum the number of the token
     * @throws IOException occurs if an I/O error occurred during output
     */
    public void processCommand( final GDD gdd, final RtfCommandTable commandTable,
                                final Token tok, final int tokNum  )
            throws IOException
    {
        assert( gdd != null );
        assert( commandTable != null );
        assert( tok != null );
        assert( outfile != null );

        OutputCommandable command = commandTable.getCommand( tok.getRoot() );
        if( command == null ) {
            //TODO: error message
        }

        command.process( rtd, tok, tokNum );          // < <<<<<< resume here 
    }

    /**
     * Validates that there is text to output and then writes it as a string to the file.
     *
     * @param gdd the GDD
     * @param tok the token containing text
     * @param outfile the file to write the text to
     * @throws IOException occurs if an I/O error occurred during output
     */
    public void processText(  final GDD gdd, final Token tok, final RtfOutfile outfile  )
            throws IOException
    {
        assert( gdd != null );
        assert( tok != null );
        assert( outfile != null );

        String text = tok.getContent();

        if( text != null && ! text.isEmpty() ) {
             outfile.writeText( text );
        }
    }

    /**
     * Where the content of the listing file is written out
     *
     * @param outfile the file being written to
     * @param gdd the GDD. Only the literals are used
     * @throws java.io.IOException in the event the file can't be written to
     */
    public void emitListing( final FileWriter outfile, final GDD gdd )
           throws IOException
    {
//        Token tok;
//        int lineNumber = 0;
//
//        final TokenList tokensList = gdd.getInputTokens();
//        for( int i = 0; i < tokensList.size(); i++ ) {
//            tok = tokensList.get( i );
//
//            if(isItANewHtmlLine(tok, lineNumber))
//                lineNumber = startANewHtmlLine(outfile, gdd, tok);
//
//            if (skipThisToken(tok, tokensList, i))
//                continue;
//
//            i += skipNextTokens(i, tok, tokensList);
//
//            printToken(outfile, gdd, tok);
//        }
    }
}