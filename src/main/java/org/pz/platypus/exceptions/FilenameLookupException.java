/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.exceptions;

/**
 * Exception generated when user tries to look up a non-existent file in
 * FileList (either by name or by number)
 *
 * @author alb
 */
public class FilenameLookupException extends Exception
{
    public FilenameLookupException() {};

    public FilenameLookupException( final String msg )
    {
        super( msg );
    }
}