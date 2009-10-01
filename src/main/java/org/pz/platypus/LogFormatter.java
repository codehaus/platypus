/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2007-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */
package org.pz.platypus;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Converts the default logger format to something more user friendly.
 * 
 * Creates a formatter for logging records. This format prints just the log message, preceded
 * in the case of severe or warning errors by the words "Error: " or "Warning: " respectively.
 * The date and time and the two-line format of the default Java console logger are thereby
 * removed.
 *
 * @author alb
 * Note: adapted from The Java Developers Almanac 1.4 (adaptation example e400).
 *       www.exampledepot.com/egs/java.util.logging/CustomFormat.html
 */

public class LogFormatter extends Formatter
{
    /** for Literals access */
    private final Literals lits;

    public LogFormatter( final Literals literals )
    {
        lits = literals;
    }

    /**
     * The principal function: it accepts a LogRecord and returns the string that should
     * be written out by the logger. It translates the Level.Severe status into an "Error"
     * message and Level.Warning LogRecords into "Warning" messages.
     *
     * @param record the LogRecord (which contains the severity and the string to log)
     * @return the string to log. If null is passed in (an error), returns a newline.
     */
    @Override
    public String format( final LogRecord record )
    {
        if ( record == null )
        {
            return( "\n" );
        }

        final int LOG_MSG_MAX = 1000;
        final StringBuffer buf = new StringBuffer( LOG_MSG_MAX );

        if ( record.getLevel().intValue() == Level.SEVERE.intValue() ) {
            buf.append( lits.getLit( "ERROR_COLON" )).append( ' ' );
        }
        else
        if ( record.getLevel().intValue() == Level.WARNING.intValue() ) {
            buf.append( lits.getLit( "WARNING_COLON" )).append( ' ' );
        }

        buf.append( formatMessage(record));
        buf.append( '\n' );
        return buf.toString();
    }
}
