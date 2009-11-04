/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

/**
* Contains the current parsing info in a single object.
*
* @author alb
*/
public class ParseContext
{
    final public Source source;
    private String content;
    public char[] chars;
    final public int startPoint;

    public ParseContext( final GDD gdd, final Source source, String content, final int startPoint )
    {
        if( content == null || source == null ) {
            throw new IllegalArgumentException( ); //curr: use our version
        }

        if( startPoint < 0 ) {
            throw new IllegalArgumentException( ); //curr: use our version
        }

        this.source = source;
        this.content = content;
        this.chars = content.toCharArray();
        this.startPoint = startPoint;

        if ( chars[chars.length-1] != '\n' ) {
            gdd.logSevere( gdd.getLit( "FILE#" ) + " " + source.getFileNumber() + " " +
                           gdd.getLit( "LINE#" ) + " " + source.getLineNumber() + " " +
                           gdd.getLit( "ERROR.NO_CRLF" ));
            throw new IllegalArgumentException( ); //curr: use our version
        }

    }

    public boolean atEndOfLine() {
        return isEnd(startPoint);
    }

    public String segment( final int endPoint ) {
        return content.substring( startPoint, endPoint );
    }

    public boolean isEnd( final int parsePoint ) {
        return chars[parsePoint] == '\n';
    }

    public boolean isCommandStart( final int parsePoint ) {
        return chars[parsePoint] == '[';
    }

    public boolean containsInRemainingChars( final String symbol ) {
        return content.substring( startPoint ).contains( symbol );
    }

    public int getLocation( final String symbol ) {
        return content.indexOf( symbol, startPoint );
    }

    public boolean isPastEol( final int parsePoint ) {
        return( parsePoint >= chars.length );
    }


    public String getContent()
    {
        return( content );
    }
}
