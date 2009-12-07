/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 *
 */

package org.pz.platypus.exceptions;

/**
 * Exception generated during command-line parsing
 *
 * @author atul khot
 */

public class InvalidInputException extends IllegalArgumentException
{
    private final int status;

    public InvalidInputException(String s, int status)
    {
        super(s);
        this.status = status;
    }

    public int getStatus()
    {
        return status;
    }
}