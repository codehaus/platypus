/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 * 
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.*;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.plugin.pdf.PdfOutfile;
import org.pz.platypus.test.mocks.MockLogger;
import org.pz.platypus.test.mocks.MockLiterals;
import org.pz.platypus.test.mocks.MockPdfOutfile;


import java.util.logging.Level;

/**
 * Test processing of a new bullet list
 *
 * @author alb
 */
public class PdfBulletListPlainEndTest
{
    private GDD gdd;
    private PdfData pdd;
    private Token tok;
    private CommandParameter parm;
    private PdfBulletListPlainStart bulletList;
    private PdfBulletListPlainEnd bulletListEnd;

    @Before
    public void setUp()
    {
        gdd = new GDD();
        gdd.initialize();
        gdd.initialize();
        gdd.setLogger( new MockLogger() );
        gdd.setLits( new MockLiterals() );
        gdd.getLogger().setLevel( Level.OFF );
        pdd = new PdfData( gdd );
        bulletList = new PdfBulletListPlainStart();
        bulletListEnd = new PdfBulletListPlainEnd();
        parm = new CommandParameter();
        parm.setAmount( 4f );
        parm.setUnit( UnitType.NONE );
        tok = new Token( new Source(1,1), TokenType.COMMAND, "[-list]", "[-list]", parm );
    }

    @Test
    public void testConstructor()
    {
        assertEquals( "[-list]", bulletListEnd.getRoot() );
    }

    @Test(expected = IllegalArgumentException.class )
    public void invalidTokenParameters()
    {
        pdd.setOutfile( null );
        bulletListEnd.process( pdd, tok, 3 );
    }

    @Test(expected = IllegalArgumentException.class )
    public void invalidNullParameters()
    {
        MockPdfOutfile mockOutf = new MockPdfOutfile();
        PdfOutfile outfile = (PdfOutfile) mockOutf;
        pdd.setOutfile( outfile  );
        assertEquals( 0, bulletListEnd.process( pdd, null, 0 ));
    }

    @Test
    public void validProcess()
    {
        MockPdfOutfile mockOutf = new MockPdfOutfile();
        PdfOutfile outfile = (PdfOutfile) mockOutf;
        pdd.setOutfile( outfile  );
        assertEquals( 0, bulletListEnd.process( pdd, tok, 0 ));
    }

    @Test
    public void validProcessWithFollowingTokenIscr()
    {
        MockPdfOutfile mockOutf = new MockPdfOutfile();
        PdfOutfile outfile = (PdfOutfile) mockOutf;
        pdd.setOutfile( outfile  );

        TokenList tl = new TokenList();
        tl.add( tok );

        Token tok2 = new Token( new Source(1,1), TokenType.COMMAND, "[cr]", "[cr]", null );
        tl.add( tok2 );

        gdd.setInputTokens( tl );

        assertEquals( 1, bulletListEnd.process( pdd, tok, 0 ));
    }

    @Test
    public void validProcessWithFollowingTokensArecrCR()
    {
        MockPdfOutfile mockOutf = new MockPdfOutfile();
        PdfOutfile outfile = (PdfOutfile) mockOutf;
        pdd.setOutfile( outfile  );

        TokenList tl = new TokenList();
        tl.add( tok );

        Token tok2 = new Token( new Source(1,1), TokenType.COMMAND, "[cr]", "[cr]", null );
        tl.add( tok2 );

        Token tok3 = new Token( new Source(1,1), TokenType.COMMAND, "[CR]", "[CR]", null );
        tl.add( tok3 );

        gdd.setInputTokens( tl );

        assertEquals( 2, bulletListEnd.process( pdd, tok, 0 ));
    }

    @Test
    public void validProcessWithFollowingTokensArecrTEXT()
    {
        MockPdfOutfile mockOutf = new MockPdfOutfile();
        PdfOutfile outfile = (PdfOutfile) mockOutf;
        pdd.setOutfile( outfile  );

        TokenList tl = new TokenList();
        tl.add( tok );

        Token tok2 = new Token( new Source(1,1), TokenType.COMMAND, "[cr]", "[cr]", null );
        tl.add( tok2 );

        Token tok3 = new Token( new Source(1,1), TokenType.TEXT, "hello", "hello", null );
        tl.add( tok3 );

        gdd.setInputTokens( tl );

        assertEquals( 1, bulletListEnd.process( pdd, tok, 0 ));
    }

    @Test
    public void validProcessWithFollowingTokensAreTwoNonCrCommands()
    {
        MockPdfOutfile mockOutf = new MockPdfOutfile();
        PdfOutfile outfile = (PdfOutfile) mockOutf;
        pdd.setOutfile( outfile  );

        TokenList tl = new TokenList();
        tl.add( tok );

        parm.setAmount( 12f );
        parm.setUnit( UnitType.POINT );
        Token tok2 = new Token( new Source(1,1), TokenType.COMMAND, "[fsize:12pt]", "[fsize:12pt]", parm );
        tl.add( tok2 );

        Token tok3 = new Token( new Source(1,1), TokenType.COMMAND, "[fsize:12pt]", "[fsize:12pt]", parm );
        tl.add( tok3 );

        gdd.setInputTokens( tl );

        assertEquals( 0, bulletListEnd.process( pdd, tok, 0 ));
    }
}