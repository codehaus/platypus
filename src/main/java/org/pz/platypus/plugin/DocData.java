/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-10 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin;

import org.pz.platypus.GDD;
import org.pz.platypus.Source;
import org.pz.platypus.DefaultValues;
import org.pz.platypus.plugin.pdf.*;
import org.pz.platypus.interfaces.OutputContextable;

/**
 * Abstract container class for all the state of the output document.
 * Derived classes are created for the PDF, RTF, etc. plugins.
 *
 * @author alb
 */

public abstract class DocData implements OutputContextable
{
    /**
     * Inner class containing the value of the data item, plus
     * the line at which it was las changed. Made static for
     * performance reasons (per FindBugs).
     */
    public static class Value
    {
        public boolean bval;
        public float fval;
        public int ival;
        public String sval;

        public Source source;

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
            try {
                source = fileAndLine.clone();
            }
            catch( CloneNotSupportedException clnse ){
                source = new Source();
            }
        }

        public Value( final String val, final Source fileAndLine )
        {
            sval = val;
            try {
                source = fileAndLine.clone();
            }
            catch( CloneNotSupportedException clnse ){
                source = new Source();
            }
        }

        public Value( final boolean yn, final Source fileAndLine )
        {
            bval = yn;
            try {
                source = fileAndLine.clone();
            }
            catch( CloneNotSupportedException clnse ){
                source = new Source();
            }
        }
    }

    private GDD gdd;

    private Value alignment;
    private Value codeSection;
    private Value columnCount;
    private Value userSpecifiedColumnWidth;
    public  Columns columns;
    private int currColumn;
    private Value firstLineIndent;
    private Value leading;
    private Value lineHeight;   // not the same as leading
    private Value lineNumberSkip;
    private Value lineNumberLast;
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
    private Value strikethru;
    protected Underline underline;

    public DocData()
    {

    }
    /**
     * Initializes the state of the PDF document.
     *
     * @param gddIn the GDD
     */
    public DocData( final GDD gddIn )
    {
        gdd = gddIn;

        alignment       = new Value( DefaultValues.ALIGNMENT );

        codeSection     = new Value( false );
        columnCount     = new Value( DefaultValues.COLUMN_COUNT );
        currColumn      = 0;

        firstLineIndent = new Value( DefaultValues.FIRST_LINE_INDENT );

        leading         = new Value( DefaultValues.LEADING );
        lineHeight      = new Value( DefaultValues.LEADING );
        lineNumberLast  = new Value( DefaultValues.LINE_NUMBER_LAST );
        lineNumberSkip  = new Value( DefaultValues.LINE_NUMBER_SKIP );

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

        strikethru      = new Value( false );
        underline       = new Underline();
        userSpecifiedColumnWidth = new Value( DefaultValues.COLUMN_WIDTH );
    }

    /**
     * Some data items need additional initialization. This is not done in the
     * constructor, for testing purposes. This two-step process enables tests to
     * create a structure and do the complex initialization separately.
     */
    public void init()
    {
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
    public void setValue( final Value field, final float val, final Source fileAndLine,
                        final String name )
    {
        if( val != field.fval ) {
            field.fval = val;
            try {
                field.source = fileAndLine.clone();
            }
            catch( CloneNotSupportedException clnse ){
                field.source = new Source();
            }
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
    public void setValue( final Value field, final int val, final Source fileAndLine,
                        final String name )
    {
        if( val != field.fval ) {
            field.ival = val;
            try {
                field.source = fileAndLine.clone();
            }
            catch( CloneNotSupportedException clnse ){
                field.source = new Source();
            }
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
    public void setValue( final Value field, final boolean val, final Source fileAndLine,
                        final String name )
    {
        if( val != field.bval ) {
            field.bval = val;
            try {
                field.source = fileAndLine.clone();
            }
            catch( CloneNotSupportedException clnse ){
                field.source = new Source();
            }
            gdd.getSysStrings().add( name, Boolean.toString( val ));
        }
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

    public boolean inCodeSection()
    {
        return( codeSection.bval );
    }

    public Source getCodeSectionLine()
    {
        return( codeSection.source );
    }

    public void setInCodeSection( final boolean inCode, final Source fileAndLine )
    {
        setValue( codeSection, inCode, fileAndLine, "_inCodeSection" );
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

    public GDD getGdd()
    {
        return( gdd );
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

    public int getLineNumberLast()
    {
        return lineNumberLast.ival;
    }

    public Source getLineNumberLastLine()
    {
        return lineNumberLast.source;
    }

    public void setLineNumberLast( final int newLastLineNumber, final Source fileAndLine )
    {
        setValue( lineNumberLast, newLastLineNumber, fileAndLine, "_lastLineNumber" );
    }

    public int getLineNumberSkip()
    {
        return lineNumberSkip.ival;
    }

    public Source getLineNumberSkipLine()
    {
        return lineNumberSkip.source;
    }

    public void setLineNumberSkip( final int newLineNumberSkip, final Source fileAndLine )
    {
        setValue( lineNumberSkip, newLineNumberSkip, fileAndLine, "_lineNumberSkip" );
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

    public Underline getUnderline()
    {
        return( underline );
    }

    public float getUserSpecifiedColumnWidth()
    {
        return( userSpecifiedColumnWidth.fval );
    }

    public Source getUserSpecifiedColumnWidthLine()
    {
        return( userSpecifiedColumnWidth.source );
    }

    public void setUserSpecifiedColumnWidth( final float newWidth, final Source fileAndLine )
    {
        setValue( userSpecifiedColumnWidth, newWidth, fileAndLine, "_userSpecifiedColumnWidth" );
    }
}