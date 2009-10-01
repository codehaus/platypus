/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.commandline;

import org.pz.platypus.*;
import java.util.TreeMap;

/**
 * Maintains a tree of all the soptions and arguments specified on the command line.
 * 
 * @author alb
 */
public class SpecifiedOptions
{
    /** a tree containing the options args specified on the command line */
    private final TreeMap<String, String> specifiedOptions;

    /**
     * Create the tree and load the currently supported options. As new options are added
     * to Platypus, they should be entered here.
     */
    public SpecifiedOptions()
    {
        specifiedOptions = new TreeMap<String, String>();
    }    

    /**
     * Add a switch-value pair to the tree of possible command-line arguments
     * @param key the switch
     * @param value the value
     * @throws NullPointerException
     * @throws ClassCastException 
     * @return Status.OK if all went well, otherwise, error code
     */
    public int add( final String key, final String value ) throws
        NullPointerException, ClassCastException
    {
        try {
            specifiedOptions.put( key, value );
        } 
        catch ( NullPointerException e ) {
            return( Status.INVALID_PARAM_NULL );
        } 
        catch ( ClassCastException e ) {
            return( Status.INVALID_PARAM );
        }
        return( Status.OK );
    }

    /**
     * See whether the option is in the "official" list of supported options
     * @param argToLookup the command-line option to look up
     * @return true if found, false otherwise
     */
    public boolean containsArg( final String argToLookup )
    {
        return( specifiedOptions.containsKey( argToLookup ));
    }

    /**
     * Get the number of arguments a specific supported option should have
     * @param optionToLookup the option that has arguments
     * @return the count of arguments for the option
     */
    public String getArg( final String optionToLookup )
    {
        return( specifiedOptions.get( optionToLookup ));
    }
}
