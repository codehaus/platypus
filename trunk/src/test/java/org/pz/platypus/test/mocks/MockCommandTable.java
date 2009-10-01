/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.test.mocks;

import org.pz.platypus.*;
import org.pz.platypus.interfaces.Commandable;

import java.util.MissingResourceException;
import java.util.HashMap;

/**
 * Implements a Mock Command Table that loads nothing into the table
 *
 * @author alb
 */
public class MockCommandTable extends CommandTable
{
    public MockCommandTable( GDD gdd )
    {
        commandTable = new HashMap<String, Commandable>( 300 );
    }
    
    /**
     * Load nothing into the table.
     */
    @Override
    public void load()
    {
        return;
    }

}