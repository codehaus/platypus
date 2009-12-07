/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import org.pz.platypus.interfaces.Commandable;
import org.pz.platypus.parsers.BlockCommentParser;
import org.pz.platypus.parsers.CommandStartParser;
import org.pz.platypus.parsers.LineCommentParser;
import org.pz.platypus.parsers.MacroParser;
import static org.pz.platypus.utilities.TextTransforms.*;


/**
 * The primary parser for Platypus files. It parses input into core elements:
 *  - text
 *  - commands and macros
 *  - comments, both line and block format
 */
public class PlatypusParser
{
    /** are we in code? If so, this changes how we isLineComment text. */
    private boolean inCode;

    /** the symbol we need to match for the close of a block of  */
    private String blockCommentClosingSymbol = null;

    /** the ComandTable that holds all the commands that Platypus supports */
    private CommandTable ctable;

    private GDD gdd;

    public PlatypusParser( final GDD Gdd )
    {
        inCode = false;
        gdd = Gdd;
        if( gdd != null ) {     //curr: add error handling if GDD is null
            ctable = gdd.getCommandTable();
        }
    }

    /**
     * Converts input lines into an ArrayList of parsed tokens.
     * 
     * @param lines  the input lines
     * @param tokens the resulting tokens
     * @param configFile the configuration file might have info on how to handle some features
     * @param pluginPrefix the prefix to look up output items with in the config file
     * @return Status.OK if all went well, otherwise an error code.
     */
    public int parse( final LineList lines,
                      final TokenList tokens,
                      final PropertyFile configFile,
                      final String pluginPrefix )
    {
        if ( lines == null || tokens == null || configFile == null || pluginPrefix == null ) {
            return( Status.INVALID_PARAM_NULL );
        }
              //curr: move this to prior to call of this routine
        if ( ! doesPlatypusParse( configFile, pluginPrefix ))
            return( noParse( lines, tokens ));

        int i;
        for ( i = 0; i < lines.size(); i++ )
        {
            if( parseLine( tokens, lines.get( i )) == Status.UNFIXABLE_PARSE_ERR ) {
                return( Status.UNFIXABLE_PARSE_ERR );
            }
        }

        gdd.log( "Finished parsing input: " + i + " lines -> " + tokens.size() + " tokens." );
        return( Status.OK );
    }


    /**
     * Parse a line of the input file
     * @param line line to be parsed
     * @param newTokens the TokenList to which we're adding tokens
     * @return Status.OK or Status.UNFIXABLE_PARSE_ERR, which is a serious, stop-the-Platypus error
     */
    public int parseLine( final TokenList newTokens, final InputLine line )
    {
        if ( newTokens == null || line == null ) {
            return( Status.INVALID_PARAM_NULL );
        }

        if( inCode ) {} // process being in code and return. //TODO

        // a line comment?
        if( LineCommentParser.isLineComment( line.getContent(), blockCommentClosingSymbol )) {
            addToken( newTokens, TokenType.LINE_COMMENT, line.getContent(), line.getSource() );
            return( Status.OK );
        }

        // is it a blank line?
        Character c = line.getContent().charAt(0);
        if( c == '\n' || c == '\r' ) {
                 newTokens.add( new Token( line.getSource(), TokenType.COMMAND, "[CR]", "[CR]",
                                           null ));
                return( Status.OK );
        }

        // isLineComment segments of the line (segments = parsable units: text, command, comment).
        // '\n' is the last parsable token in the line.
        int segmentStartPoint = 0;
        String content = line.getContent();
        while( segmentStartPoint < content.length() )
        {

            ParseContext context = new ParseContext( gdd, line.getSource(), content, segmentStartPoint);

            if ( context.atEndOfLine() ) {
                emitEolToken( newTokens, context.source );
                break;
            }
            
            // are we in a block comment?
            if ( blockCommentClosingSymbol != null ) {
                int closeCommentStart = content.indexOf( blockCommentClosingSymbol, segmentStartPoint );
                if( closeCommentStart == -1 ) {
                    addToken( newTokens, TokenType.BLOCK_COMMENT, content, context.source );
                    return( Status.OK );
                }
                else {
                    int i = handleClosingBlockComment( newTokens, context, closeCommentStart );
                    blockCommentClosingSymbol = null;
                    segmentStartPoint += i;
                    continue;
                }
            }

            segmentStartPoint = parseSegment( context, newTokens );
            if ( segmentStartPoint == Status.UNFIXABLE_PARSE_ERR ||
                 segmentStartPoint == Status.INVALID_PARAM_NULL ||
                 segmentStartPoint == Status.INVALID_PARAM ) {
                return( Status.UNFIXABLE_PARSE_ERR );
            }

            // if there is code to inject (generally, as the result of a macro
            // expansion, then remove the amount of text occupied by the macro
            // and replace it with the macro body. Then restart the isLineComment point
            // at the beginning of this new input string.
            if ( gdd.getExpandedMacro() != null ) {
                content = gdd.getExpandedMacro() + content.substring( segmentStartPoint );
                segmentStartPoint = 0;
                gdd.setExpandedMacro( null );
                continue;
            }
        }
        return( Status.OK );
    }

