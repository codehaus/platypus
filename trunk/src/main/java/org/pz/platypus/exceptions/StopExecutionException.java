/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.exceptions;

/**
 * Exception used for stopping exception after processing of certain Command Line Options
 *
 * @author atul khot
 */
public class StopExecutionException extends RuntimeException
{
    public StopExecutionException(String errMsg)
    {
        super(errMsg);
    }
}
