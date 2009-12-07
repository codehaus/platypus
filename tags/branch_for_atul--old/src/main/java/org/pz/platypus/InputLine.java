/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 *
 */
package org.pz.platypus;

/**   //curr: do we need the setters? Line probably should be immutable 
 * Defines a InputLine, which simply holds the file#, line # of the current input line, and
 * the content of the current input line (as a string).
 *
 * @author alb
 */
public class InputLine
{
    /** the file # and line # for this InputLine */
    private Source source;

    /** the content of the line as a string */
    private String content;
    
    public InputLine()
    {
        source = new Source();
        content = null;
    }

    public InputLine( int lineNumber, String text )
    {
        source = new Source( lineNumber );
        content = text;
    }

    public InputLine( final int fileNumber, final int lineNumber, String text )
    {
        source = new Source( fileNumber, lineNumber );
        content = text;
    }

    public InputLine( final Source s, String text )
    {
        source = s;
        content = text;
    }
    
    /**
     * Dump one input line to console.
     * Note: uses printf from Java 5
     * @return a string containing the dumped InputLine
     */
    public String dump()
    {
        final int MAX_CONTENT = 40;
        
        String contentToPrint;
        
        if ( content == null || content.isEmpty() || source == null  )  {
            contentToPrint = " ";
        }
        else
        if ( content.length() > MAX_CONTENT ) {
            contentToPrint = content.substring( 0, MAX_CONTENT );
            contentToPrint += ( "..." );
        }
        else {
            contentToPrint = content;
        }
        
        String dumpString = null;
        return( dumpString.format( "%02d - %04d: %s",
                source.getFileNumber(), source.getLineNumber(), contentToPrint ));
    }

    //=== getters and setters ===//
    
    /**
     * return a string with the line content
     * @return content
     */
    public String getContent()
    {
        return( content );
    }

    /**
     * Set the content portion
     * @param newContent the new line content
     */
    public void setContent( final String newContent )
    {
        content = newContent;
    }

    /** 
     * get the file number that this line appeared in. The file number is primarily
     * used to track which file is being read in the event that a file is included
     * in the main file via the [include:xxx] command.
     * 
     * @return the file number
     */
    public Source getSource()
    {
        return( source );
    }

    public void setSource( final Source newSource )
    {
       source = newSource;
    }
}