    /**
     * Emits the so-called "soft" EOL command used when at end of line.
     * 
     * @param newTokens token list to add the token to
     * @param source file# and line# of token
     */
    void emitEolToken( final TokenList newTokens, final Source source )
    {
         newTokens.add( new Token( source, TokenType.COMMAND, "[cr]", "[cr]", null ));
    }


    /**
     * Write the closed block comment to the token list and return the amount by which
     * the isLineComment point needs to be moved up to get past the block comment.
     * @param newTokens what we write the tokens to
     * @param context the parsing context info
     * @param closeCommentStart where the beginning of the close to the block comment is
     * @return number of chars to move forward to get past the closing of the block comment
     */
    private int handleClosingBlockComment( final TokenList newTokens, final ParseContext context,
                                           final int closeCommentStart )
    {
        int i = closeCommentStart + blockCommentClosingSymbol.length();
        addToken( newTokens, TokenType.BLOCK_COMMENT, context.segment( i ),
                context.source );

        return( i );
    }

    /**
     * The primary line-parsing routine. It finds one segment (text, a command, a comment block)
     * parses it, adds it to tokens, and returns after moving parsePoint to the first character
     * of the next segment. This method is called repeatedly by parseLine() until parsePoint
     * points to the '\n' that ends the input line.
     *
     * Note: assumes that all segments end with a \n
     *
     * @param context the source context data
     * @param tokens the sequence of tokens to which we'll add the parsed segment.
     * @return the new isLineComment point or, if an error that can't be fixed in the parser
     *         occurs, returns Status.UNFIXABLE_PARSE_ERR
     */
    public int parseSegment( final ParseContext context, TokenList tokens )
    {
        if ( tokens == null || context == null )
            return( Status.INVALID_PARAM_NULL );

        int parsePoint = context.startPoint;

        while( true )
        {
            // if we're at a \n, write out all the preceding text we've seen, and exit.
            // On the next loop through in parseLine(), the \n will be recognized and
            // be processed as part of end of line.

            if ( context.isEnd( parsePoint )) {
                if( context.atEndOfLine() ) {
                    //curr: how did this happen?!? should have been handled in parseLine
                }
                writeOutText( context, parsePoint-1, tokens );
                return( parsePoint );
            }

            if ( context.isCommandStart( parsePoint ) ) {
                if ( CommandStartParser.isItACommand( context.chars, parsePoint )) {
                    // if the [ is not the beginning of the segment, then
                    // write out all that precedes it as text. Then loop.
                    // the new segment starting with [ will come back
                    // here and proceed with processing the command 
                    if ( context.startPoint != parsePoint ) {
                        writeOutText( context, parsePoint-1, tokens );
                        return( parsePoint );
                    }

//                    // is it a macro?
//                    if( context.chars[parsePoint+1] == '$' ) {
//                        return( processMacro( context, tokens ) + 1 );
//                    }

                    // is it a block comment?
                    if( context.chars[parsePoint+1] == '%' ) {
                        return( processBlockComment( context, tokens ));
                    }

                    // it's a command
                    parsePoint = processCommand( context, tokens );
                    return( parsePoint );
                }
                else   // it's not a command.
                {
                    // if it's an escaped [ (so: /[ ), we need to check whether we passthrough
                    // the escape char. If not (the general case), we write all the text up to
                    // the escape char, plus the [ after it to a text token.
                    if( CommandStartParser.isItEscapedCommandStart( context.chars, parsePoint )) {
                        if( ! passThroughEscapeChar() ) {
                            if( parsePoint - context.startPoint > 2 ) {// there's preceding text
                                writeOutText( context, parsePoint-2, tokens );
                            }
                            tokens.add( new Token( context.source, TokenType.TEXT, "[" ));
                            return( ++parsePoint );
                        }
                    }
                }
            }

            // if a character is not a command, comment, macro or a LF, it's text, so keep looping.
            parsePoint++;
        }
    }

