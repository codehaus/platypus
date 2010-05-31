/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.*;
import org.pz.platypus.plugin.pdf.Columns;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.test.mocks.MockLiterals;
import org.pz.platypus.test.mocks.MockLogger;
import org.pz.platypus.test.mocks.MockPdfOutfile;

import java.util.logging.Level;

/**
 * Test processing of bullet list with options specified. PDF plugin.
 *
 * @author alb
 */
public class PdfBulletListStartWithOptionsTest
{
    private GDD gdd;
    private PdfData pdd;
    private Token tok;
    private CommandParameter parm;
    private PdfBulletListPlainStartWithOptions bl;
    private MockPdfOutfile outfile = new MockPdfOutfile();

    @Before
    public void setUp()
    {
        gdd = new GDD();
        gdd.initialize();
        gdd.setLogger( new MockLogger() );
        gdd.setLits( new MockLiterals() );
        gdd.getLogger().setLevel( Level.OFF );
        pdd = new PdfData( gdd );
        pdd.setColumns( new Columns( pdd ));

        pdd.setOutfile( outfile );
        bl = new PdfBulletListPlainStartWithOptions();
    }

    @Test
    public void testStartNewListInvalid()
    {
        parm = new CommandParameter();
        parm.setString( "billet:{asterisk}]" );  // invalid b/c does not start with: bullet:
        tok = new Token( new Source(), TokenType.COMMAND, "[list|", "[list|", parm );

        bl.startNewList( pdd, tok, 6 );

        assertEquals( DefaultValues.BULLET, outfile.getContent() );
    }

    @Test
    public void testStartNewListValidWithCharSpecified()
    {
        parm = new CommandParameter();
        parm.setString( "bullet:>>]" ); // the bullet is the string >>
        tok = new Token( new Source(), TokenType.COMMAND, "[list|", "[list|", parm );

        bl.startNewList( pdd, tok, 6 );

        assertEquals( ">>", outfile.getContent() );
    }
}