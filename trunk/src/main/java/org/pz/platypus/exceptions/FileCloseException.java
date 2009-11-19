/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.exceptions;

/**
 *
 * @author alb
 */
public class FileCloseException extends PlatyException
{
    public FileCloseException( final String msg )
    {
        super( "Error closing file: " + msg );
    }

    public FileCloseException( final String msg1, final String msg2 )
    {
        super( msg1, msg2 );
    }
}