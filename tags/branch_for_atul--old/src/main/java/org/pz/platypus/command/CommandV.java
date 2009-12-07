/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.command;

import org.pz.platypus.*;
import org.pz.platypus.parsers.CommandParameterParser;


/**
 * Command with a single Value parameter
 *
 * @author alb
 */
public class CommandV extends Command
{
    public CommandV( final String commandRoot, final char allowInCode )
    {
        parameterType = ParamType.MEASURE;
        root = commandRoot;
        validInCode = ( allowInCode == 'y' ? true : false );
    }

    /**
     * Processes the command, emits the corresponding token, and returns the number of
     * chars in the lexeme.
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
        int parsePoint = context.startPoint;
        parsePoint += root.length();

        CommandParameter cparm = CommandParameterParser.extractUnitValue( context.chars, parsePoint );
        parsePoint += CommandParameterParser.extractNumericString( context.chars, parsePoint).length();
        UnitType unitTyp = CommandParameterParser.extractUnitType( context.chars, parsePoint );
        cparm.setUnit( unitTyp );
        if( unitTyp == UnitType.POINT || unitTyp == UnitType.CM || unitTyp == UnitType.INCH ||
            unitTyp == UnitType.LINE  || unitTyp == UnitType.PIXEL ) {
                parsePoint += 2;
        }

        parsePoint += 1;
        String commandText = context.segment( parsePoint );
        Token tok = new Token( context.source, TokenType.COMMAND, root, commandText, cparm );
        tl.add( tok );
        return( parsePoint - context.startPoint );
    }
}