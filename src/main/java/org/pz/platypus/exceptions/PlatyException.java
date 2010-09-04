/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.exceptions;

import org.pz.platypus.interfaces.IException;

/**
 * Basic template for all Platypus-specific runtime exceptions
 *
 * @author alb
 */
public class PlatyException extends RuntimeException implements IException
{
    /** a string to look up in Literals() that identifies the nature of the exception */
    private String explanation;

    /** any method-specific string that adds further info about the location or context of
     *  the exception;
     */
    private String location;

    /**
     * @param explain literal to look up in Literals that explains the problem
     */
    public PlatyException( final String explain )
    {
        explanation = explain;
        location = null;
    }

    /**
     * The most common form of constructor
     * 
     * @param explain literal to look up in Literals that explains the problem
     * @param locate any additional info that might indicate location
     */
    public PlatyException( final String explain, final String locate )
    {
        explanation = explain;
        location = locate;
    }

    public String getExplanation()
    {
        return( explanation );
    }

    public String getLocation()
    {
        return( location );
    }
}
