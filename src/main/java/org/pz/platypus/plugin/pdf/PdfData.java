/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-10 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf;

import org.pz.platypus.GDD;
import org.pz.platypus.Source;
import org.pz.platypus.TypefaceMap;
import org.pz.platypus.DefaultValues;
import org.pz.platypus.plugin.DocData;
import org.pz.platypus.interfaces.OutputContextable;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Container class for all the state of the PDF document
 *
 * @author alb
 */

public class PdfData extends DocData implements OutputContextable
{
    private PdfOutfile pdfOutfile;

    private GDD gdd;
    private boolean eolPending;
    private Value eolTreatment;
    private PdfFont font;
    private Footer footer;
    private FormatStack formatStack;
    private TypefaceMap typefaceMap;

    /**
     * Initializes the state of the PDF document.
     *
     * @param gddIn the GDD
     */
    public PdfData( final GDD gddIn )
    {
        super( gddIn );
        gdd = gddIn;

        eolPending      = false;
        eolTreatment    = new Value( DefaultValues.EOL_TREATMENT );

        font            = new PdfFont( this );
        footer          = new Footer( this );

        typefaceMap     = new TypefaceMap( getGdd() );

        underline       = new Underline();
    }

    /**
     * Some data items need additional initialization. This is not done in the
     * constructor, for testing purposes. This two-step process enables tests to
     * create a PdfData structure and do the complex initialization separately.
     */
    public void init()
    {
        columns = new Columns( this );
        loadTypefaceMap();
        formatStack = new FormatStack( this );
    }

    /**
     * Load the typefaces from the fontlist into the typefaceMap.
     */
    public void loadTypefaceMap()
    {
        typefaceMap.loadMapFromFile();
    }


    public ScriptEngine createNewScriptEngine()
    {
        ScriptEngineManager sem = new ScriptEngineManager();
    //    ScriptEngine se = sem.getEngineByName( "Groovy Scripting Engine" );
        ScriptEngine se = sem.getEngineByName( "groovy" );
        if( se == null ) {
            gdd.logWarning( gdd.getLit( "ERROR.SCRIPT_ENGINE_NOT_FOUND" ));
            return( null );
        }

//      For debugging: lists the names of the available script engines
//        System.out.println( "Available factories" );
//        for( ScriptEngineFactory f : sem.getEngineFactories()) {
//            System.out.println( f.getEngineName() );
//        }

        return( se );
    }

    //=== getters and setters in alpha order by field  ===


    public boolean getEolPending()
    {
        return( eolPending );
    }

    public void setEolPending( final boolean newEol )
    {
        // newEol = false only when resetting flag; there is
        // no user command to do this. It's only done as a
        // result of processing EOL.
        if( ! newEol ) {
            eolPending = false;
            return;
        }

        // [cr] has been encountered
        if( ! eolPending ) {
            eolPending = true;
            return;
        }

        // If there's an EOL already pending, then the second one means a new paragraph.
        // However, if paragraph == null, iText needs a single blank space before skipping the line.
        if( pdfOutfile.getItPara() == null ) {
            pdfOutfile.emitText( " " );
        }

        pdfOutfile.startNewParagraph();
        eolPending = false;
    }

    public int getEolTreatment()
    {
        return( eolTreatment.ival );
    }

    public Source getEolTreatmentLine()
    {
        return( eolTreatment.source );
    }

    public void setEolTreatment( final int newEolTreatment, final Source fileAndLine )
    {
        setValue( eolTreatment, newEolTreatment, fileAndLine, "_EOL_handling" );
    }


    public PdfFont getFont()
    {
        return( font );
    }

    public void setFont( final PdfFont newFont )
    {
        font = newFont;
    }

    public String getFontFace()
    {
        return( font.getFace() );
    }

    public void setFontFace( final String newFace, final Source newSource )
    {
        font.setFace( newFace, newSource );
    }

    public float getFontSize()
    {
        return( font.getSize() );
    }

    public void setFontSize( final float newSize, final Source newSource )
    {
        font.setSize( newSize, newSource );
    }

    public Footer getFooter()
    {
        return( footer );
    }

    public void setFooter( final Footer newFooter )
    {
        footer = newFooter;
    }

    // used principally (exclusively?) for testing
    public void setFormatStack( final FormatStack formats )
    {
        formatStack = formats;
    }

    public FormatStack getFormatStack()
    {
        return( formatStack );
    }

    public PdfOutfile getOutfile()
    {
        return( pdfOutfile );
    }

    public void setOutfile( final PdfOutfile newOutfile)
    {
        pdfOutfile = newOutfile;
    }

    public TypefaceMap getTypefaceMap()
    {
        return( typefaceMap );
    }

    public void setTypefaceMap( final TypefaceMap tfMap )
    {
        typefaceMap = tfMap;
    }
}
