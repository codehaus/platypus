/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.groovyTests;

import groovy.util.GroovyTestCase;

/**
 * <purpose goes here>
 *
 * @author alb
 */
public class GroovyTest extends GroovyTestCase
{
    void testSimpleGroovyTest()
    {
        def lowerCaseRange = 'a'..'z'
        assert lowerCaseRange.size() == 26
    }
}
