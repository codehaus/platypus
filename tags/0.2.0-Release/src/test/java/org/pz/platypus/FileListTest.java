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

/**
 *
 * @author alb
 */
public class FileListTest {

    private FileList fl;
    public FileListTest() {}

    @Before
    public void setUp() 
    {
        fl = new FileList();
    }
    
    @Test
    public void testConstructor()
    {
        assertEquals( 0, fl.getSize() );
    }

    /**
     * Test of addFilename method, of class FileList.
     */
    @Test
    public void addFilenameNull()
    {
        int i = 0;
        try {
            i = fl.addFilename( null );
        }
        catch(  FilenameLookupException fle ) {
            fail( "unexpected Exception in addFilenameNull()" );
        }

        assertEquals( Status.INVALID_FILENAME, i );
    }
    
    @Test
    public void addFilenameEmpty()
    {
        int i = 0;
        try {
            i = fl.addFilename( "" );
        }
        catch(  FilenameLookupException fle ) {
            fail( "unexpected Exception in addFilenameEmpty()" );
        }

        assertEquals( Status.INVALID_FILENAME, i );
    } 
    
    @Test
    public void addValidFilenames()
    {
        final String ValidFilename1 = "infile.txt";
        final String ValidFilename2 = "outfile.pdf";

        try {
            fl.addFilename( ValidFilename1 );
            fl.addFilename( ValidFilename2 );
        }
        catch(  FilenameLookupException fle ) {
            fail( "unexpected Exception in addValidFilenames()" );
        }

        assertEquals( 2, fl.getSize() );
    }

    /**
     * Add valid filename twice. Should return the same value both times.
     */
    @Test
    public void addValidFilenameTwice()
    {
        final String ValidFilename = "infile.txt";
        int i = 0, j = 0;
        try {
            i = fl.addFilename( ValidFilename );
            j = fl.addFilename( ValidFilename );
        }
        catch(  FilenameLookupException fle ) {
            fail( "unexpected Exception in addValidFilenameTwice()" );
        }

        assertEquals( i, j );
        assertEquals( 1, fl.getSize() );
    }


    @Test
    public void getFilenameFromInvalidNumber()
    {
        final String ValidFilename = "infile.txt";

        try {
            fl.addFilename( ValidFilename );
            fl.getFilename( 0 );
            String t = fl.getFilename( -1 );
        }
        catch(  FilenameLookupException fle ) {
            assertTrue( 2 != 1 );
        }

        try {
            fl.getFilename( -1 );
        }
        catch(  FilenameLookupException fle ) {
            assertTrue( 2 != 1 );
            return;
        }

        fail( "expected Exception in getFilenameFromInvalidNumber() did not occur" );
    }
    
    @Test
    public void getFilenameFromValidNumber()
    {
        final String ValidFilename1 = "infile.txt";
        final String ValidFilename2 = "outfile.pdf";

        try {
            fl.addFilename( ValidFilename1 );
            String s = fl.getFilename( 1 );
            assertEquals( ValidFilename1, s );

            fl.addFilename( ValidFilename2 );
            String t = fl.getFilename( 2 );
            assertEquals( ValidFilename2, t );

            // now retry 1 to make sure nothing untoward happened
            s = fl.getFilename( 1 );
            assertEquals( ValidFilename1, s );
        }
        catch(  FilenameLookupException fle ) {
            fail( "unexpected Exception in getFilenameFromValidNumber()" );
        }
    }
    /**
     * Test of getFileNumber method, of class FileList.
     */
    @Test
    public void getFileNumberForValidFile()
    {
        final String ValidFilename = "infile.txt";
        int i = 0; int j = 0;
        try {
            i = fl.addFilename( ValidFilename );
            j = fl.getFileNumber( ValidFilename );
        }
        catch(  FilenameLookupException fle ) {
            fail( "unexpected Exception in getFileNumberForValidFile()" );
        }

        assertEquals( i, j );
    }
    
    @Test
    public void getFileNumberForNonExistentFile()
    {
        final String ValidFilename = "infile.txt";
        final String NonExistentFile = "abracadabra";

        try {
            fl.addFilename( ValidFilename );
            fl.getFileNumber( NonExistentFile );
        }
        catch(  FilenameLookupException fle ) {
            assertTrue( 2 != 1 );
            return;
        }

        fail( "expected Exception in getFileNumberForNonExistentFile() did not occur" );
    }

    @Test
    public void getFileNumberForNullFilename()
    {
        final String ValidFilename = "infile.txt";

        try {
            fl.addFilename( ValidFilename );
            fl.getFileNumber( null );
        }
        catch(  FilenameLookupException fle ) {
            assertTrue( 2 != 1 );
            return;
        }

        fail( "expected Exception in getFileNumberForNullFilename() did not occur" );
    }     
}