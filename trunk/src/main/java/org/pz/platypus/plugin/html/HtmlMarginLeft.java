/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.html;

import org.pz.platypus.ParamType;
import org.pz.platypus.Token;
import org.pz.platypus.utilities.Conversions;
import org.pz.platypus.interfaces.IOutputCommand;
import org.pz.platypus.interfaces.IOutputContext;

/**
 * handles processing of the definition of a user string (macro).
 * Handled by the plug-in because the same value can be redefined
 * numerous times by the Platypus file in context-dependent ways.
 *
 * @author ask
 */
public class HtmlMarginLeft implements IOutputCommand
{
    private String root = "[lmargin:";

    private ParamType parameterType = ParamType.STRING;

    private boolean validInCode = false;

    public int process( final IOutputContext context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }
        HtmlData htmlData = (HtmlData) context;

        float lMargin = Conversions.convertParameterToPoints( tok.getParameter(), htmlData );

        float currLMargin = htmlData.getMarginLeft();

        if ( lMargin != currLMargin ) {
            htmlData.setMarginLeft( lMargin, tok.getSource() );
            return 0;
        }
        return 0;
    }

    public String getRoot()
    {
        return( root );
    }


}