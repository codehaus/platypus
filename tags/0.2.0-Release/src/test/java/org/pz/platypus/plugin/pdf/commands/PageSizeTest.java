/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 * 
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.*;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.test.mocks.MockLogger;
import org.pz.platypus.test.mocks.MockLiterals;

import java.util.logging.Level;
import java.util.HashMap;

import com.lowagie.text.Rectangle;

/**
 * Test processing of changing page sizes. Also testing pageheight and pagewidth commands.
 *
 * @author alb
 */
public class PageSizeTest
{
    private GDD gdd;
    private PdfData pdd;
    private Token tok;
    private CommandParameter parm;

    @Before
    public void setUp()
    {
        gdd = new GDD();
        gdd.initialize();
        gdd.setLogger( new MockLogger() );
        gdd.setLits( new MockLiterals() );
        gdd.getLogger().setLevel( Level.OFF );
        pdd = new PdfData( gdd );
    }

    @Test
    public void testValidPageWidth()
    {
        // get the starting page width and height
        float startWidth = pdd.getPageWidth();
        float startHeight = pdd.getPageHeight();

        parm = new CommandParameter();
        parm.setAmount( 200f );
        parm.setUnit( UnitType.POINT );
        tok = new Token( new Source(), TokenType.COMMAND, "[pagewidth:", "[pagewidth:200]", parm );

        PdfPageWidth ppw = new PdfPageWidth();
        ppw.process( pdd, tok, 2 );

        assertFalse( startWidth == pdd.getPageWidth() );
        assertEquals( startHeight, pdd.getPageHeight(), 0.5f );
        assertEquals( 200f, pdd.getPageWidth(), 0.5f );
    }

    @Test
    public void testInvalidPageWidth()  // Invalid width should result in no change to page size
    {
        // get the starting page width and height
        float startWidth = pdd.getPageWidth();
        float startHeight = pdd.getPageHeight();

        parm = new CommandParameter();
        parm.setAmount( 1f );
        parm.setUnit( UnitType.POINT );
        tok = new Token( new Source(), TokenType.COMMAND, "[pagewidth:", "[pagewidth:1]", parm );

        PdfPageWidth ppw = new PdfPageWidth();
        ppw.process( pdd, tok, 2 );

        assertEquals( startWidth, pdd.getPageWidth(), 0.5f );
        assertEquals( startHeight, pdd.getPageHeight(), 0.5f );
    }

    @Test
    public void testValidPageHeight()
    {
        // get the starting page width and height
        float startWidth = pdd.getPageWidth();
        float startHeight = pdd.getPageHeight();

        parm = new CommandParameter();
        parm.setAmount( 200f );
        parm.setUnit( UnitType.POINT );
        tok = new Token( new Source(), TokenType.COMMAND, "[pageheight:", "[pageheight:200]", parm );

        PdfPageHeight pph = new PdfPageHeight();
        pph.process( pdd, tok, 2 );

        assertEquals( startWidth, pdd.getPageWidth(), 0.5f );
        assertEquals( 200f, pdd.getPageHeight(), 0.5f );
        assertFalse( startHeight == pdd.getPageHeight() );
    }

    @Test
    public void testInvalidPageHeight()  // invalid height should result in no change to page size
    {
        // get the starting page width and height
        float startWidth = pdd.getPageWidth();
        float startHeight = pdd.getPageHeight();

        parm = new CommandParameter();
        parm.setAmount( 200000f );
        parm.setUnit( UnitType.POINT );
        tok = new Token( new Source(), TokenType.COMMAND, "[pageheight:", "[pageheight:200000]",
                         parm );

        PdfPageHeight pph = new PdfPageHeight();
        pph.process( pdd, tok, 2 );

        assertEquals( startWidth, pdd.getPageWidth(), 0.5f );
        assertEquals( startHeight, pdd.getPageHeight(), 0.5f );
    }

    @Test
    public void testCreationOfPageSizeTable()
    {
        PdfPageSize pps = new PdfPageSize();
        HashMap<String, Rectangle> psTable = pps.createPageSizeLookupTable();
        assert( psTable.size() > 30 );
    }

    @Test
    public void testContainsSpecificPageSizes()
    {
        PdfPageSize pps = new PdfPageSize();
        HashMap<String, Rectangle> psTable = pps.createPageSizeLookupTable();
        assertNotNull( psTable.get( "A4" ));
        assertNotNull( psTable.get( "LETTER" ));
    }

    @Test
    public void testProcessingOfToken()
    {
        parm = new CommandParameter();
        parm.setString( "ANSI-E" );
        tok = new Token( new Source(), TokenType.COMMAND, "[pagesize:", "[pagesize:ANSI-E]", parm );

        PdfPageSize pps = new PdfPageSize();
        pps.process( pdd, tok, 2 );
        assertEquals( 2448f, pdd.getPageWidth(), 0.5f );
        assertEquals( 3168f, pdd.getPageHeight(), 0.5f );
    }

    @Test
    public void testProcessingOfNullPageSize()
    {
        parm = new CommandParameter(); // this will result in a parameter with a null contents

        // get the starting page width and height
        float startWidth = pdd.getPageWidth();
        float startHeight = pdd.getPageHeight();

        tok = new Token( new Source(), TokenType.COMMAND, "[pagesize:", "[pagesize:]", parm );

        PdfPageSize pps = new PdfPageSize();
        pps.process( pdd, tok, 2 );

        // when a null page size is specified, it should be ignored and the page size
        // should remain unchanged.
        assertEquals( startWidth, pdd.getPageWidth(), 0.5f );
        assertEquals( startHeight, pdd.getPageHeight(), 0.5f );
    }

    @Test
    public void testProcessingOfInvalidPageSize()
    {
        parm = new CommandParameter(); // this will result in a parameter with a null contents
        parm.setString( "NOT_VALID" );
        // get the starting page width and height
        float startWidth = pdd.getPageWidth();
        float startHeight = pdd.getPageHeight();

        tok = new Token( new Source(), TokenType.COMMAND, "[pagesize:", "[pagesize:NOT_VALID]",
                         parm );

        PdfPageSize pps = new PdfPageSize();
        pps.process( pdd, tok, 2 );

        // when an invalid page size is specified, it should be ignored and the page size
        // should remain unchanged.
        assertEquals( startWidth, pdd.getPageWidth(), 0.5f );
        assertEquals( startHeight, pdd.getPageHeight(), 0.5f );
    }
}