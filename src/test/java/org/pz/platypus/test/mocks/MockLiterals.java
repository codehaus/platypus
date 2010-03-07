/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.test.mocks;

import org.pz.platypus.*;

import java.util.MissingResourceException;

/**
 * Implements a Mock Literal file. Actions can be specified by setting various variables before
 * calling this mock.
 * 
 * @author alb
 */
public class MockLiterals extends Literals 
{
    /** should this mock throw an exception when the constructor is called? */
    private boolean throwExceptionOnConstructor = false;

    /** should calling getLit() throw an exception? */
    private boolean throwExceptionOnGetLit = false;

    /** should getLit() find the lookup key? */
    private boolean getLitShouldReturnKey = true;

    /** should getLit() return the value for the lookup key? Use only if previously a k-v pair was loaded. */
    private boolean getLitShouldReturnValue = false;

    /** what getting VERSION should return */
    private String getLitVersionShouldReturn = null;

    public MockLiterals()
    {
    }

    public MockLiterals( final String filename )
    {
        if( throwExceptionOnConstructor ) {
            throw new MissingResourceException( null, null, null );
        }
    }
    
    /**
     * Can do one of three things depending on the settings of booleans.
     * 1) Return a valid string (the search key). This is the default behavior.
     * 2) Return a single space, which is the behavior when the string lookup fails
     * 3) Throw a MissingResourceException, which would happen only if Lits were not opened.
     * @param key
     * @return a string or a blank depending on various switches regarding behavior.
     */
    @Override
    public String getLit( final String key ) 
    {
        if( throwExceptionOnGetLit ) {
            throw new MissingResourceException( null, null, null );
        }
        else if( key.equals( "VERSION" ) && getLitVersionShouldReturn != null ) {
            return( getLitVersionShouldReturn );
        }
        else if( getLitShouldReturnKey ) {
            return( key );
        }
        else if( getLitShouldReturnValue ) {
            return( super.getLit( key ));
        }
        else {
            return( " " );
        }
    }

    public void setGetLitShouldReturnKey( final boolean yesOrNo )
    {
        getLitShouldReturnKey = yesOrNo;
        if( yesOrNo ) {
            getLitShouldReturnValue = false;
        }
    }

    public void setGetLitShouldReturnValue( final boolean yesOrNo )
    {
        getLitShouldReturnValue = yesOrNo;
        if( yesOrNo ) {
            getLitShouldReturnKey = false;
        }
    }

    public void setThrowExceptionOnConstructor( final boolean yesOrNo )
    {
        throwExceptionOnConstructor = yesOrNo;
    }

    public void setThrowExceptionOnGetLit( final boolean yesOrNo )
    {
        throwExceptionOnGetLit = yesOrNo;
    }

    public void setVersionNumberToReturn( final String version )
    {
        getLitVersionShouldReturn = version;
    }
}
