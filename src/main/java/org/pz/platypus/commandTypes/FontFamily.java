/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.commandTypes;

import org.pz.platypus.*;
import org.pz.platypus.exceptions.InvalidCommandException;
import org.pz.platypus.exceptions.InvalidCommandParameterException;
import org.pz.platypus.interfaces.ICommand;
import org.pz.platypus.parsers.CommandFamilyParser;

/**
 * handles processing of the font family of commands
 *
 * @author alb
 */
public class FontFamily implements ICommand
{
    private String root = "[font|";

    private ParamType parameterType = ParamType.NONE;

    private boolean validInCode = true;

    private TokenType tokType = TokenType.COMMAND_FONT_FAMILY;

    private static CommandFamilyParser commandParser = null;



    /**
     * Processes the command and returns the number of chars in the lexeme.
     *
     * @param tl TokenList to which the generated tokens are added
     * @param inCode Are we currently in a code section?
     * @return number of chars to skip to get past the command string in the input. If negative,
     * means an error occurred.
     */
    public int process( GDD gdd, ParseContext context, final TokenList tl, final boolean inCode )
    {
        if( commandParser == null ) {
            commandParser = new CommandFamilyParser();
        }

        TokenList newTokens = new TokenList();
        int length;

        try {
            length = commandParser.parse( gdd.getCommandTable(), context, newTokens, root, gdd );
        }
        // on exception, return value that skips over the errant command
        catch( InvalidCommandException ice ) {
            return( findCommandClose( context ) - context.startPoint + 1 );
        }
        catch( InvalidCommandParameterException icpe ) {
            return( findCommandClose( context ) - context.startPoint + 1 );
        }
        catch( IllegalArgumentException iae ) { //here, skip the entire line, else we can't recover
            gdd.logWarning(
                gdd.getLit( "FILE#" ) + " " + context.source.getFileNumber() + " " +
                gdd.getLit( "LINE#" ) + " " + context.source.getLineNumber() + " " +
                iae + " " +
                gdd.getLit( "SKIPPED" ));
            return( context.getContent().length() - context.startPoint );
        }

        for ( Token t : newTokens ) {
            tl.add( t );
        }
        return( length );
    }


    public int processSubstitute( final ParseContext context, final TokenList tl,
                                  final boolean inCode, final GDD gdd, final String subst )
    {
        int tlStartingSize = tl.size();
        int k = process( gdd, context, tl, inCode );
        if( tl.size() == tlStartingSize + 1 ) {
            insertOriginalLexeme( tl, subst );
        }
        return( k );
    }

    /**
     * In cases where the current token is an expanded form of a shorthand command,
     * this routine replaces the lexeme in the processed token with the original shorthand
     * that the user wrote.
     * @param tl token list
     * @param origLexeme the original shorthand lexeme
     */
    void insertOriginalLexeme( final TokenList tl, final String origLexeme )
    {
        if( origLexeme == null ) {
            return;
        }

        Token t = tl.get( tl.size() - 1);
        String tokLit = t.getContent();
        int colon = tokLit.indexOf( ':' );
        StringBuffer sb = new StringBuffer();
        sb.append( origLexeme ).append( tokLit.substring( colon+1 ));
        t.setContent( sb.toString() );
        tl.set( tl.size() - 1, t );
    }

    /**
     * Finds the rough end of a command. Note: does not have to be the exact end, as long
     * as it's not past the end of the command. The point is to write some portion of the
     * command out as text, so this picks up the entire command in nearly all cases. In those
     * where it doesn't, the remaining fragment will not be parsed as a command and so will
     * later be written out by PlatypusParser as text as well, which is what we want.
     *
     * @param context the parsing context
     * @return index of char in content where the command is thought to end
     */
    int findCommandClose( final ParseContext context )
    {
        if( context == null ) {
            return( 0 );
        }

        int i;
        for( i = context.startPoint; i < context.chars.length; i++ )
        {
            if( context.chars[i] == ' ' || context.chars[i] == ']' || context.isEnd( i )) {
                break;
            }
        }
        return( i );
    }

//    /**
//     * Loads the data on the individual commands that make up the family
//     */
//    void loadCommandMap()
//    {
//        commandMap = new CommandFamilyMap();
//
//        Command fAlt = new Falt();
//        commandMap.add( "alt",  fAlt.getParamType(), fAlt.getRoot() );
//
//        Command fFace = new Fface();
//        commandMap.add( "face", fFace.getParamType(), fFace.getRoot() );
//
//        Command fSize = new Fsize();
//        commandMap.add( "size", fSize.getParamType(), fSize.getRoot() );
//    }


    public boolean isAllowedInCode()
    {
        return( validInCode );
    }

    public ParamType getParamType()
    {
        return( parameterType );
    }    

    public String getRoot()
    {
        return( root );
    }

    public String getRootSubstitute()
    {
        return( null );
    }

    public TokenType getTokenType()
    {
        return( tokType );
    }
}