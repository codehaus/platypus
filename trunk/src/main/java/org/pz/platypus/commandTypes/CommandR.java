/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.commandTypes;

import org.pz.platypus.*;
import org.pz.platypus.interfaces.ICommand;
import org.pz.platypus.utilities.*;

/**
 * Command that is a Replacement for another command. Almost always a single command of a family
 * Such as [fsize:12pt] which is mapped to [font|size:12pt]
 *
 * @author alb
 * //TODO: combine the two methods for extracting the root
 */
public class CommandR extends Command
{
    private String replacementRoot;
    private CommandTable cTable;

    public CommandR( final String commandRoot, final String attributes, CommandTable ct )
    {
        parameterType = ParamType.MEASURE;
        root = commandRoot;
        validInCode = ( attributes.charAt( 2 )== 'y' ? true : false );
        replacementRoot = extractReplacementRoot( attributes );
        cTable = ct;
    }

    /**
     * Reads the attribute data for the replacement command as it is found in the command table
     * and extracts the command root that should replace the alias root.
     *
     * @param attribData the data containing the replacement text for the command root
     * @return the extracted replacement text; an empty string on error.
     */
    String extractReplacementRoot( final String attribData )
    {
        StringBuilder sb = new StringBuilder( 20 );

        if( attribData == null || attribData.indexOf( '[') == -1 ) {
            return( "" );
        }

        int i = attribData.indexOf( '[' );
        char c = attribData.charAt( i );

        // should loop until the closing : has been processed. Any whitespace or cr/lf is an error,
        // so we return the partial piece and the error will manifest in subsequent processing.
        while( ! Character.isWhitespace( c )) {
            sb.append( c );
            if( c == ':' || ( i == attribData.length() - 1 )) {
                break;
            }

            c = attribData.charAt( ++i );
        }
        return( sb.toString() );
    }

    /**
     * Processes the command, emits the corresponding token, and returns the number of
     * chars in the lexeme.
     *
     * @param gdd GDD
     * @param tl TokenList to which the generated tokens are added
     * @param inCode Are we currently in a code section?
     * @return number of chars to skip to get past the command string in the input. If negative,
     * an error occurred.
     */
    public int process( final GDD gdd, final ParseContext context, final TokenList tl,
                        final boolean inCode )
    {
        /** the root that is going to be mapped to a replacement command root */
        final String aliasRoot = root;

        // insert the replacement command in the ParseContext and call the
        // replacement command's process() method.

        String newInputLine = TextTransforms.replaceSubstringAtLocation(
                context.getContent(), aliasRoot, replacementRoot, context.startPoint );

        ParseContext newContext = new ParseContext( gdd, context.source,
                                                    newInputLine, context.startPoint);

        ICommand com = cTable.getCommand( replacementRoot );
        if( com == null ) {
            // if the real command cannot be found, an error occurred. So, output the
            // invalid original alias root as text and advance by its length. This means
            // that a warning will be issued and the invalid command will appear in the text.
            gdd.logWarning( gdd.getLit( "FILE#" ) + context.source.getFileNumber() + " " +
                            gdd.getLit( "LINE#") + context.source.getLineNumber() + " "  +
                            gdd.getLit( "ERROR.INVALID_SUBSTITUTE_COMMAND_ROOT" ) + " " +
                            replacementRoot + ". " + gdd.getLit( "IGNORED" ));
            tl.add( new Token( context.source, TokenType.TEXT, aliasRoot ));
            return( aliasRoot.length() ); 
        }

        int i =  com.process( gdd, newContext, tl, inCode );

        int originalCharsProcessed = i - replacementRoot.length() + aliasRoot.length();

        // if we pass through the replaced command (a config-file switch, used mostly for listings),
        // then emit that here
        if( passthroughReplacedCommands( gdd )) {
            tl.add( new Token( context.source, TokenType.REPLACED_COMMAND,
                               context.getContent().substring( context.startPoint,
                                                               context.startPoint +
                                                                    originalCharsProcessed )));
        }

        return( originalCharsProcessed );
    }

    /**
     * Extract the root. We use the root-extraction routinein PlatypusParser.
     *
     * @param gdd the GDD
     * @param source the file and line # info
     * @param commandStr the substitute command from which to extract the root
     * @return  the root or empty string if an error occurred
     */
    public String extractRoot( final GDD gdd, final Source source, final String commandStr )
    {
        ParseContext context = new ParseContext( gdd, source, commandStr + '\n', 0 );
        PlatypusParser parser = new PlatypusParser( gdd );
        String root;

        try {
            root = parser.extractCommandRoot( context );
        }
        catch( IndexOutOfBoundsException iobe ) {
            return( "" );
        }

        return( root );
    }

    /**
     * Looks up "....process_replaced_commands" to see whether the plugin needs a copy
     * of the replaced command. Mostly used in listings.
     *
     * @param gdd the GDD*
     * @return true if they are expanded by Platypus (the defaul); else false.
     */
    boolean passthroughReplacedCommands( final GDD gdd )
    {
        boolean defaultValue = false;

        PropertyFile configFile = gdd.getConfigFile();
        if( configFile == null ) {
            return( defaultValue );
        }

        String pluginPrefix = gdd.getOutputPluginPrefix();
        String setting =
            configFile.lookup( "pi.out." + pluginPrefix + ".process_replaced_commands" );

        // default is fales
        if( setting != null && setting.equals( "yes" ) ) {
            return( true );
        }

        return( false );
    }

}