    /**
     * Check the config file to see whether we pass through the escape char to the
     * output token stream. The default is no. So, in the event it's not specified,
     * we return false.
     * @return false if we don't pass it through (the general case), true if we do.
     */
    boolean passThroughEscapeChar()
    {
        PropertyFile configFile = gdd.getConfigFile();
        if( configFile == null ) {
            return( false );
        }

        String pluginPrefix = gdd.getOutputPluginPrefix();
        String setting =
            configFile.lookup( "pi.out." + pluginPrefix + ".passthrough_escape_char" );

        if( setting == null ) {
            return( false );
        }

        if( setting.equals( "yes" )) {
            return( true );
        }

        return( false );
    }

    /**
     * Write out text Token to the token list
     * @param context where the text string is
     * @param endPoint the end of the text (starts at context.startPoint)
     * @param tokens the token list to write the token to.
     */
    private void writeOutText( final ParseContext context, final int endPoint,
                               final TokenList tokens )
    {
        writeOutText( context.startPoint, endPoint, context.chars, tokens, context.source );
    }

    /**
     * Processes block comments. It identifies the starting marker, computes the ending marker,
     * and verifies checks whether the closing marker is on the same line. If so, it handles the
     * block comment. Otherwise, it stores the closing marker in blockCommentClosingSymbol.
     *
     * @param tokens the token list to add the block-comment token to
     * @param context the parser context/location info.
     * @return the new isLineComment point //TODO: put this in a block-comment parser?
     */
    public int processBlockComment( final ParseContext context, TokenList tokens )
    {
        BlockCommentParser bcp = new BlockCommentParser();

        blockCommentClosingSymbol = bcp.computeClosingMarker( context.chars, context.startPoint );
        if( blockCommentClosingSymbol == null ) {
            //curr: now what?!? throw exception (used only by tests)
        }

        // if it's a multiline block comment, write it out and point to EOL.
        int endPoint = context.chars.length - 1;

        // but first check to see whether block closes on this same line.
        if ( blockClosesOnSameLine(context, blockCommentClosingSymbol) ) {
            endPoint = context.getLocation( blockCommentClosingSymbol ) + blockCommentClosingSymbol.length();
            blockCommentClosingSymbol = null;
        }
        
        addToken( tokens, TokenType.BLOCK_COMMENT, context.segment( endPoint ), context.source );
        return( endPoint );
    }

    private boolean blockClosesOnSameLine( final ParseContext context,
                                           final String blockCommentClosingSymbol )
    {
        return context.containsInRemainingChars( blockCommentClosingSymbol );
    }

    /**
     * Just grabs the command and writes it to the token list
     *
     * @param context parse context: sourcefile# & line#, parsePoint, line as string, line as char[]
     * @param tokens token list to which we will add the tokens derived from the command
     * @return the updated version of the isLineComment point.
     */
    public int processCommand( final ParseContext context, TokenList tokens)
    {
        try {
            final String commandRoot = extractCommandRoot( context );
            if( commandRoot == null ) {
                //TODO: do something big here
            }

            Commandable command = ctable.getCommand( commandRoot );
            if ( command != null ) {
                return( command.process( gdd, context, tokens, inCode ) + context.startPoint );
            }
            else {
                // the command root is not found, so it's probably text that looks like command
                // so, write out the root as text. The rest of the command will be treated as
                // text on the next pass through the rest of the segment.
                invalidCommandError( context, tokens, commandRoot );
                return( context.startPoint + commandRoot.length() );
            }
        }
        catch ( IndexOutOfBoundsException iobe ) {
            gdd.logWarning( gdd.getLit( "ERROR.UNEXPECTED_EOL" ) + " " +
                                     gdd.getLit( "FILE#" ) + context.source.getFileNumber() + " " +
                                     gdd.getLit( "LINE#") + context.source.getLineNumber() + " "  +
                                     gdd.getLit( "IGNORED" ));
        }

        // in theory we should never get here.
        return( Status.UNREACHABLE_ERR );
    }

