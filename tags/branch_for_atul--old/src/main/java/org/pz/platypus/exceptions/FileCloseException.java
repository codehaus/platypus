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
public class FileCloseException extends Exception
{
    public FileCloseException() {};

    public FileCloseException( final String msg )
    {
        super( msg );
    }
}