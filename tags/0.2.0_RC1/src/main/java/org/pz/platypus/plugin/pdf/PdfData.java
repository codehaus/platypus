/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf;

import org.pz.platypus.GDD;
import org.pz.platypus.Source;
import org.pz.platypus.TypefaceMap;
import org.pz.platypus.interfaces.OutputContextable;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Container class for all the state of the PDF document
 *
 * @author alb
 */

public class PdfData implements OutputContextable
{
    /**
     * Inner class containing the value of the data item, plus
     * the line at which it was las changed.
     */
    class Value
    {
        boolean bval;
        float fval;
        int ival;
        String sval;

        Source source;

        public Value( final boolean bool )
        {
            bval = bool;
            source = new Source( 0, 0 );
        }

        public Value( final float val )
        {
            fval = val;
            source = new Source( 0, 0 );
        }

        public Value( final int integer )
        {
            ival = integer;
            source = new Source( 0, 0 );
        }

        public Value( final float val, final Source fileAndLine )
        {
            fval = val;
            source = fileAndLine.clone();
        }

        public Value( final String val, final Source fileAndLine )
        {
            sval = val;
            source = fileAndLine.clone();
        }

        public Value( final boolean yn, final Source fileAndLine )
        {
            bval = yn;
            source = fileAndLine.clone();
        }
    }

    private GDD gdd;
    private PdfOutfile pdfOutfile;

    private boolean inCode = false;

    private Value alignment;
    private Value columnCount;
    private Value columnWidth;
    private Columns columns;
    private int currColumn;
    private boolean eolPending;
    private Value eolTreatment;
    private Value firstLineIndent;
    private PdfFont font;
    private Footer footer;
    private Value leading;
    private Value lineHeight;   // not the same as leading
    private Value marginBottom;
    private Value marginLeft;
    private Value marginRight;
    private Value marginTop;
    private Value marginsMirrored;
    private Value noIndent;
    private Value pageHeight;
    private int   pageNumber;
    private Value pageWidth;
    private Value paragraphIndent;
    private Value paragraphIndentRight;
    private Value paragraphSkip;
    private Value pixelsPerInch;
    private ScriptEngine scriptEngine;
    private Value strikethru;
    private TypefaceMap typefaceMap;
    private Underline underline;

    /**
     * Initializes the state of the PDF document.
     *
     * @param gddIn the GDD
     */
    public PdfData( final GDD gddIn )
    {
        gdd = gddIn;

        alignment       = new Value( DefaultValues.ALIGNMENT );

        columnCount     = new Value( DefaultValues.COLUMN_COUNT );
        columnWidth     = new Value( DefaultValues.COLUMN_WIDTH );
        currColumn      = 0;

        eolPending      = false;
        eolTreatment    = new Value( DefaultValues.EOL_TREATMENT );

        firstLineIndent = new Value( DefaultValues.FIRST_LINE_INDENT );
        font            = new PdfFont( this );
        footer          = new Footer( this );

        leading         = new Value( DefaultValues.LEADING );
        lineHeight      = new Value( DefaultValues.LEADING );

        marginBottom    = new Value( DefaultValues.MARGIN );
        marginLeft      = new Value( DefaultValues.MARGIN );
        marginRight     = new Value( DefaultValues.MARGIN );
        marginTop       = new Value( DefaultValues.MARGIN );
        marginsMirrored = new Value( DefaultValues.MARGINS_MIRRORED );

        noIndent        = new Value( DefaultValues.NO_INDENT );

        pageHeight      = new Value( DefaultValues.PAGE_HEIGHT );
        pageNumber      = 0;
        pageWidth       = new Value( DefaultValues.PAGE_WIDTH );
        paragraphIndent = new Value( DefaultValues.PARA_INDENT );
        paragraphIndentRight
                        = new Value( DefaultValues.PARA_INDENT_RIGHT );
        paragraphSkip   = new Value( DefaultValues.PARA_SKIP_LINES );
        pixelsPerInch   = new Value( DefaultValues.PIXELS_PER_INCH );

//        scriptEngine    = createNewScriptEngine();
        strikethru      = new Value( false );
        typefaceMap     = new TypefaceMap( gdd );
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
        typefaceMap.loadMapFromFile();
    }

    /* Note: the following setters only update Value fields if the new value is different
     * from the existing one. If they're not different, no update occurs, which means no
     * modification to the Source field in the Value object occurs either.
     */