    /**
     * Write an error message to console saying that the command-like token is not actually
     * a Platypus command, and then write the token out to the output as a text token.
     *
     * @param context the parser context
     * @param tokens list of tokens to which this token will be added
     * @param commandRoot the root of the command, were it a real command. 
     */
    void invalidCommandError( final ParseContext context,
                              final TokenList tokens, final String commandRoot )
    {
        gdd.logInfo( gdd.getLit( "FILE#" ) + " " + context.source.getFileNumber() + " " +
                     gdd.getLit( "LINE#" ) + " " + context.source.getLineNumber() + " " +
                     commandRoot + " " +
                     gdd.getLit( "ERROR.NOT_PALYTPUS_COMMAND" ));
        writeOutText( context, context.startPoint + commandRoot.length() - 1, tokens );
    }

    /**
     * Extracts the root of a command. The root is the part that at the beginning of the
     * command that remains the same regardless of parameters. To wit:
     * - commands with no parameters, it's the whole command: [+i]
     * - commands with 1 parameter, it's everything up to the ':' fsize: in [fsize:12pt]
     * - commands with 2+ parameters, it's the family name, so [font| in [font|size:12pt|face:...
     *
     * @param context the parsing location info.
     * @return the command root as a string
     * @throws IndexOutOfBoundsException for unclosed command
     */  //curr: consider making the parameters a string and a starting point.
    public String extractCommandRoot( final ParseContext context )
           throws IndexOutOfBoundsException
    {
        int i;
        char c;
        StringBuilder root = new StringBuilder( 15 );

        // these commands do not contain a : or a | after the root,
        // so they must be tested for explicitly
        if( context.chars[context.startPoint] == '[' &&
            context.chars[context.startPoint+1] == '*' ) {
            return( "[*" );
        }

        for( i = context.startPoint; ; i++ )
        {
            if ( context.isEnd( i )) {
                String unclosedCommand = context.getContent().substring( context.startPoint, i );
                gdd.logWarning( gdd.getLit( "ERROR.MALFORMED_COMMAND" ) +": " + unclosedCommand );
                throw new IndexOutOfBoundsException( null );
            }

            c = context.chars[i];
            root.append( c );
            //curr: need to detect whitespace (as an error)
            if( c == ']' || c == '|' || c == ':' ) {
                return( root.toString() );
            }
        }
    }

    /**
     * If the config file for this output format says that Platypus expands macros, this routine
     * looks up the macro, expands it, and puts the expanded form in gdd.expandedMacro. If the
     * config file says not to process macros, the macro is simply written out to the token stream.
     *
     * @param tokens list of tokens we're building up in parser. (Used only if macros not processed)
     * @param context the parsing context info
     * @return the new isLineComment point after the macro, or Status.UNFIXABLE_PARSE_ERR if an error occurs
     */
    public int processMacro( final ParseContext context,
                             final TokenList tokens )
    {
        MacroParser mp = new MacroParser( gdd );

        // if config file says we don't process macros, then just write them through as a token
        PropertyFile configFile = gdd.getConfigFile();
        if( configFile != null ) {
            String doWeExpandMacros =
                configFile.lookup( "pi.out." + gdd.getOutputPluginPrefix() + ".platy_macroexpand" );
            if ( doWeExpandMacros.equals( "no" )) {
                return( outputMacroAsToken( context, tokens, mp ) + context.startPoint );
            }
        }

        // expand the macro
        try {
            final int charsSpanned = mp.parse( context.chars, context.startPoint );
            return( context.startPoint + charsSpanned - 1 );
        }
        catch ( IllegalArgumentException iae ) {
            return( Status.UNFIXABLE_PARSE_ERR );
        }
    }

