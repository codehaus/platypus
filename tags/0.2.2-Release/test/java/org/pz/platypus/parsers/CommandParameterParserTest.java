/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.parsers;

import org.junit.*;
import static org.junit.Assert.*;
import org.pz.platypus.UnitType;
import org.pz.platypus.CommandParameter;

/**
 * Test of CommandStartParser, which lexically analyzes a series
 * of chars with an embedded [ to see whether it's a Platypus command.
 *
 * @author alb
 */
public class CommandParameterParserTest
{
    @Test
    public void unitAbbrevConversionValid()
    {
        assertEquals( UnitType.CM,
                      CommandParameterParser.unitAbbrevToUnitType( "xcm".toCharArray(), 1 ));
        assertEquals( UnitType.INCH,
                      CommandParameterParser.unitAbbrevToUnitType( "xin".toCharArray(), 1 ));
        assertEquals( UnitType.LINE,
                      CommandParameterParser.unitAbbrevToUnitType( "liin".toCharArray(), 0 ));
        assertEquals( UnitType.PIXEL,
                      CommandParameterParser.unitAbbrevToUnitType( "pxcm".toCharArray(), 0 ));
    }

    @Test
    public void unitAbbrevConversionEmptyUnit()
    {
        char[] charArray = {};
        assertEquals( UnitType.ERROR,
                      CommandParameterParser.unitAbbrevToUnitType( charArray, 0 ));
    }

    @Test
    public void unitAbbrevConversionNoUnit()
    {
        char[] charArray = { ']' };
        assertEquals( UnitType.NONE,
                      CommandParameterParser.unitAbbrevToUnitType( charArray, 0 ));
    }
    @Test
    public void unitAbbrevConversionTooShort()
    {
        char[] charArray = {'c'};
        assertEquals( UnitType.ERROR,
                      CommandParameterParser.unitAbbrevToUnitType( charArray, 0 ));
    }

    @Test
    public void stringParamInBraces1()
    {
        String testWithout = "Bell MT";
        String testWith = "{" + testWithout + "}";
        assertEquals(
                testWithout,
                CommandParameterParser.extractStringInsideBraces( testWith.toCharArray(), 0 ));
    }

    @Test
    public void stringParamInBraces2()
    {
        String testWithout = "Bell MT";
        String testWith = "{{{" + testWithout + "}}}";
        assertEquals(
                testWithout,
                CommandParameterParser.extractStringInsideBraces( testWith.toCharArray(), 0 ));
    }

    @Test
    public void extractStringParamWith3Braces()
    {
        String testText = "Bell MT";
        String testTextWithBraces = "{{{" + testText + "}}}";
        CommandParameter cp =
                CommandParameterParser.extractStringParam( testTextWithBraces.toCharArray(), 0 );
        assertEquals( testText, cp.getString() );
        assertEquals( testText.length() + 2 * "{{{".length(), cp.getCharsParsed() );
    }

    @Test
    public void stringInvalidParamInBracesUnclosed()
    {
        String testWithout = "Bell MT";
        String testWith = "{{{" + testWithout;
        String result =
                CommandParameterParser.extractStringInsideBraces( testWith.toCharArray(), 0 );
        assertTrue( result.isEmpty() );
    }

    @Test
    public void countBraces()
    {
        String text = "abc{{{";
        assertEquals( 3, CommandParameterParser.countBraces( text.toCharArray(), 3 ));

        text = "abcdef";
        assertEquals( 0, CommandParameterParser.countBraces( text.toCharArray(), 3 ));

        text = "abc}}}";
        assertEquals( 0, CommandParameterParser.countBraces( text.toCharArray(), 3 ));
    }

    @Test
    public void makeBracesValid()
    {
        assertEquals( "{", CommandParameterParser.makeBraces( '{', 1 ));
        assertEquals( "}", CommandParameterParser.makeBraces( '}', 1 ));
        assertEquals( "{{", CommandParameterParser.makeBraces( '{', 2 ));
        assertEquals( "}}}", CommandParameterParser.makeBraces( '}', 3 ));
    }

    @Test
    public void makeBracesInvalid()
    {
        assertEquals( "", CommandParameterParser.makeBraces( '{', 0 ));
        assertEquals( "", CommandParameterParser.makeBraces( '}', 0 ));
        assertEquals( "", CommandParameterParser.makeBraces( '[', 2 ));
        assertEquals( "", CommandParameterParser.makeBraces( ']', -1 ));
    }
}