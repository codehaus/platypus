/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-10 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.pz.platypus.test.mocks.MockLiterals;

import java.util.MissingResourceException;
import java.util.logging.LogRecord;
import java.util.logging.Level;


public class LogFormatterTest
{
    private LogFormatter logFormatter;
    
    @Before
    public void setUp() 
    {
        try {
            logFormatter = new LogFormatter( new MockLiterals() );
        }
        catch ( MissingResourceException mre ) {
            fail( "Could not load Literals file ");
        }
    }
    
    @Test
    public void testSevereFormatter()
    {
        final LogRecord logRecSevere = new LogRecord( Level.SEVERE, "Test of Severe" );
        final String str = logFormatter.format( logRecSevere );
        assertEquals( str, "Error: Test of Severe\n");
    }

    @Test
    public void testWarningFormatter()
    {
        final LogRecord logRecWarning = new LogRecord( Level.WARNING, "Test of Warning" );
        final String str = logFormatter.format( logRecWarning );
        assertEquals( str, "Warning: Test of Warning\n");
    }

    @Test
    public void testInfoFormatter()
    {
        final LogRecord logRecInfo = new LogRecord( Level.INFO, "Test of Info" );
        final String str = logFormatter.format( logRecInfo );
        assertEquals( str, "Test of Info\n");
    }

    @Test
    public void testFinestFormatter()
    {
        final LogRecord logRecFinest = new LogRecord( Level.FINEST, "Test of Finest" );
        final String str = logFormatter.format( logRecFinest );
        assertEquals( str, "Test of Finest\n");
    }

    @Test
    public void testNullInFormatter()
    {
        final String str = logFormatter.format( null );
        assertEquals( str, "\n");
    }
}