    /**
     * Outputs the macro to the TokenList without expanding it.
     *
     * @param context the parsing context data
     * @param tokens the TokenList to which the token is written
     * @param mp the macro parser class
     * @return number of chars to skip
     */
    private int outputMacroAsToken( final ParseContext context,
                                    final TokenList tokens,
                                    final MacroParser mp )
    {
        String macro = mp.extractMacroName( context.chars, context.startPoint + 1 );
        tokens.add( new Token(  context.source, TokenType.MACRO, "[" + macro + "]" ));
        return( macro.length() +  1 );
    }

    /**
     * Write out a set of text characters to the token list as a text token
     * @param start subscript of first char in text
     * @param end subscript of last char in text
     * @param text array of chars containing the line from which the text is extracted
     * @param tokensOut the list of tokens to which the text token will be added
     * @param source the file# and line#
     * @return  right now, always Status.OK. If this doesn't change, might change to void.
     */
    public int writeOutText( final int start, final int end, final char[] text,
                             final TokenList tokensOut, final Source source )
    {

        if ( start < 0 || end < 0 || source == null || start > end ) {
            return( Status.INVALID_PARAM );
        }

        if ( text == null || tokensOut == null ) {
            return( Status.INVALID_PARAM_NULL );
        }
//
//        final StringBuffer tokenText = new StringBuffer( end - start );
//        for( int i = start; i <= end; i++ ) {
//            tokenText.append( text[i] );
//        }

        final String tokenText = charArrayToString( text, start, end );

        final Token tok = new Token( source, TokenType.TEXT, tokenText );
        tokensOut.add( tok );
        
        return( Status.OK );
    }

    /**
     *  Do we do the parsing, or will the output plugin do the parsing? Check the config file
     *  for what the plug-in specifies. If there is no entry, the default is that we isLineComment.
     *
     * @param configFile the configuration file
     * @param pluginPrefix the prefix for this output plugin
     * @return true if we isLineComment, false if the plugin does the parsing.
     */
    public static boolean doesPlatypusParse( final PropertyFile configFile,
                                              final String pluginPrefix )
    {
        final String platypusParseLookupKey =  "pi.out." + pluginPrefix + ".platyparse";

        final String yesOrNo = configFile.lookup( platypusParseLookupKey );
        if ( yesOrNo != null && yesOrNo.equals( "no" )) {
            return( false );
        }

        return( true );
    }

    /**
     * Adds a parsed token to the ArrayList of tokens
     * @param tokens the ArrayList of tokens to add to
     * @param tokType the type of token we're adding (an enum in Platypus.TokenType)
     * @param content the user entered text that's been parsed into a token
     * @param source the file# and line# in the input file where the token was found
     */
    public void addToken( final TokenList tokens, final TokenType tokType,
                          final String content, final Source source )
    {
        if( tokens == null || tokType == null || content == null || source == null ) {
            return;
        }

        tokens.add( new Token( source, tokType, content ));
    }

    /**
     * Some output plugins want to parse the input text themselves. (This would occur, for example,
     * if an output plugin adds proprietary commands to the Platypus commands.) In this case,
     * it is passed tokens that consist only of the unparsed lines.
     *
     * @param lines ArrayList of input lines
     * @param tokens ArrayList of output tokens (consisting of the unparsed lines)
     * @return Status.OK, if all went well; otherwise, an error code.
     */
    public int noParse( final LineList lines, final TokenList tokens )
    {
        if ( lines == null || tokens == null ) {
            return( Status.INVALID_PARAM_NULL );
        }

        for ( int i = 0; i < lines.size(); i++ )
        {
            InputLine line = lines.get( i );

            Token tok = new Token( line.getSource(), TokenType.LINE, line.getContent() );
            tokens.add( tok );
        }
        return( Status.OK );
    }

    //=== getters and setters ===

    public boolean isInCode()
    {
        return( inCode );
    }

    public void setInCode( final boolean inCodeYesOrNo )
    {
        inCode = inCodeYesOrNo;
    }

}
