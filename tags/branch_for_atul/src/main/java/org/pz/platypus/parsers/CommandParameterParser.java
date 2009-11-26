/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.parsers;

import org.pz.platypus.*;

/**
 * Handles all the various command-parameter parsing tasks. All public classes are static.
 */
public class CommandParameterParser
{
    /**
     * Parses a substring that contains a quantity.
     *
     * @param input char[] that contains a numeric quantity
     * @param parsePoint the point in input where the numeric quantity to isLineComment begins
     * @return a CommandParameter instance containing value and type, or null on error
     */
    public static CommandParameter extractUnitValue( final char[] input, int parsePoint )
    {

        if ( input == null || parsePoint < 0 || parsePoint > ( input.length - 1 )) {
            return( null );
        }

        /** the CommandParameter we'll be filling in and returning */
        CommandParameter ret = new CommandParameter();

        char[] actualValue;

        // set ret to error value. We'll override these if we get good results.
        ret.setUnit( UnitType.UNKNOWN );
        ret.setErrorCode( Status.INVALID_PARAM );
        ret.setCharsParsed( 0 );

//     DO WE WANT TO ALLOW A MACRO INSIDE A PARAM SPECIFICATION?
//        // check for a Platypus system macro or a user macro
//        if ( inputStr.charAt( 0 ) == '$' || inputStr.charAt( 0 ) == '#' ) {
//            macroName = Platypus.macros.extractMacroName( inputStr );
//            charsScanned += macroName.length();
//            actualValue = Platypus.macros.get( macroName );
//            if ( actualValue == null ) {
//                ret.setCharsParsed( charsScanned );
//                return( ret );
//            }
//            actualValue =  actualValue.concat( inputStr.substring( macroName.length() ));
//        }
//        else
        {
            actualValue = input;
        }

        // at this point, we have a string that starts with the true  value,
        // with all of its macros resolved, followed by the units (which might still contain macros).

        String numericPortion = extractNumericString( actualValue, parsePoint );
        ret.setCharsParsed( numericPortion.length() );


        float value;
        try {
            value = Float.parseFloat( numericPortion );
        }
        catch ( NumberFormatException nfe ) {
            return( ret );
        }

        // store the value in ret. then...
        ret.setAmount( value );
        ret.setErrorCode( Status.OK );
        return( ret );
    }

    /**
     * Extracts the numeric value from a specific point in a char array. Used for extracting
     * the value of a parameter to a command
     * @param content the char[] containing the value
     * @param parsePoint where the value begins in content
     * @return a string containing the value; or null if an error occurred.
     */
    public static String extractNumericString( final char[] content, final int parsePoint )
    {
        StringBuffer numericPortion = new StringBuffer( 10 );
        char ch;
        int i;
        for( i = parsePoint; i < content.length; i++ )
        {
            ch = content[i];
            if ( ch == '-' || ch == '.' || Character.isDigit( ch )) {
                numericPortion.append( ch );
            }
            else {
                break;
            }
        }
        return( numericPortion.toString() );
    }

    /**
     * Gets the unit type. First it looks up any macro string, then converts the abbreviation for
     * unit to a unit type
     * @param input the characters that contain the unit type
     * @param parsePoint where the unit type begins in input
     * @return a UnitType, which will = UnitType.ERROR in the event of an error.
     */
    public static UnitType extractUnitType( final char[] input, int parsePoint )
    {
        char[] actualValue;

        if ( input == null ) {
            return( UnitType.ERROR );
        }

        if ( input.length == 0 ) {
            return( UnitType.NONE );
        }
//      DO WE WANT TO ALLOW MACROS INSIDE A PARAM SPECIFICATION?
//        // test for string macro only (unit type cannot be numeric)
//        if ( inputStr.charAt( 0 ) == '$' ) {
//            macroName = Platypus.macros.extractMacroName( inputStr );
//            actualValue = Platypus.macros.get( macroName );
//            if ( actualValue == null ) {
//                return( UnitType.ERROR );
//            }
//        }
//        else
        {
            actualValue = input;
        }

        // at this point, we have the string containing the unit type with all macros resolved
        if ( actualValue.length == 0 ) {
            return( UnitType.NONE );
        }
        else {
            return( unitAbbrevToUnitType( actualValue, parsePoint ));
        }
    }

