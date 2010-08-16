/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import org.pz.platypus.interfaces.Commandable;
import org.pz.platypus.utilities.ProcessSymbol;

/**
 * Entry in command table for all symbols and special characters (such as foreign characters)
 *
 * @author alb
 */
public class Symbol implements Commandable
{
    /** what kind of parameter does this command take, if any? */
    protected ParamType parameterType;

    /** the command root */
    protected String root;

    /** the substitute root, if this command is shorthand for another */
    protected String rootSubstitute;

    /** is this command executed in a listing or code section? */
    protected boolean validInCode = true;

    public Symbol( final String symRoot )
    {
        root = symRoot;
    }

    /**
     * get the root of the command/symbol. For symbols, which don't take parameters, it's the
     * whole symbol.
     *
     * @return the root as a string
     */
    public String getRoot()
    {
        return( root );
    }

    /**
     * required by commandable, but not used by symbols
     *
     * @return the actual command string for which the current command is shorthand
     */
    public String getRootSubstitute()
    {
        return( null );
    }

    /**
     * required by Commandable, but not used by symbols
     * @return ParamType for the command
     */
    public ParamType getParamType()
    {
        return( null );
    }

    /**
     * Is this command processed or ignored in a code listing?
     * @return boolean indicating whether command is/is not processed in a code listing
     */
    public boolean isAllowedInCode()
    {
        return( validInCode );
    }

    /**
     * method for processing this symbol. Note that many of these parameters are unneeded
     * by symbols, but .
     *
     * @param gdd the GDD
     * @param tl TokenList to which the generated tokens are added
     * @param inCode Are we currently in a code section?
 
     * @return number of chars to skip to get past the command string in the input. If negative,
     * means an error occurred.
     *
     */
    public int process( final GDD gdd, final ParseContext context, final TokenList tl,
                        final boolean inCode )
    {
        return( ProcessSymbol.toToken( context.source, tl, root ));
    }
}
