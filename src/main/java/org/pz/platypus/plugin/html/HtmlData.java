/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.html;

import org.apache.ecs.html.Body;
import org.pz.platypus.DefaultValues;
import org.pz.platypus.GDD;
import org.pz.platypus.Source;
import org.pz.platypus.TypefaceMap;
import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.plugin.common.DocData;

import java.util.*;

/**
 * Container class for all the state of the HTML document
 *
 * @author ask
 */

public class HtmlData extends DocData implements OutputContextable
{
    LinkedList<String> listAsStack = new LinkedList<String>();
    LinkedList<String> nestTagsList = new LinkedList<String>();

    public void configureBody(Body body) {
        int lmargin = (int) getMarginLeft();
        body.addAttribute( "leftmargin", String.valueOf(lmargin) + "px" );
    }

    public void push(String tok) {
        if (itIsAnOffCommand(tok)) {
            justPushIt(tok);
        } else if(isIsAnOnCommand(tok)) {
            popTillItMatches(tok);
        } else {
            /* do nothing - no nested tags handling */
        }
    }

    private void popTillItMatches(String tok) {
        LinkedList<String> nested = new LinkedList<String>();
        Iterator<String> iter = listAsStack.descendingIterator();
        while (iter.hasNext()) { 
            String curr = iter.next();
            if (matches(curr, tok)) {
                iter.remove();
                break;
            } else {
                nested.add(curr);
            }
        }
        generateNestedTags(nested, tok);
    }

    private void generateNestedTags(LinkedList<String> nested, String tok) {
        nestTagsList = new LinkedList();
        nestTagsList.add(tok);
        Iterator<String> iter = nested.descendingIterator();
        while(iter.hasNext()) {
            String elem = iter.next();
            String matching = substitutePlusWithMinus(elem);
            nestTagsList.addFirst(matching);
            nestTagsList.add(elem);
        }
    }

    private String substitutePlusWithMinus(String tag) {
        return tag.replaceAll("[+]", "-");
    }

    private boolean matches(String curr, String tok) {
        if (curr.length() < 4 || tok.length() < 4) {
            return false;
        }
        String subCurr = curr.substring(2);
        String subTok = tok.substring(2);
        return subCurr.equals(subTok);
    }

    private void justPushIt(String tok) {
        listAsStack.add(tok);
    }

    private boolean isIsAnOnCommand(String tok) {
        return tok.charAt(1) == '-';  
    }
    
    private boolean itIsAnOffCommand(String tok) {
        return tok.charAt(1) == '+';  
    }

    public String nestedTags() {
        String nestedTags = "";
        for(String elem : nestTagsList) {
            nestedTags += elem;
        }
        return nestedTags;
    }

    public List<OutputCommandable> getNestedTagCmds() {
        List<OutputCommandable> listOfCmds = new ArrayList<OutputCommandable>();
        for (String cmdName: nestTagsList) {
            // ??? lookup
        }
        return listOfCmds;
    }

    public void outputTags() {
        HtmlOutfile outfile = getOutfile();
        for (String cmdName: nestTagsList) {
            if (cmdName.equals( "[+b]" )) {
                outfile.emitText( "<b>" );
            } else if (cmdName.equals( "[-b]" )) {
                outfile.emitText( "</b>" );
            } else if (cmdName.equals( "[+i]" )) {
                outfile.emitText( "<I>" );
            } else if (cmdName.equals( "[-i]" )) {
                outfile.emitText( "</I>" );
            } else if (cmdName.equals( "[+st]" )) {
                outfile.emitText( "<s>" );
            } else if (cmdName.equals( "[-st]" )) {
                outfile.emitText( "</s>" );
            } else if (cmdName.equals( "[+u]" )) {
                outfile.emitText( "<u>" );
            } else if (cmdName.equals( "[-u]" )) {
                outfile.emitText( "</u>" );
            }
        }            
    }


    /**
     * Inner class containing the value of the data item, plus
     * the line at which it was las changed. Made static for
     * performance reasons (per FindBugs).
     */
    static class Value
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
    private HtmlOutfile pdfOutfile;

