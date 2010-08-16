/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

/**
 * The possible types of argument a command can have. A command can have only one of these.
 *
 * @author alb
 */
public enum ParamType
{
    NONE,
    NUMBER,
    MEASURE,      // such as: 12pt or 7in, etc.
    STRING;     
}
