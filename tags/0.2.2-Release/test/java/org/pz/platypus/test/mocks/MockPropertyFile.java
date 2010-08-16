/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.test.mocks;

import org.pz.platypus.PropertyFile;

/**
 * For testing of PropertyFile features
 *
 * @author alb
 */
public class MockPropertyFile extends PropertyFile
{
    /** the value returned by a lookup() method call */
    private String lookupReturn = null;

    public void lookupShouldReturnNo()
    {
        lookupReturn = "no";
    }

    public void lookupShouldReturnYes()
    {
        lookupReturn = "yes";
    }

    public void lookupShouldReturnNull()
    {
        lookupReturn = null;
    }

    @Override
    public String lookup( final String param )
    {
        return( lookupReturn );
    }
}
