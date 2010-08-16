/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.commandTypes;

import org.pz.platypus.*;
import org.pz.platypus.parsers.CommandParameterParser;

/**
 * Command with a single String parameter
 *
 * @author alb
 */
public class CommandS extends Command
{
    public CommandS( final String commandRoot, final char allowInCode )
    {
        root = commandRoot;
        rootSubstitute = null;
        parameterType = ParamType.STRING;
        validInCode = ( allowInCode == 'y' ? true : false );
    }

    /**
     * Processes the command and returns the number of chars in the command + parameters.
     *
     * @param gdd GDD
     * @param tl TokenList to which the generated tokens are added
     * @param inCode Are we currently in a code section?
     * @return number of chars to skip to get past the command string in the input. If negative,
     * means an error occurred.
     */
    public int process( final GDD gdd, ParseContext context,
                        final TokenList tl, final boolean inCode )
    {
        int braces = 0;
        int parsePoint = context.startPoint + root.length();
        CommandParameter cparam =
            CommandParameterParser.extractStringParam( context.chars, parsePoint );

        if( context.chars[parsePoint] == '{') {
            braces = CommandParameterParser.countBraces( context.chars, parsePoint );
        }

        String newContent = root +
                            CommandParameterParser.makeBraces( '{', braces ) +
                            cparam.getString() +
                            CommandParameterParser.makeBraces( '}', braces ) +
                            "]";

        if( ! validInCode && gdd.isInCode() )  {
            return notExecutedInCodeSection( newContent, tl, context.source, gdd );
        }
        Token tok = new Token( context.source, TokenType.COMMAND, root, newContent, cparam );
        tl.add( tok );

        return( newContent.length() );
    }

    //=== getters and setters ===//

    @Override
    public ParamType getParamType()
    {
        return( parameterType );
    }

    @Override
    public String getRoot()
    {
        return( root );
    }

    @Override
    public String getRootSubstitute()
    {
        return( null );
    }

}