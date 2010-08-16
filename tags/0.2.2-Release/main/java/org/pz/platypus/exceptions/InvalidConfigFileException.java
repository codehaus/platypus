/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.exceptions;

/**
 * Exception generated when user tries to open an non-existent or invalid config file
 *
 * @author alb
 */
public class InvalidConfigFileException extends PlatyException
{
    public InvalidConfigFileException( final String msg )
    {
        super( msg );
    }

    public InvalidConfigFileException( final String msg, final String msg2 )
    {
        super( msg, msg2 );
    }
}