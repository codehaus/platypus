/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.*;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.utilities.ClosingBraceBuilder;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.interfaces.OutputCommandable;

/**
 * handles processing of the definition of a user string (macro).
 * Handled by the plug-in because the same value can be redefined
 * numerous times by the Platypus file in context-dependent ways.
 *
 * @author alb
 */
public class DefUserString implements OutputCommandable
{
    private String root = "[def:";

    private ParamType parameterType = ParamType.STRING;

    private boolean validInCode = false;

    public void process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdf = (PdfData) context;
        GDD gdd = pdf.getGdd();
        CommandParameter cparam = tok.getParameter();

        // parameter must have a name at the start that begins with letter or digit
        if( cparam == null || ! Character.isLetterOrDigit( cparam.getString().charAt( 0 ))) {
            issueErrorMessage( gdd, tok.getSource(), root );
        }

        // at this point, cparam contains: macroName:{definition}. Now, extract macro name.
        String macroName = extractMacroName( cparam.getString() );
        if( macroName.isEmpty() ) {
            issueErrorMessage( gdd, tok.getSource(), cparam.getString() );
        }

        // get the value associated with the macro name
        String macroValue = extractMacroValue( cparam.getString(), macroName );
        if( macroValue.isEmpty() ) {
            issueErrorMessage( gdd, tok.getSource(), cparam.getString() );
        }

        // add to the hash map of user strings
        if( gdd.getUserStrings().add( macroName, macroValue ) != Status.OK ) {
            issueErrorMessage( gdd, tok.getSource(), macroName + ":" + macroValue );
        }
    }

    /**
     * Extracts the macro name from the beginning of a string
     * @param text the string to extract the macro name from
     * @return the macro name or an empty string if an error occurs
     */
    String extractMacroName( final String text )
    {
        StringBuilder macroName = new StringBuilder( 20 );
        final String emptyString = "";    // returned in event of error

        if( text == null || text.isEmpty() ) {
            return( emptyString );
        }

        if( ! Character.isLetterOrDigit( text.charAt( 0 ))) {
            return( emptyString );
        }

        macroName.append( text.charAt( 0 ));

        for( int i = 1; i < text.length(); i++ ) {
            char c = text.charAt( i );
            if( Character.isLetterOrDigit( c ) || c == '_') {
                macroName.append( c );
            }
            else {
                break;
            }
        }

        return( macroName.toString() );
    }

    /**
     * Extracts the substitute value from the macro definition
     * @param text the macro definition
     * @param macroName the macro name
     * @return the substitute string value, or the empty string in case of error.
     */
    String extractMacroValue( final String text, final String macroName )
    {
        final String emptyString = "";    // returned in event of error

        if( text == null || macroName == null || text.isEmpty() ) {
            return( emptyString );
        }

        if( macroName.length() >= text.length() ) {
            return( emptyString );
        }

        int i = macroName.length(); //starting point in the name{definition} string

        if( text.charAt( i ) != '{' ) {
            return( emptyString );
        }

        String definition = text.substring( i );
        String closingBraces = new ClosingBraceBuilder( definition ).getClosingBrace();
        int j = definition.indexOf( closingBraces );
        if( j < 1 ) {
            return( emptyString );
        }

        return( definition.substring( closingBraces.length(), j ));
    }

    public void issueErrorMessage( final GDD gdd, final Source source, final String macro )
    {
        gdd.logWarning(
           gdd.getLit( "FILE#" ) + " " + source.getFileNumber() + " " +
           gdd.getLit( "LINE#" ) + " " + source.getLineNumber() + " " +
           gdd.getLit( "ERROR.INVALID_USER_STRING" ) + " " + macro +  " " +
           gdd.getLit( "SKIPPED" ));
    }

    /**
     * In the event of an error, the command is converted into a series of text tokens (after the
     * error message is shown). This is done by taking the root and making it a text token. Sub-
     * sequent parsing by the parser will then view the remaining part of the command string as
     * text.
     * @param source the current file # and line #
     * @param tl the token list to add the text token to
     */
    public void convertToTextToken( Source source, TokenList tl )
    {
        Token tok = new Token( source, TokenType.TEXT, root );
        tl.add( tok );
    }

    //=== getters and setters ===/
    
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
        return (TokenType) null;
    }
}