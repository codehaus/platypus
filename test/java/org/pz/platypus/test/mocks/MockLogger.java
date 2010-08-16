/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.test.mocks;

import java.util.logging.*;

/**
 * Mock for Java Logger. Simply records the severity level and the message and
 * makes these available via getSeverity() and getMessage() for testing. Also,
 * it captures the Level of the logger. Note: No logging to file, console, or
 * real Java logger occurs.
 *
 * @author alb
 */
public class MockLogger extends Logger
{
    /** the number of times this logger was called */
    private int counter;

    private Level level;
    private Level severity;
    private String lastMessage;

    public MockLogger()
    {
        super( null, null );
        counter = 0;
    }

    @Override
    public void fine( final String msg )
    {
        severity = Level.WARNING;
        lastMessage = msg;
        counter++;
    }

    public int getCounter()
    {
        return( counter );
    }

    /**
     * Returns the level of the logger (that is, the lowest
     * level of messages the logger will log.
     *
     * @return level
     */
    @Override
    public Level getLevel()
    {
        return( level );
    }

    /**
     * Key testing function: Returns the last logged string unchanged.
     *
     * @return the logged message
     */
    public String getMessage()
    {
        return( lastMessage );
    }

    /**
     * Key testing function: Returns the severity of the last logged message.
     *
     * @return severity of last logged message
     */
    public Level getSeverity()
    {
        return( severity );
    }

    @Override
    public void info( final String msg )
    {
        severity = Level.INFO;
        lastMessage = msg;
        counter++;
    }

    @Override
    public void setLevel( final Level newLevel )
    {
        level = newLevel;
    }

    @Override
    public void severe( final String msg )
    {
        severity = Level.SEVERE;
        lastMessage = msg;
        counter++;
    }

    @Override
    public String toString()
    {
        return( lastMessage );
    }


    public void setMessage( String newMsg )
    {
        lastMessage = newMsg;
    }    

    @Override
    public void warning( final String msg )
    {
        severity = Level.WARNING;
        lastMessage = msg;
        counter++;
    }

}
