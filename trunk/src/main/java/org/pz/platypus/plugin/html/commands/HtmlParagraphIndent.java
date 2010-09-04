/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.html.commands;

import org.pz.platypus.Token;
import org.pz.platypus.interfaces.IOutputCommand;
import org.pz.platypus.interfaces.IOutputContext;
import org.pz.platypus.plugin.html.HtmlData;
import org.pz.platypus.utilities.Conversions;

/**
 * Implementation of indentation of a full paragraph for HTML plugin
 *
 * @author ask
 */
public class HtmlParagraphIndent implements IOutputCommand
{
    private String root = "[paraindent:";

    public int process( final IOutputContext context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        HtmlData htmlData = (HtmlData) context;

        float newIndent = Conversions.convertParameterToPoints( tok.getParameter(), htmlData );
        
        htmlData.setParagraphIndent( newIndent, tok.getSource() );

        // htmlData.getOutfile().generateStyleClassDefinition();

        return 0;
    }

    public String getRoot()
    {
        return( root );
    }
}