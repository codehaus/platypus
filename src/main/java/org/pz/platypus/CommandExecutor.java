/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2011 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

/**
 * The basic abstract class for command execution in the plugins
 *
 * @author alb
 */
public abstract class CommandExecutor
{
    protected String root;

    /**
     * Prints an error message via the GDD error logging facility.
     * @param msg string of the message
     * @param t   token where the error occurred
     * @param gdd contains the logger
     */
    protected void errMsg( final String msg, final Token t, final GDD gdd )
    {
        assert( msg != null );
        assert( t != null );
        assert( gdd != null );

        gdd.logWarning( gdd.getLit( "FILE" ) + t.getSource().getFileNumber() + " " +
                        gdd.getLit( "LINE" ) + t.getSource().getLineNumber() + " " +
                        msg + " " +
                        gdd.getLit( "IGNORED" ));
    }

    /**
     * get the root of the command. For example, [fsize: in [fsize:12pt]
     *
     * @return the root as a string
     */
    public String getRoot()
    {
        return( root );
    }
}
