/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-10 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.exceptions;

/**
 * Exception generated when user tried to specify an invalid EOL treatment
 * (see EolTreament class)
 *
 * @author alb
 */
public class EolTreatmentException extends PlatyException
{
    public EolTreatmentException( final String msg )
    {
        super( msg );
    }
}