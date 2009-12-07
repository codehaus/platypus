/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.interfaces;

/**
 * Platypus-specific exceptions are passed two strings: the key to look up the error message
 * in Literals(), and some info on where the problem occurred.
 *
 * This interface defines access methods for both.
 *
 * @author alb
 */
public interface Exceptionable
{
    public String getExplanation();

    public String getLocation();
}
