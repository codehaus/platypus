package org.pz.platypus;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 * <p/>
 * Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 * Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */
public abstract class HtmlListingStrategy {
    protected Logger logger;

    public abstract String format(Token tok, GDD gdd) throws IOException;
    public abstract boolean canOutputHtmlEndOfLine();
    
    public static HtmlListingStrategy getFormatStrategy(Token tok) {

        if (tok.getContent().equals("[cr]" )) {
            return new HtmlCRListingStrategy(tok);
        }
        else if ( tok.getContent().endsWith( "[]" ) ) {
            return new HtmlExplicitNewLineListingStrategy(tok);
        }
        else if ( tok.getType().equals( TokenType.COMMAND ) ||
             tok.getType().equals( TokenType.REPLACED_COMMAND )) {
            return new HtmlCommandListingStrategy(tok);
        }
        else if ( tok.getType().equals( TokenType.COMPOUND_COMMAND )) {
            return new HtmlCompoundCommandListingStrategy(tok);
        }
        else if ( tok.getType().equals( TokenType.MACRO )) {
            return new HtmlMacroListingStrategy(tok);
        }
        else if ( tok.getType().equals( TokenType.LINE_COMMENT )) {
            return new HtmlLineCommentListingStrategy(tok);
        }
        else if ( tok.getType().equals( TokenType.BLOCK_COMMENT )) {
            return new HtmlBlockCommentListingStrategy(tok);
        }
        else if ( tok.getType().equals( TokenType.SYMBOL )) {
            return new HtmlSymbolListingStrategy(tok);
        }
        else if ( tok.getType().equals( TokenType.TEXT )) {
            return new HtmlTextListingStrategy(tok);
        }
        throw new IllegalArgumentException( "Html Listing Strategy not supported for " + tok.getType() );
        // return null;
    }

    /**
     * replaces reserved HTML characters to make text printable
     * @param text text to transform
     * @return text string with the transformation applied
     */
    protected String convertToHtmlText( final String text )
    {
        char c;

        if( text == null || text.isEmpty() ) {
            return( "" );
        }

        StringBuilder sb = new StringBuilder( 2 * text.length() );

        for( int i = 0; i < text.length(); i++ )
        {
            switch( c = text.charAt( i ))
            {
                case '<':   sb.append( "&lt;" );    break;
                case '>':   sb.append( "&gt;" );    break;
                case '(':   sb.append( "&#40;" );   break;
                case ')':   sb.append( "&#41;" );   break;
                case '"':   sb.append( "&quot;" );  break;
                case '\'':  sb.append( "&#39;" );   break;
                case '&':   sb.append( "&amp;" );   break;
                case '#':   sb.append( "&#35;" );   break;
                default:    sb.append( c );         break;
            }
        }
        return( sb.toString() );
    }
    
}
