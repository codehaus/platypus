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
import org.pz.platypus.exceptions.FilenameLookupException;

public class InfileTest
{
    private GDD gdd;

    @Before
    public void setUp()
    {
        gdd = new GDD();
        gdd.initialize();
    }

    @Test
    public void testConstructor()
    {
        final String filename = "testInfile";

        try {
            gdd.getInputFileList().addFilename( filename );
        }
        catch( FilenameLookupException fle ) {
            fail( "unexpected exception adding filename to Infile in testConstructor()" );
        }
        Infile inf = new Infile( filename, gdd );
        assertTrue( filename.equals( inf.getFilename() ));
    }
    
    @Test
    public void testOpenOfNonExistentFile()
    {
        final String invalidFilename = "this file does not exist!";

        try {
            gdd.getInputFileList().addFilename( invalidFilename );
        }
        catch( FilenameLookupException fle ) {
            fail( "unexpected exception adding filename to Infile in testOpenOfNonExistentFile()" );
        }

        Infile inf = new Infile( invalidFilename, gdd );
        assertEquals( Status.FILE_NOT_FOUND_ERR, inf.open() );
    }

    @Test
    public void testReadOfNonExistentFile()
    {
        final String invalidFilename = "this file does not exist!";

        try {
            gdd.getInputFileList().addFilename( invalidFilename );
        }
        catch( FilenameLookupException fle ) {
            fail( "unexpected exception adding filename to Infile in testReadOfNonExistentFile()" );
        }

        Infile inf = new Infile( invalidFilename, gdd );
        final LineList textLines = new LineList();
        assertEquals( Status.FILE_NOT_FOUND_ERR, inf.readFileIntoInputLines( textLines ));
    }

    @Test
    public void testReadFileIntoNullArrayOfInputLines()
    {
        final String invalidFilename = "this file does not exist!";

        try {
            gdd.getInputFileList().addFilename( invalidFilename );
        }
        catch( FilenameLookupException fle ) {
            fail( "unexpected exception in testReadFileIntoNullArrayOfInputLines()" );
        }

        Infile inf = new Infile( invalidFilename, gdd );
        final  LineList textLines = null;
        assertEquals( Status.INVALID_PARAM_NULL, inf.readFileIntoInputLines( textLines ));
    }


    @Test
    public void testReadingIntoNullTextLines()
    {
        final String validFilename = "inputfile";

        try {
            gdd.getInputFileList().addFilename( validFilename );
        }
        catch( FilenameLookupException fle ) {
            fail( "unexpected exception adding filename in testReadingIntoNullTextLines" );
        }

        Infile inf = new Infile( validFilename, gdd );
        assertEquals( Status.INVALID_PARAM_NULL, inf.readFileIntoInputLines( null ));
    }
}