    private boolean inCode = false;

    private Value alignment;
    private Value columnCount;
    private Value userSpecifiedColumnWidth;
    private HtmlColumns columns;
    private int currColumn;
    private boolean eolPending;
    private Value eolTreatment;
    private Value firstLineIndent;
    private HtmlFont font;
    private HtmlFooter footer;
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
//    private ScriptEngine scriptEngine;
    private Value strikethru;
    private TypefaceMap typefaceMap;

    /**
     * Initializes the state of the PDF document.
     *
     * @param gddIn the GDD
     * @param commandTable
     */
    public HtmlData(final GDD gddIn, HtmlCommandTable commandTable)
    {
        gdd = gddIn;

        alignment       = new Value( DefaultValues.ALIGNMENT );

        columnCount     = new Value( DefaultValues.COLUMN_COUNT );
        currColumn      = 0;

        eolPending      = false;
        eolTreatment    = new Value( DefaultValues.EOL_TREATMENT );

        firstLineIndent = new Value( DefaultValues.FIRST_LINE_INDENT );
        font            = new HtmlFont( this );
        footer          = new HtmlFooter( this );

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
        userSpecifiedColumnWidth = new Value( DefaultValues.COLUMN_WIDTH );
    }

    /**
     * Some data items need additional initialization. This is not done in the
     * constructor, for testing purposes. This two-step process enables tests to
     * create a PdfData structure and do the complex initialization separately.
     */
    public void init()
    {
//        columns = new HtmlColumns( this );
//        typefaceMap.loadMapFromFile();
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
    void setValue( final Value field, final int val, final Source fileAndLine,
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
    void setValue( final Value field, final boolean val, final Source fileAndLine,
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

    public void setColumnCount( final int newCount, final Source fileAndLine )
    {
        setValue( columnCount, newCount, fileAndLine, "_columnCount" );
    }

    public void setColumns( final HtmlColumns newColumnsList )
    {
        columns = newColumnsList;
        columnCount = new Value( newColumnsList.size() );
    }

    public void setCurrColumn( int newColNumber )
    {
        currColumn = newColNumber;
    }

    public int getEolTreatment()
    {
        return( eolTreatment.ival );
    }

    public float getFirstLineIndent()
    {
        return( firstLineIndent.fval );
    }

    public void setFirstLineIndent( final float newFirstLineIndent, final Source fileAndLine )
    {
        setValue( firstLineIndent, newFirstLineIndent, fileAndLine, "_firstLineIndent" );
    }

    public HtmlFont getFont()
    {
        return( font );
    }

    public String getFontFace()
    {
        return( font.getFace() );
    }

    public float getFontSize()
    {
        return( font.getSize() );
    }

    public HtmlFooter getFooter()
    {
        return( footer );
    }

    public void setFooter( final HtmlFooter newFooter )
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

    public Source getNoIndentLine()
    {
        return( noIndent.source );
    }

    public void setNoIndent( final boolean trueFalse, final Source fileAndLine )
    {
        setValue( noIndent, trueFalse, fileAndLine, "_noIndent" );
    }

    public HtmlOutfile getOutfile()
    {
        return( pdfOutfile );
    }

    public void setOutfile( final HtmlOutfile newOutfile)
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

    public float getPageWidth()
    {
        return pageWidth.fval;
    }

    public void setPageWidth( final float val, final Source fileAndLine )
    {
        setValue( pageWidth, val, fileAndLine, "_pageWidth" );
    }

    public float getPixelsPerInch()
    {
        return( pixelsPerInch.fval );
    }

    public TypefaceMap getTypefaceMap()
    {
        return( typefaceMap );
    }

    public float getUserSpecifiedColumnWidth()
    {
        return( userSpecifiedColumnWidth.fval );
    }

    public void setUserSpecifiedColumnWidth( final float newWidth, final Source fileAndLine )
    {
        setValue( userSpecifiedColumnWidth, newWidth, fileAndLine, "_userSpecifiedColumnWidth" );
    }
}