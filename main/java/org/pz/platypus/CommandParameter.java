/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

/**
 * Purely a data structure used for returning units information from parsers.
 * It contains, to speak of, no logic. For this reason, there also no unit
 * tests for this class.
 *
 * @author alb
 */
public class CommandParameter
{
    /** the number of units */
    private float amount;

    /** the type of unit. e.g. POINTS, PIXELS, INCHES, etc. */
    private UnitType unit;

    /** if an error occured (unit = ERROR), what kind was it? */
    private int errorCode;

    /** how many chars were taken up with the number and the units? */
    private int charsParsed;

    /** used only if parameter is a string */
    private String string;

    public CommandParameter()
    {
        this.unit = UnitType.ERROR;
        this.errorCode = 0;
        this.string = null;
    }

    /**
     * Due to its many fields
     * @param o the command parameter to test equality against
     * @return true = are equal, false otherwise
     */
    @Override
    public boolean equals( final Object o )
    {
        if( o == null ) {
            return( false );
        }

        if( ! ( o instanceof CommandParameter )) {
            return( false );
        }

        CommandParameter cp = (CommandParameter) o;

        if( amount != cp.getAmount() || unit != cp.getUnit() || errorCode != cp.errorCode ||
            charsParsed != cp.charsParsed ) {
            return( false );
        }

        if( string == null ) {
            return(  cp.getString() == null );
        }

        // so only differences in string (of which this.string is known not to be null apply
        if( cp.getString() == null || ! string.equals( cp.getString() )) {
            return( false );
        }

        return( true );
    }

    /**
     * If you override equals(), then...
     * @return the hash code
     */
    @Override
    public int hashCode()
    {
        return( (int) amount * 131 + errorCode * 17 + charsParsed * 7 );
    }

    //=== getters and setters in alpha order by field name ===/

    public float getAmount()
    {
        return amount;
    }

    public void setAmount( final float amount )
    {
        this.amount = amount;
    }

    public UnitType getUnit()
    {
        return unit;
    }

    public void setUnit( final UnitType unit )
    {
        this.unit = unit;
    }

    public int getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode( final int errorCode )
    {
        this.errorCode = errorCode;
    }

    public int getCharsParsed()
    {
        return charsParsed;
    }

    public void setCharsParsed( final int charsParsed )
    {
        this.charsParsed = charsParsed;
    }

    public String  getString()
    {
        return( string );
    }

    public void setString( final String newString )
    {
        this.string = newString;
    }

}
