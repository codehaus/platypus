/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.rtf;

import org.pz.platypus.GDD;
import org.pz.platypus.DefaultValues;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Handles the output to the RTF file
 *
 * @author alb
 */
public class RtfOutfile
{
    private boolean isOpen = false;
    private String filename;
    private FileWriter fwOut;
    private GDD gdd;
    private RtfData rtd;

    public RtfOutfile( final String filename, final RtfData rtd )
    {
        this.filename = filename;
        this.gdd = rtd.getGdd();
        this.rtd = rtd;
    }
    /**
     *  Get an instance of FileWriter, to which we will emit HTML output
     *
     *  @param filename of file to open (obtained from the command line)
     *  @param log logger for error messages
     *  @throws java.io.IOException in the event the file can't be opened
     *  @return the open FileWriter
     */
    public FileWriter open( final String filename, final Logger log )
            throws IOException
    {
        if( filename == null || filename.isEmpty() ) {
            log.severe( gdd.getLit( "ERROR.OPENING_OUTPUT_FILE" ) + " " + filename );
            throw new IOException();
        }

        try {
            fwOut = new FileWriter( filename );
        }
        catch ( IOException e ) {
            log.severe( gdd.getLit( "ERROR.OPENING_OUTPUT_FILE" ) + " " + filename );
            isOpen = false;
            throw new IOException();
        }

        isOpen = true;

        emitRtfProlog();
        return( fwOut );
    }

    /**
     * Writes the required header parts of the RTF file. Called from the RTF-file open routine,
     * so we know for sure the file exists and is writable.
     *
     * @throws java.io.IOException in the event of an I/O error
     */
    private void emitRtfProlog()
            throws IOException
    {
        final String prologString = "{\\rtf1\\ansi";
        final String resetStyles = "\\plain";
        final String defaultFont = "\\deff0{\\fonttbl{\\f0 Times New Roman;}}";
        final String docComment = "{\\info{\\doccomm " + gdd.getLit( "CREATED_BY_PLATYPUS" ) + " " +
                                   gdd.getLit( "VERSION") + " " + gdd.getLit( "AVAILABLE_AT_PZ_ORG" ) + "}}";
        final String widowControlOn = "\\widowctrl";
        final String pageWidth =  "\\paperw" + (int)( rtd.getPageWidth()  * DefaultValues.TWIPS_PER_POINT );
        final String pageHeight = "\\paperh" + (int)( rtd.getPageHeight() * DefaultValues.TWIPS_PER_POINT );
        final String marginR = "\\margr" + (int)( rtd.getMarginRight()    * DefaultValues.TWIPS_PER_POINT );
        final String marginL = "\\margl" + (int)( rtd.getMarginLeft()     * DefaultValues.TWIPS_PER_POINT );
        final String marginT = "\\margt" + (int)( rtd.getMarginTop()      * DefaultValues.TWIPS_PER_POINT );
        final String marginB = "\\margb" + (int)( rtd.getMarginBottom()   * DefaultValues.TWIPS_PER_POINT );
        final String eoCommands = " ";

        try {
            fwOut.write( prologString );
            fwOut.write( resetStyles );
            fwOut.write( defaultFont );
            fwOut.write( docComment );
            fwOut.write( widowControlOn );
            fwOut.write( pageWidth );
            fwOut.write( pageHeight );
            fwOut.write( marginR );
            fwOut.write( marginL );
            fwOut.write( marginT );
            fwOut.write( marginB );
            fwOut.write( eoCommands );
        }
        catch( IOException ioe ) {
            gdd.logSevere( gdd.getLit( "ERROR.WRITING_TO_OUTPUT_FILE" ));
            throw new IOException();
        }

    }

    /**
     * Writes the closing part of the RTF file. Called from the RTF-file close routine,
     * so we know for sure the file exists and is writable.
     *
     * @throws IOException in the event of an I/O error
     */
    private void emitRtfEpilog()
            throws IOException
    {
        final String epilogString = "}";

        try {
            fwOut.write( epilogString );
        }
        catch( IOException ioe ) {
            gdd.logSevere( gdd.getLit( "ERROR.WRITING_TO_OUTPUT_FILE" ));
            throw new IOException();
        }

    }

    /**
     * Write a String of text to the RTF file.
     *
     * @param text text to write
     * @throws IOException in the event of an I/O error
     */
    public void writeText( String text ) throws IOException
    {
        if( text == null ) {
            return;
        }

        try {
            if( ! isOpen ) {
                open( filename, gdd.getLogger() );
            }

            fwOut.write( text );
        }
        catch( IOException ioe ) {
            gdd.logSevere( gdd.getLit( "ERROR.WRITING_TO_OUTPUT_FILE" ));
            throw new IOException();
        }
    }
    
    /**
     * Write a command string to the RTF file.
     *
     * @param command text to write
     * @throws IOException in the event of an I/O error
     */
    public void writeCommand( String command ) throws IOException
    {
        if( command == null ) {
            return;
        }

        try {
            if( ! isOpen ) {
                open( filename, gdd.getLogger() );
            }

            fwOut.write( command );
        }
        catch( IOException ioe ) {
            gdd.logSevere( gdd.getLit( "ERROR.WRITING_TO_OUTPUT_FILE" ));
            throw new IOException();
        }
    }

    /**
     * Close the file
     *
     * @param log for literals
     * @throws IOException in case of error
     */
    public void close( Logger log ) throws IOException
    {
        if( ! isOpen ) {
            return;
        }

        emitRtfEpilog();

        try {
            fwOut.close();
        }
        catch( IOException ioe ) {
            log.severe( gdd.getLit( "ERROR.CLOSING_OUTPUT_FILE" ));
            throw new IOException();
        }
    }
}
