/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

/**
 * Object that contains the file number and line number of the referenced input item
 *
 * @author alb
 */
public class Source implements Cloneable
{
    /** the file number. FileList maps the number to a specific file name */
    private int fileNumber;

    /** the line numbre within the file pointed to by fileNumber */
    private int lineNumber;

    public Source()
    {
        fileNumber = 0;
        lineNumber = 0;
    }

    public Source( final int lineNum )
    {
        fileNumber = 0;
        lineNumber = lineNum;
    }

    public Source( final int fileNum, final int lineNum )
    {
        fileNumber = fileNum;
        lineNumber = lineNum;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o ) {
            return( true );
        }

        if ( o == null || getClass() != o.getClass() ) {
            return( false );
        }

        final Source source = (Source) o;

        if (( fileNumber == source.fileNumber ) && ( lineNumber == source.lineNumber )) {
            return( true );
        }

        return( false );
    }

    @Override
    public Source clone() throws CloneNotSupportedException
    {
        Source newSrc = (Source) super.clone();
        newSrc.setFileNumber( fileNumber );
        newSrc.setLineNumber( lineNumber );
        return( newSrc );
    }

    @Override
    public int hashCode()
    {
        return( 31 * fileNumber + lineNumber );
    }

    //=== getters and setters ===/

    public int getFileNumber()
    {
        return( fileNumber );
    }

    public void setFileNumber( final int newFileNumber )
    {
        fileNumber = newFileNumber;
    }

    public int getLineNumber()
    {
        return( lineNumber );
    }

    public void setLineNumber( final int newLineNumber )
    {
        lineNumber = newLineNumber;
    }
}
