/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-10 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import org.junit.Test;

import java.io.IOException;
import java.io.BufferedReader;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 *
 * @author alb
 */
public class PropertyFileTest
{

//    private Literals lits;
    private GDD gdd;
    private PropertyFile pf;

    @Test
    public void testConstructor()
    {
        pf = new PropertyFile();
        HashMap<String,String>contents = (HashMap<String,String>) pf.getContents();
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
        HashMap<String,String>contents = (HashMap<String,String>) pf.getContents();
        assertEquals( 0, pf.getSize() );

        pf.loadLine( "platypus=typesetting" );
        assertEquals( 1, pf.getSize() );
        assertEquals( "typesetting", pf.lookup( "platypus"));
    }

    @Test
    public void testForInvalidLineLoaded()   //
    {
        pf = new PropertyFile();
        HashMap<String,String>contents = (HashMap<String,String>) pf.getContents();
        assertEquals( 0, pf.getSize() );

        pf.loadLine( "platypus=typesetting" );
        assertEquals( 1, pf.getSize() );
        assertEquals( null, pf.lookup( "no-such-record"));
    }

    @Test
    public void testOpenReaderInvalidFile()
    {
        pf = new PropertyFile();
        HashMap<String,String>contents = (HashMap<String,String>) pf.getContents();
        BufferedReader bf = pf.open( "no such file ");
        assertEquals( null, bf );
    }

//    @Test(expected=IOException.class)
//    public void testOpenOfInvalidFile() throws IOException
//    {
//        pf = new PropertyFile( "this file does not exist", gdd );
//        pf.load();
//    }

}