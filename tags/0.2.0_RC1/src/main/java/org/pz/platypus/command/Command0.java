/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.command;

import org.pz.platypus.*;

/**
 * Command with no parameters
 *
 * @author alb
 */
public class Command0 extends Command
{
    public Command0( final String commandRoot, final char allowInCode )
    {
        assert( commandRoot != null );

        root = commandRoot;
        parameterType = ParamType.NONE;
        validInCode = ( allowInCode == 'y' ? true : false );
    }

    /**
     * Processes the command and returns the number of chars in the lexeme.
     *
     * @param gdd GDD
     * @param tl TokenList to which the generated tokens are added
     * @param inCode Are we currently in a code section?
     * @return number of chars to skip to get past the command string in the input. If negative,
     * means an error occurred.
     */
    public int process( final GDD gdd, final ParseContext context, final TokenList tl,
                        final boolean inCode )
    {
        Token tok = new Token( context.source, TokenType.COMMAND, root, root, null );
        tl.add( tok );
        return( root.length() );
    }
}