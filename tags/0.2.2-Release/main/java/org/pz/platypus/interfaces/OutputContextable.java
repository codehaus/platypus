/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.interfaces;

import org.pz.platypus.GDD;
import org.pz.platypus.Source;

/**
 * Interface for Output context info
 *
 * @author alb
 */
public interface OutputContextable
{
    public GDD getGdd();

    boolean inCodeSection();

    void    setLineNumberLast( int lineNum, Source s);
    void    setLineNumberSkip( int skipSize, Source s );
}