    /**
     * Convert the abbreviation used for the unit into a unit-type (an enum in PlatyConst)
     * @param input characters containing the unit abbreviation
     * @param parsePoint where the unit abbreviation begins
     * @return the UnitType enum that corresponds to the type or to an error
     */
    public static UnitType unitAbbrevToUnitType( final char[] input, int parsePoint )
    {
        char ch1, ch2;

        // note: if input is empty, the following test catches it as input length will >= length.
        if ( input == null || parsePoint < 0 || parsePoint >= input.length ) {
            return( UnitType.ERROR );
        }

        ch1 = input[parsePoint];
        if ( ch1 == '}' || ch1 == ']' || ch1 == '|' ) {
            return( UnitType.NONE );
        }

        // if there is a unit specified, then it must have two letters. Get the second one.
        try {
            ch2 = input[parsePoint+1];
        }
        catch( IndexOutOfBoundsException iobe ) {
            return( UnitType.ERROR );
        }

        if ( ch1 == 'c' && ch2 == 'm' ) {
            return( UnitType.CM );
        }
        else
        if ( ch1 == 'i' && ch2 == 'n' ) {
            return( UnitType.INCH );
        }
        else
        if ( ch1 == 'l' && ch2 == 'i' ) {
            return( UnitType.LINE );
        }
        else
        if ( ch1 == 'p' && ch2 == 't' ) {
            return( UnitType.POINT );
        }
        else
        if ( ch1 == 'p' && ch2 == 'x' ) {
            return( UnitType.PIXEL );
        }
        else
            return( UnitType.ERROR );
    }

    /**
     * Static method that extracts a string parameter.
     *
     * This method accepts a parameter and checks it for matching initial/closing { } 's.
     * Command parameters in Platypus that contain spaces are enclosed in { } characters. If the
     * parameter should contain a {, then multiple { 's can be used to open and close the parameter.
     * This function counts the number of opening {, builds a matching string of } 's and searches
     * for it in the parameter; it then returns whatever it finds between these characters (trimmed
     * of any white space. So: [ff:{times roman}] returns "times roman". Likewise {{{times roman}}}
     *
     * It then extracts the string between the matching { }. If there were no { }, it simply
     * extracts the string.
     *
     * @param input the text we're parsing for the parameter
     * @param parsePoint where the parameter begins
     * @return a CommandParameter containing the string; or null in case of error
     */
    public static CommandParameter extractStringParam( final char[] input, int parsePoint )
    {
        if ( input == null || input.length == 0 || parsePoint < 0 || parsePoint >= input.length ) {
            return( null );
        }

        String stringParam;
        int braceCount = 0;

        if( input[parsePoint] == '{' ) {
            stringParam = extractStringInsideBraces( input, parsePoint );
            braceCount = countBraces( input, parsePoint );
        }
        else {
            StringBuilder sb = new StringBuilder( 20 );
            for( int i = parsePoint; i < input.length; i++ )
            {
                if( input[i] != '|' && input[i] != ']' && input[i] != '\n' ) {
                    sb.append( input[i] );
                }
                else {
                    break;
                }
            }
            stringParam= sb.toString();
        }

        if( stringParam.isEmpty() ) {
            //occurs if the string param is missing; e.g.: [font|face:]
            return( null );
        }

        CommandParameter cp = new CommandParameter();
        cp.setString( stringParam );
        cp.setCharsParsed( stringParam.length() + 2 * braceCount );
        cp.setUnit( UnitType.NONE );

        return( cp );
    }

    /**
     * Extracts the string parameter enclosed in matching opening and closing braces
     * @param input the text in which the braces and parameter are located
     * @param start where the first opening brace begins
     * @return the parameter, or an empty string on error
     */
    public static String extractStringInsideBraces( final char[] input,
                                                                final int start )
    {
        String param;
        int braceCount;
        int i;

        braceCount = countBraces( input, start );

        StringBuilder closingBracesSb = new StringBuilder( braceCount );
        for( int x = 0; x < braceCount; x++ ) {
            closingBracesSb.append( '}' );
        }
        String closingBraces = closingBracesSb.toString();
        String inputText = new String( input );

        i = inputText.indexOf( closingBraces, start );
        if( i < 0 ) {
            return( "" );
        }

        param = inputText.substring( start + braceCount, i );
        return( param );
    }

    /**
     * Count the number of opening braces in a parameter
     * @param input the chars containing the opening braces
     * @param start where the opening braces start
     * @return the number of opening braces.
     */
    public static int countBraces( final char[] input, final int start)
    {
        assert( input != null );
        assert( start >= 0 && start < input.length );

        int braceCount = 0;

        for( int i = start; i < input.length; i++ ) {
            if( input[i] == '{') {
                braceCount++;
            }
            else {
                break;
            }
        }

        return( braceCount );
    }

    /**
     * Returns a string of braces (that is { or }, depending on the char passed to it)
     * for the length specified. Invalid counts or brace-chars result in an empty string.
     *
     * @param braceChar is '{' for opening brace or '}' for closing brace
     * @param howMany how many braces to put in the string
     * @return the string containing the braces, or an empty string otherwise.
     */
    public static String makeBraces( final char braceChar, final int howMany )
    {
        if( howMany < 1 || ( braceChar != '{' && braceChar != '}' )) {
            return "";
        }

        StringBuilder braces = new StringBuilder( howMany );
        for( int i = 0; i < howMany; i++ ) {
            braces.append( braceChar );
        }
        return( braces.toString() );
    }
}
