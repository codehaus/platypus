/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.Token;
import org.pz.platypus.command.EolTreatment;
import org.pz.platypus.plugin.pdf.*;

/**
 * Begin a code section or code listing
 *
 * @author alb
 */
public class PdfCodeOn implements OutputCommandable
{
    private String root = "[code]";

    public void process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdd = (PdfData) context;
        if( pdd.inCodeSection() ) {
            return; //already in a code section
        }

        // save the current format
        PdfSaveFormat.save( pdd );
        
        // now switch to code format
        PdfFont currFont = (PdfFont) pdd.getFont().clone();
        currFont.setFace( "Bitstream Vera Sans Mono", tok.getSource() );
        currFont.setSize( currFont.getSize() * 0.85f, tok.getSource() );
        currFont.setBold( false, tok.getSource() );
        currFont.setItalics( false, tok.getSource() );
        pdd.setFont( currFont );
        
        pdd.setStrikethru( false, tok.getSource() );
        setUnderlineToFalse( pdd, tok );
        setEolTreatmentToHard( pdd, tok );

        pdd.setInCodeSection( true, tok.getSource() );
    }

    private void setEolTreatmentToHard( final PdfData pdd, Token token )
    {
        EolTreatment eolHandler = new EolTreatment();
        int hardEol = eolHandler.toInteger( "hard" );
        if( pdd.getEolTreatment() != hardEol ) {
            pdd.setEolTreatment( hardEol, token.getSource());
        }
    }

    private void setUnderlineToFalse( final PdfData pdd, Token token )
    {
        Underline underline = pdd.getUnderline();
        underline.setInEffect( false, token.getSource() );
    }

    public String getRoot()
    {
        return( root );
    }
}