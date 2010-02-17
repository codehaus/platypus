/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.rtf.commands;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.*;
import org.pz.platypus.plugin.rtf.RtfData;
import org.pz.platypus.plugin.rtf.RtfOutfile;

/**
 * Test functioning of four margin commands in the RTF plugin
 */
public class MarginsTest
{
    private GDD gdd;
    private RtfData rtd;

    private RtfMarginBottom bM;
    private RtfMarginLeft   lM;
    private RtfMarginRight  rM;
    private RtfMarginTop    tM;

    @Before
    public void setUp()
    {
        gdd = new GDD();
        gdd.initialize();
        rtd = new RtfData( gdd );
    }

    @Test
    public void testConstructor()
    {
        bM = new RtfMarginBottom();
        assertEquals( "[bmargin:", bM.getRoot() );

        lM = new RtfMarginLeft();
        assertEquals( "[lmargin:", lM.getRoot() );

        rM = new RtfMarginRight();
        assertEquals( "[rmargin:", rM.getRoot() );

        tM = new RtfMarginTop();
        assertEquals( "[tmargin:", tM.getRoot() );
    }

    @Test( expected = IllegalArgumentException.class )
    public void handlingNullParametersBottom()
    {
        bM = new RtfMarginBottom();
        bM.process( null, null, 25 );
        bM.process( rtd, null, 25 );
    }

    @Test( expected = IllegalArgumentException.class )
    public void handlingNullParametersLeft()
    {
        lM = new RtfMarginLeft();
        lM.process( null, null, 25 );
        lM.process( rtd, null, 25 );
    }

    @Test( expected = IllegalArgumentException.class )
    public void handlingNullParametersRight()
    {
        rM = new RtfMarginRight();
        rM.process( null, null, 25 );
        rM.process( rtd, null, 25 );
    }

    @Test( expected = IllegalArgumentException.class )
    public void handlingNullParametersTop()
    {
        tM = new RtfMarginTop();
        tM.process( null, null, 25 );
        tM.process( rtd, null, 25 );
    }

    @Test
    public void testThatRtfDataIsUpdatedForBottomMargin()
    {
        rtd.setOutfile( new RtfOutfile( "abc", rtd ));
        assertTrue( rtd.getMarginBottom() != 144.0f );
        int tokNumber = 6;

        Token tok = createMarginToken( "[bmargin", 144.0f );
        bM = new RtfMarginBottom();
        bM.process( rtd, tok, tokNumber );
        assertEquals( 144.0f, rtd.getMarginBottom(), 0.05f );
        assertEquals( 42, rtd.getMarginBottomLine().getLineNumber() );
    }

    @Test
    public void testThatRtfDataIsUpdatedForLeftMargin()
    {
        rtd.setOutfile( new RtfOutfile( "abc", rtd ));
        assertTrue( rtd.getMarginLeft() != 144.0f );
        int tokNumber = 6;

        Token tok = createMarginToken( "[lmargin", 144.0f );
        lM = new RtfMarginLeft();
        lM.process( rtd, tok, tokNumber );
        assertEquals( 144.0f, rtd.getMarginLeft(), 0.05f );
        assertEquals( 42, rtd.getMarginLeftLine().getLineNumber() );
    }

    @Test
    public void testThatRtfDataIsUpdatedForRightMargin()
    {
        rtd.setOutfile( new RtfOutfile( "abc", rtd ));
        assertTrue( rtd.getMarginRight() != 144.0f );
        int tokNumber = 77;

        Token tok = createMarginToken( "[rmargin", 144.0f );
        rM = new RtfMarginRight();
        rM.process( rtd, tok, tokNumber );
        assertEquals( 144.0f, rtd.getMarginRight(), 0.05f );
        assertEquals( 42, rtd.getMarginRightLine().getLineNumber() );
    }

    @Test
    public void testThatRtfDataIsUpdatedForTopMargin()
    {
        rtd.setOutfile( new RtfOutfile( "abc", rtd ));
        assertTrue( rtd.getMarginTop() != 144.0f );
        int tokNumber = 99;

        Token tok = createMarginToken( "[tmargin", 144.0f );
        tM = new RtfMarginTop();
        tM.process( rtd, tok, tokNumber );
        assertEquals( 144.0f, rtd.getMarginTop(), 0.05f );
        assertEquals( 42, rtd.getMarginTopLine().getLineNumber() );
    }

    private Token createMarginToken( final String root, final float width )
    {
        CommandParameter cp = new CommandParameter();
        cp.setAmount( 144.0f );
        cp.setUnit( UnitType.POINT );
        Token token = new Token( new Source( 1, 42 ), TokenType.COMMAND, root, root, cp );
        return( token );
    }
}