    /**
     * Sets the new float value, updates line number, and table in systemStrings
     * @param field the Value field to be updated
     * @param val the new floating-point value
     * @param fileAndLine the file and line number where the change occurred
     * @param name the key to look up the string in systemStrings
     */
    void setValue( final Value field, final float val, final Source fileAndLine,
                        final String name )
    {
        if( val != field.fval ) {
            field.fval = val;
            field.source = fileAndLine.clone();
            gdd.getSysStrings().add( name, Float.toString( val ));
        }
    }

    /**
     * Sets the new int value, updates line number, and table in systemStrings
     * @param field the Value field to be updated
     * @param val the new integer value
     * @param fileAndLine the file and line number where the change occurred
     * @param name the key to look up the string in systemStrings
     */
    void setValue( final Value field, final int val, final Source fileAndLine,
                        final String name )
    {
        if( val != field.fval ) {
            field.ival = val;
            field.source = fileAndLine.clone();
            gdd.getSysStrings().add( name, Integer.toString( val ));
        }
    }

    /**
     * Sets the new boolean value, updates line number, and table in systemStrings
     * @param field the Value field to be updated
     * @param val the new floating-point value
     * @param fileAndLine the file and line number where the change occurred
     * @param name the key to look up the string in systemStrings
     */
    void setValue( final Value field, final boolean val, final Source fileAndLine,
                        final String name )
    {
        if( val != field.bval ) {
            field.bval = val;
            field.source = fileAndLine.clone();
            gdd.getSysStrings().add( name, Boolean.toString( val ));
        }
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

    public int getAlignment()
    {
        return( alignment.ival );
    }

    public Source getAlignmentLine()
    {
        return( alignment.source );
    }

    public void setAlignment( final int newAlignment, final Source fileAndLine )
    {
        setValue( alignment, newAlignment, fileAndLine, "_alignment" );
    }

    public int getColumnCount()
    {
        return columnCount.ival;
    }

    public Source getColumnCountLine()
    {
        return columnCount.source;
    }

    public void setColumnCount( final int newCount, final Source fileAndLine )
    {
        setValue( columnCount, newCount, fileAndLine, "_columnCount" );
    }

    public float getColumnWidth()
    {
        return( columnWidth.fval );
    }

    public Source getColumnWidthLine()
    {
        return( columnWidth.source );
    }

    public void setColumnWidth( final float newWidth, final Source fileAndLine )
    {
        setValue( columnWidth, newWidth, fileAndLine, "_columnWidth" );
    }

    public Columns getColumns()
    {
        return( columns );
    }

    public void setColumns( final Columns newColumnsList )
    {
        columns = newColumnsList;
        columnCount = new Value( newColumnsList.size() );
    }

    public int getCurrColumn()
    {
        return( currColumn );
    }

    public void setCurrColumn( int newColNumber )
    {
        currColumn = newColNumber;
    }

    public boolean getEolPending()
    {
        return( eolPending );
    }

    public void setEolPending( final boolean newEol )
    {
        // newEol = false only when resetting flag; there is
        // no user command to do this. It's only done as a
        // result of processing EOL.
        if( newEol == false ) {
            eolPending = false;
            return;
        }

        // [cr] has been encountered
        if( eolPending == false ) {
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

    public float getFirstLineIndent()
    {
        return( firstLineIndent.fval );
    }

    public Source getFirstLineIndentLine()
    {
        return( firstLineIndent.source );
    }

    public void setFirstLineIndent( final float newFirstLineIndent, final Source fileAndLine )
    {
        setValue( firstLineIndent, newFirstLineIndent, fileAndLine, "_firstLineIndent" );
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

    public GDD getGdd()
    {
        return( gdd );
    }

    public boolean isInCode()
    {
        return( inCode );
    }

    public void setInCode( final boolean newState )
    {
        inCode = newState;
    }

    public float getLeading()
    {
        return( leading.fval );
    }

    public Source getLeadingLine()
    {
        return( leading.source );
    }

    public void setLeading( final float newLeading, final Source fileAndLine )
    {
        setValue( leading, newLeading, fileAndLine, "_leading" );
    }

    public float getLineHeight()
    {
        return( lineHeight.fval );
    }

    public Source getLineHeightLine()
    {
        return( lineHeight.source );
    }

    public void setLineHeight( final float newLineHeight, final Source fileAndLine )
    {
        setValue( lineHeight, newLineHeight, fileAndLine, "_lineHeight" );
    }

    public float getMarginBottom() {
        return marginBottom.fval;
    }

    public Source getMarginBottomLine() {
        return marginBottom.source;
    }

    public void setMarginBottom( final float val, final Source fileAndLine )
    {
        setValue( marginBottom, val, fileAndLine, "_marginBottom" );
    }

    public float getMarginLeft() {
        return marginLeft.fval;
    }

    public Source getMarginLeftLine() {
        return marginLeft.source;
    }

    public void setMarginLeft( final float val, final Source fileAndLine )
    {
        setValue( marginLeft, val, fileAndLine, "_marginLeft" );
    }

    public float getMarginRight()
    {
        return marginRight.fval;
    }

    public Source getMarginRightLine() {
        return marginRight.source;
    }

    public void setMarginRight( final float val, final Source fileAndLine )
    {
        setValue( marginRight, val, fileAndLine, "_marginRight" );
    }

    public float getMarginTop()
    {
        return marginTop.fval;
    }

    public Source getMarginTopLine()
    {
        return marginTop.source;
    }

    public void setMarginTop( final float val, final Source fileAndLine )
    {
        setValue( marginTop, val, fileAndLine, "_marginTop" );
    }

    public boolean getMarginsMirrored() {
        return( marginsMirrored.bval );
    }

    public Source getMarginsMirroredLine() {
        return( marginsMirrored.source );
    }

    public void setMarginsMirrored( final boolean trueFalse, final Source fileAndLine )
    {
        setValue( marginsMirrored, trueFalse, fileAndLine, "_marginsMirrored" );
    }

    public boolean getNoIndent()
    {
        return( noIndent.bval );
    }

    public Source getNoIndentLine()
    {
        return( noIndent.source );
    }

    public void setNoIndent( final boolean trueFalse, final Source fileAndLine )
    {
        setValue( noIndent, trueFalse, fileAndLine, "_noIndent" );
    }

    public PdfOutfile getOutfile()
    {
        return( pdfOutfile );
    }

    public void setOutfile( final PdfOutfile newOutfile)
    {
        pdfOutfile = newOutfile;
    }

    public float getPageHeight()
    {
        return pageHeight.fval;
    }

    public Source getPageHeightLine()
    {
        return pageHeight.source;
    }

    public void setPageHeight( final float val, final Source fileAndLine )
    {
        setValue( pageHeight, val, fileAndLine, "_pageHeight" );
    }

    public int getPageNumber()
    {
        return( pageNumber );
    }

    public void setPageNumber( final int newPageNumber )
    {
        pageNumber = newPageNumber;
    }

    public float getPageWidth()
    {
        return pageWidth.fval;
    }

    public Source getPageWidthLine()
    {
        return pageWidth.source;
    }

    public void setPageWidth( final float val, final Source fileAndLine )
    {
        setValue( pageWidth, val, fileAndLine, "_pageWidth" );
    }

    public float getParagraphIndent()
    {
        return( paragraphIndent.fval );
    }

    public Source getParagraphIndentLine()
    {
        return( paragraphIndent.source );
    }

    public void setParagraphIndent( final float newParagraphIndent, final Source fileAndLine )
    {
        setValue( paragraphIndent, newParagraphIndent, fileAndLine, "_paragraphIndent" );
    }

    public float getParagraphIndentRight()
    {
        return( paragraphIndentRight.fval );
    }

    public Source getParagraphIndentRightLine()
    {
        return( paragraphIndentRight.source );
    }

    public void setParagraphIndentRight( final float newParagraphIndentRight,
                                         final Source fileAndLine )
    {
        setValue( paragraphIndentRight, newParagraphIndentRight, fileAndLine,
                  "_paragraphIndentRight" );
    }

    public float getParagraphSkip()
    {
        return( paragraphSkip.fval );
    }

    public int getParagraphSkipLine()
    {
        return( paragraphSkip.source.getLineNumber() );
    }

    public void setParagraphSkip( final float val, final Source fileAndLine )
    {
        setValue( paragraphSkip, val, fileAndLine, "_paragraphSkip" );
    }

    public float getPixelsPerInch()
    {
        return( pixelsPerInch.fval );
    }

    public Source getPixelsPerInchLine()
    {
        return( pixelsPerInch.source );
    }

    public ScriptEngine getScriptEngine()
    {
        return( scriptEngine );
    }

    public boolean getStrikethru()
    {
        return( strikethru.bval );
    }

    public int getStrikethruLine()
    {
        return( strikethru.source.getLineNumber() );
    }
    public void setStrikethru( boolean newStrikethru, final Source fileAndLine )
    {
        setValue( strikethru, newStrikethru, fileAndLine, "_strikethru" );
    }

    public TypefaceMap getTypefaceMap()
    {
        return( typefaceMap );
    }

    public void setTypefaceMap( final TypefaceMap tfMap )
    {
        typefaceMap = tfMap;
    }

    public Underline getUnderline()
    {
        return( underline );
    }
}
