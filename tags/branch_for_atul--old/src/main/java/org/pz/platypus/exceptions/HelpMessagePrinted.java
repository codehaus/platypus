/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.exceptions;

/**
 * Exception generated when the user places -help on the command line.
 * It is responsible for stopping further processing after the usage
 * message has been written to stdout.
 *
 * @author alb
 */
public class HelpMessagePrinted extends Exception
{
    public HelpMessagePrinted() {};

    public HelpMessagePrinted( final String msg )
    {
        super( msg );
    }
}
