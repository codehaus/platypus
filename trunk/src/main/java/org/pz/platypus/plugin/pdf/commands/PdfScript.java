/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.Token;
import org.pz.platypus.interfaces.IOutputCommand;
import org.pz.platypus.interfaces.IOutputContext;

/**
 * Handles script (in Groovy) for PDF plugin
 *
 * @author alb
 */
public class PdfScript implements IOutputCommand
{
    private String root = "[@:";

    public int process( final IOutputContext context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null || tok.getParameter().getString() == null ) {
            throw new IllegalArgumentException();
        }

//        PdfData pdfData = (PdfData) context;
//
//        String script = tok.getParameter().getString();
//        if( script.endsWith( "@" )) {
//            script = script.substring( 0, script.length() - 1 );
//        }

//        ScriptEngine engine = pdfData.getScriptEngine();
//        if( engine == null ) {
//            return;
//        }

//        try {
//            engine.eval( script );
//        }
//        catch ( ScriptException sex ) {
//            pdfData.getGdd().logWarning(
//                    pdfData.getGdd().getLit( "LINE#" ) + tok.getSource().getLineNumber() + " " +
//                    pdfData.getGdd().getLit( "ERROR.MALFORMED_SCRIPT" ));
//        }
        return 0;
    }


    public String getRoot()
    {
        return( root );
    }
}