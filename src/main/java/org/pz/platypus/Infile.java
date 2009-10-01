/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */
package org.pz.platypus;

import org.pz.platypus.exceptions.FilenameLookupException;

import java.io.*;
import java.util.MissingResourceException;

/**
 * Handles input file I/O functions.
 *
 * @author alb
 */
public class Infile
{
	/** the name of the input file */
    private final String filename;

    /** file open status */
    private boolean isOpen = false;

    /** are we at EOF? */
    private boolean atEOF = false;

    /** input reader for file */
    private BufferedReader inReader;

    /** the current line number and filenumber */
    private int currFileNumber;
    private int currLineNumber;

    public Infile( final String name, GDD gdd )
    {
        filename = name;
        try {
            currFileNumber = gdd.getInputFileList().getFileNumber( name );
        }
        catch( FilenameLookupException fle ) {
            gdd.logSevere( filename + " " + gdd.getLit( "FILENAME_ NOT_IN_FILELIST" ));
            throw( new MissingResourceException( null, null, null ));
        }
        currLineNumber = 0;
    }

    /**
     * Reads the input file on a line basis, adding each line to an ArrayList for
     * later processing. Lines where the first character is a comment are skipped
     * (that is, not saved) in this process.
     *
     * @param textLines array of InputLine's into which the file will be read.
     * @return the number the total number of lines read or an error code on exception
     */
    public int readFileIntoInputLines( final LineList textLines )
    {
        if ( textLines == null ) {
            return( Status.INVALID_PARAM_NULL );
        }

        int retVal = 0;

        if ( ! isOpen ) {
            if (( retVal = open()) != Status.OK ) {
                return( retVal );
            }
        }

        while( ! atEOF )
        {
            final InputLine inputLine = new InputLine();
            
            switch( readNext1LineIntoInputLine( inputLine )) {                    
                case Status.OK:
                    textLines.add( inputLine );
                    break;

                case Status.AT_EOF:
                    atEOF = true; 
                    break;
                    
                case Status.IO_ERR:
                default:   // anything other is also an error 
                    break; //TODO Issue Error message

            }
        }
        
        // now close the input filestream
        try
        {
        	inReader.close();
            isOpen = false;
        }
        catch( IOException e )
        {
        	retVal = Status.IO_ERR;
        }
 
        return(( retVal == Status.IO_ERR ) ? retVal : currLineNumber );
    }
    
    /**
     * Reads a single line from the input file and converts into an InputLine
     * data structure, which contains, the file number of the input file,
     * the line number of the line, plus the content of the line
     * @param inputLine the InputLine into which the line of content is placed
     * @return Status.OK; Status.EOF at EOF; Status.IO_ERR if something goes wrong.
     * //TODO: Should return TEXT, COMMENT, EOF, ERROR.
     */
    private int readNext1LineIntoInputLine( final InputLine inputLine )
    {
        final String content;

        try {
            content = inReader.readLine();
            if ( content == null ) {
                return( Status.AT_EOF );
            }

            // the reader strips off the end-of-line char,
            // so we add it back as our end-of-line flag.
            inputLine.setContent( content + '\n' );
            inputLine.setSource( new Source( currFileNumber, ++currLineNumber ));
        } catch ( IOException e ) {
            return( Status.IO_ERR );
        }
        return( Status.OK );
    } 
    
    /**
     * open the file
     * @return Status.OK, if all went well; Status.FILE_NOT_FOUND_ERR, which should not occur.
     */
    public int open()
    {

        final File thisFile = new File( filename );
        
        try {
            FileReader fr = new FileReader( thisFile );
            inReader = new BufferedReader( fr );
        } 
        catch ( FileNotFoundException e ) {
            return( Status.FILE_NOT_FOUND_ERR ); // all files are known to exist by this point.
        }

        isOpen = true;
        atEOF = false;
        return( Status.OK );
    }   

    //=== getters and setters ===

    public String getFilename()
    {
        return( filename );
    }
    
    public int getLineNumber()
    {
        return( currLineNumber );
    }
}
