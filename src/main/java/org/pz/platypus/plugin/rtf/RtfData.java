/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-10 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.rtf;

import org.pz.platypus.GDD;
import org.pz.platypus.Source;
import org.pz.platypus.TypefaceMap;
import org.pz.platypus.DefaultValues;
import org.pz.platypus.plugin.DocData;
import org.pz.platypus.interfaces.OutputContextable;


/**
 * Container class for all the state of the RTF document
 *
 * @author alb
 */

public class RtfData extends DocData implements OutputContextable
{
    private GDD gdd;
    private RtfOutfile rtfOutfile;

    private boolean inCode = false;

    private Value eolTreatment;
    private Value firstLineIndent;
    private RtfFont font;
    private Value pixelsPerInch;
//    private ScriptEngine scriptEngine;
    private Value strikethru;
    private TypefaceMap typefaceMap;


    /**
     * Initializes the state of the PDF document.
     *
     * @param gddIn the GDD
     */
    public RtfData( final GDD gddIn )
    {
        super( gddIn );
        gdd = gddIn;
        eolTreatment    = new Value( DefaultValues.EOL_TREATMENT );
        font            = new RtfFont( this );
        pixelsPerInch   = new Value( DefaultValues.PIXELS_PER_INCH );

        typefaceMap     = new TypefaceMap( gdd );
    }

    /**
     * Some data items need additional initialization. This is not done in the
     * constructor, for testing purposes. This two-step process enables tests to
     * create a PdfData structure and do the complex initialization separately.
     */
    public void init()
    {

    }

    /**
     * Load the typefaces from the fontlist into the typefaceMap.
     */
    public void loadTypefaceMap()
    {
        typefaceMap.loadMapFromFile();
    }
    
    //=== getters and setters in alpha order by field  ===

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

    public RtfFont getFont()
    {
        return( font );
    }

    public void setFont( final RtfFont newFont )
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

    public boolean isInCode()
    {
        return( inCode );
    }

    public void setInCode( final boolean newState )
    {
        inCode = newState;
    }


    public RtfOutfile getOutfile()
    {
        return( rtfOutfile );
    }

    public void setOutfile( final RtfOutfile newOutfile)
    {
        rtfOutfile = newOutfile;
    }


    public float getPixelsPerInch()
    {
        return( pixelsPerInch.fval );
    }

    public Source getPixelsPerInchLine()
    {
        return( pixelsPerInch.source );
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