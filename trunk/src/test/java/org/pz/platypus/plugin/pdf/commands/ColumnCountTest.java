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
import org.pz.platypus.plugin.pdf.Columns;
import org.pz.platypus.plugin.pdf.PdfOutfile;
import org.pz.platypus.plugin.pdf.commands.PdfColumnCount;
import org.pz.platypus.test.mocks.MockLogger;
import org.pz.platypus.test.mocks.MockLiterals;
import org.pz.platypus.interfaces.OutputContextable;

import java.util.logging.Level;

import com.lowagie.text.pdf.ColumnText;

/**
 * Test processing of changing column counts
 *
 * @author alb
 */
public class ColumnCountTest
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
        gdd.initialize();
        gdd.setLogger( new MockLogger() );
        gdd.setLits( new MockLiterals() );
        gdd.getLogger().setLevel( Level.OFF );
        pdd = new PdfData( gdd );
        pdd.setColumns( new Columns( pdd ));
        parm = new CommandParameter();
        parm.setAmount( 4f );
        parm.setUnit( UnitType.NONE );
        tok = new Token( new Source(1,1), TokenType.COMMAND, "[columns:", "[columns:4]", parm );

    }

    @Test (expected = IllegalArgumentException.class )
    public void invalidTokenParameters()
    {
        PdfColumnCount pcc = new PdfColumnCount();
        pcc.process( pdd, null, 3 );
    }

    @Test (expected = IllegalArgumentException.class )
    public void invalidPdfDataParameters()
    {
        PdfColumnCount pcc = new PdfColumnCount();
        pcc.process( null, tok, 3 );
    }
    @Test
    public void validSetUpNewCols3()
    {
        PdfColumnCount pcc = new PdfColumnCount();
        PdfOutfile outf = new PdfOutfile();
        outf.setItColumn( new ColumnText( null ));
        pdd.setOutfile( outf  );
        pcc.setupNewColumns( 3, pdd );
        assertEquals( 3, pdd.getColumnCount() );
    }

    @Test
    public void computeVerticalSkipTest()
    {
        PdfColumnCount pcc = new PdfColumnCount();

        Source s = new Source();
        pdd.setPageHeight( 720f, s );
        pdd.setMarginTop( 36f, s );
        assertEquals( 684f, pcc.computeVerticalSkip( 0f, pdd ), 0.01f );
    }

}