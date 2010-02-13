/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.*;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.DefaultValues;
import org.pz.platypus.test.mocks.MockLogger;
import org.pz.platypus.test.mocks.MockLiterals;

import java.util.logging.Level;

/**
 * Test processing of changing paragraph leading.
 *
 * @author alb
 */
public class LeadingTest
{
    private GDD gdd;
    private PdfData pdd;

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
    public void testInitialLeading()
    {
        float leading = pdd.getLeading();
        int   leadingLine = pdd.getLeadingLine().getLineNumber();

        assertEquals( DefaultValues.LEADING, leading, 0.5f );
        assertEquals( 0, leadingLine );
    }

    @Test
    public void testValidLeadingChanges()
    {
        // set leading size
        CommandParameter cp = new CommandParameter();
        cp.setAmount( 40f );
        cp.setUnit( UnitType.POINT );

        // reset leading using [leading: command
        new PdfLeading().process( pdd,
                    new Token( new Source( 5, 6 ), TokenType.COMMAND, "[leading:", "[leading:", cp ),
                    23 );

        assertEquals( 40f, pdd.getLeading(), 0.5f );
        assertEquals( 6, pdd.getLeadingLine().getLineNumber() );
    }

    @Test
    public void testInvalidLeadingOfLessThanZero()
    {

        // set leading to invalid size (-25pts)
        CommandParameter cp = new CommandParameter();
        cp.setAmount( -25f );
        cp.setUnit( UnitType.POINT );

        // process the [leading: commmand
         new PdfLeading().process( pdd,
                    new Token( new Source( 5, 6 ), TokenType.COMMAND, "[leading:", "[leading:", cp ),
                    23 );

        //Make sure an error message was generated
        MockLogger ml = (MockLogger) gdd.getLogger();
        assertTrue( ml.getMessage().contains( "ERROR.INVALID_LEADING" ));
        ml.setMessage( null );
        assertNull( ml.getMessage() );

        //Make sure the invalid value did not actually change the leading setting.
        assertEquals( DefaultValues.LEADING, pdd.getLeading(), 0.5f );
        assertEquals( 0, pdd.getLeadingLine().getLineNumber() );
    }


    @Test
    public void testInvalidLeadingGreaterThanMax()
    {
        assertEquals( DefaultValues.LEADING, pdd.getLeading(), 0.5f );

        // set leading to invalid size ( the page height )
        CommandParameter cp = new CommandParameter();
        cp.setAmount( pdd.getPageHeight());
        cp.setUnit( UnitType.POINT );

        // run the [leading: command
        new PdfLeading().process( pdd,
                    new Token( new Source(5, 10), TokenType.COMMAND, "[leading:", "[leading:", cp ),
                    23 );

        //Make sure an error message was generated
        MockLogger ml = (MockLogger) gdd.getLogger();
        assertTrue( ml.getMessage().contains( "ERROR.INVALID_LEADING" ));
        ml.setMessage( "" );
        assertEquals( "", ml.getMessage() );

        //Make sure the invalid value did not actually change the leading setting.
        assertEquals( DefaultValues.LEADING, pdd.getLeading(), 0.5f );
        assertEquals( 0, pdd.getLeadingLine().getLineNumber() );
    }
}