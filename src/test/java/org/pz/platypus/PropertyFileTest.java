/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-10 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import org.junit.Test;

import java.io.*;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Tests of PropertyFile.java, which handles reading in property files.
 * Note: these tests create a file and then delete it.
 *
 * @author alb
 */
public class PropertyFileTest
{
    private GDD gdd;
    private PropertyFile pf;

    @Test
    public void testConstructor()
    {
        pf = new PropertyFile();
        assertEquals( 0, pf.getSize() );
    }

    @Test
    public void loadInvalidLine()   // loading empty/null lines shoud add no records to the property file
    {
        pf = new PropertyFile();
        HashMap<String,String>contents = (HashMap<String,String>) pf.getContents();
        assertEquals( 0, pf.getSize() );

        pf.loadLine( null );
        assertEquals( 0, pf.getSize() );

        pf.loadLine( "" );
        assertEquals( 0, pf.getSize() );
    }

    @Test
    public void loadCommentLine()   // loading a comment should add no records to the property file
    {
        pf = new PropertyFile();
        HashMap<String,String>contents = (HashMap<String,String>) pf.getContents();
        assertEquals( 0, pf.getSize() );

        pf.loadLine( "#this is a comment" );
        assertEquals( 0, pf.getSize() );
    }

    @Test
    public void loadValidLine()   // loading a valide record should add a record to the property file
    {
        pf = new PropertyFile();
        assertEquals( 0, pf.getSize() );

        pf.loadLine( "platypus=typesetting" );
        assertEquals( 1, pf.getSize() );
        assertEquals( "typesetting", pf.lookup( "platypus"));
    }

    @Test
    public void testForInvalidLineLoaded()   //
    {
        pf = new PropertyFile();
        assertEquals( 0, pf.getSize() );

        pf.loadLine( "platypus=typesetting" );
        assertEquals( 1, pf.getSize() );
        assertEquals( null, pf.lookup( "no-such-record"));
    }

    @Test
    public void testOpenReaderInvalidFile()
    {
        pf = new PropertyFile();
        BufferedReader bf = pf.open( "no such file ");
        assertEquals( null, bf );
    }

    @Test
    public void testOpenReaderValidFile()
    {
        pf = new PropertyFile();

        String inputFilename = "test-file_OK_to_delete";

        File f = new File ( inputFilename );
        try {
            f.createNewFile();
        }
        catch( Exception e ) {
            //do nothing.
        }
        BufferedReader bf = pf.open( inputFilename );
        assertNotNull( bf );

        try {
            bf.close();
        }
        catch( Exception e) {}
        finally{
            if( f.exists() ) {
                f.delete();
            }
        }
    }

    @Test
    public void retrieveNextLineEmptyFile()
    {
        pf = new PropertyFile();

        String inputFilename = "test-file_OK_to_delete1";

        File f = new File ( inputFilename );
        try {
            f.createNewFile();
        }
        catch( Exception e ) {
            //do nothing.
        }
        BufferedReader bf = pf.open( inputFilename );
        assertNotNull( bf );

        String s = pf.retrieveNextLine( bf );
        assertNull( s );
               
        try {
            bf.close();
        }
        catch( Exception e) {}
        finally{
            if( f.exists() ) {
                f.delete();
            }
        }
    }

    @Test
    public void retrieveNextLineValid()
    {
        pf = new PropertyFile();

        String inputFilename = "test-file_OK_to_delete2";
        String lineContent = "a=b";

        File f = new File ( inputFilename );
        try {
            f.createNewFile();
        }
        catch( Exception e ) {
            //do nothing.
        }

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter( inputFilename ));
            out.write( lineContent );
            out.close();
        }
        catch( Exception e ) {
            fail( "error writing content to input Property File in PropertyFileTest.java" );
        }

        BufferedReader bf = pf.open( inputFilename );
        assertNotNull( bf );

        String s = pf.retrieveNextLine( bf );
        assertEquals( lineContent, s );

        try {
            bf.close();
        }
        catch( Exception e) {}
        finally{
            if( f.exists() ) {
                f.delete();
            }
        }
    }

    @Test
    public void retrieveNextUntilEOF()
    {
        pf = new PropertyFile();

        String inputFilename = "test-file_OK_to_delete3";
        String lineContent = "a=b";

        File f = new File ( inputFilename );
        try {
            f.createNewFile();
        }
        catch( Exception e ) {
            //do nothing.
        }

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter( inputFilename ));
            out.write( lineContent );
            out.close();
        }
        catch( Exception e ) {
            fail( "error writing content to input Property File in PropertyFileTest.java" );
        }

        BufferedReader bf = pf.open( inputFilename );
        assertNotNull( bf );

        // retrieve the one line we wrote (above), now retrieve another line. Should trigger EOF response
        pf.retrieveNextLine( bf );
        String s = pf.retrieveNextLine( bf );
        assertNull( s );

        try {
            bf.close();
        }
        catch( Exception e) {}
        finally{
            if( f.exists() ) {
                f.delete();
            }
        }
    }
}