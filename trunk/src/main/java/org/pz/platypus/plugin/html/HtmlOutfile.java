/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.html;

import org.apache.ecs.Doctype;
import org.apache.ecs.ECSDefaults;
import org.apache.ecs.XhtmlDocument;
import org.apache.ecs.xhtml.body;
import org.apache.ecs.xhtml.comment;
import org.apache.ecs.xhtml.meta;
import org.apache.ecs.xhtml.title;
import org.pz.platypus.GDD;
import org.pz.platypus.exceptions.FileCloseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;


/**
 * Manage output to the HTML file
 *
 * @author ask
 */
public class HtmlOutfile
{
    private boolean openStatus = false;

    /** the HTML data structure */
    HtmlData htmlData;

    private String htmlFileName;
    private PrintStream htmlFile;

    XhtmlDocument htmlDoc = new XhtmlDocument();
    body htmlBody = new body();

    private boolean inParagraph = false;
    private int tab = 0;
    private boolean inFont = false;

    public HtmlOutfile()
    {
        openStatus = false;

        configureHtmlPrettyPrint();
        configureHead();

        Doctype doctype = new Doctype.XHtml10Transitional();
        htmlDoc.setDoctype(doctype);

        htmlBody = htmlDoc.getBody();
        htmlBody.setPrettyPrint( true );
    }

    /**
     * The "once only" settings - We set <br>
     * 1. The title <br>
     * 2. The content-type <br>
     * 3. A comment to note that this doc was generated with platypus <br> 
     * 
     */
    private void configureHead() {
        htmlDoc.setTitle(new title("Html Doc"));
        meta m = new meta();
        m.setHttpEquiv("Content-Type");
        m.setContent("text/html;charset=utf-8");
        htmlDoc.getHead().addElement(m);
        htmlDoc.getHead().addElement(new comment("Generated by Platypus - http://platypus.pz.org"));
    }

    /** A hack to pretty-print the generated XHTML.
     *
     */
    private void configureHtmlPrettyPrint() {
        try {
            Field fld = ECSDefaults.class.getDeclaredField("defaults");
            fld.setAccessible(true);
            ECSDefaults ed = (ECSDefaults) fld.get(null);
            Field pp = ECSDefaults.class.getDeclaredField("pretty_print");
            pp.setAccessible(true);
            pp.setBoolean(ed, Boolean.TRUE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Open the output file. Does basic checks, calls openPdfFile(), and handles any exceptions
     * @param gdd Global document data
     * @param filename name of file to open
     * @param htmlData
     * @throws java.io.IOException in event of a problem opening the file
     */
    public void open( final GDD gdd, final String filename, final HtmlData htmlData) throws IOException
    {
        assert( gdd != null );

        if( filename == null || filename.isEmpty() ) {
            gdd.logSevere( gdd.getLit( "ERROR.MISSING_INPUT_FILE" ) + " " +
                                       gdd.getLit( "EXITING" ));
            throw new IOException();
        }

        try {
            htmlFileName = filename;
            openHtmlFile(filename);
        }
        catch( IOException ioe ) {
            gdd.logSevere( gdd.getLit( "ERROR.OPENING_OUTPUT_FILE" ) + ": " +
                    filename + " " + gdd.getLit( "EXITING" ));
            throw new IOException();
        }

        gdd.log( "Opened HTML output file:" + " " + filename );
    }

    private void openHtmlFile(String filename) throws FileNotFoundException {
        htmlFile = new PrintStream(filename);
        openStatus = true;
    }

    /**
     * Outputs any material that has not yet been output. Then closes the xhtml Document.
     *
     * @throws org.pz.platypus.exceptions.FileCloseException if an error occurs closing the file
     */
    public void close() throws FileCloseException
    {
        if( ! openStatus ) {
            GDD gdd = htmlData.getGdd();
            gdd.logWarning( gdd.getLit( "NO_OUTPUT_FILE" ));
            return;
        }
        
        // now, we can close (note: following steps must occur in this order)
        openStatus =  false;
        try {
            htmlFile.println(htmlDoc.toString());
            htmlFile.flush();
            htmlFile.close();
        }
        catch( Exception ex ) {
            throw new FileCloseException( htmlFileName );
        }
    }

    /**
     * Starts a new html Paragraph.
     * This does two things...
     * Outputs the paragraph tag <p>.
     * Records the fact that we have started a paragraph.  
     */
    public void startNewParagraph()
    {
        endFontTagIfAny();
        emitText("<p>");
        setInParagraph(true);
        restartFontTagIfAny();        
    }

    public void endCurrentParagraphIfAny() {
        if (getInParagraph()) {
            endFontTagIfAny();
            emitText("</p>");
            setInParagraph(false);
            restartFontTagIfAny();
        }
    }

    public boolean getInParagraph() {
        return inParagraph;
    }
    
    private void setInParagraph(boolean b) {
        inParagraph = b;
    }

    /**
     * Writes text to the HTML file.            \
     * TODO: handle quoting of special characters here...
     * TODO: maybe extract this out in a special class of its own?
     *
     * @param s the text to be written
     */
    public void emitText( String s )
    {
        String tabSpace = getCurrentTabbedOutSpaces();
        if (!tabSpace.isEmpty()) {
            htmlBody.addElement(tabSpace);
        }
        htmlBody.addElement(s);
    }

    /** Am not so sure we need this one - as the pretty print method is already used...
     *
     * @return
     */
    private String getCurrentTabbedOutSpaces() {
        String spaces = "";
        for (int i = 0; i < tab; ++i) {
            spaces += " "; 
        }
        return spaces;
    }

    public boolean isOpen()
    {
        return( openStatus );
    }

    public void setHtmlData( final HtmlData htmlData )
    {
        this.htmlData = htmlData;
    }

    /**
     * Useful utility method to handle double quotes within
     * String literals -
     * @param s
     * @return
     */
    private String wrapInQuotes(String s) {
        return "\"" + s + "\"";
    }

    public void emitFontSizeTag() {
        endFontTagIfAny();
        startNewFontSize();
        inFont = true;
    }

    private void startNewFontSize() {
        emitText("<font size=");
        String fontSize = String.valueOf(htmlData.getFontSize());
        emitText(wrapInQuotes(fontSize));
        emitText(">");
    }

    /**
     * If we have started a paragraph, or a font or any other tag - we need to close it.
     * This handles the closing up of any open tags...
     */
    public void handleEof() {
        if (getInParagraph()) {
            endCurrentParagraphIfAny(); // this handles leftover ending of font tags ( if any ;-)
        }
        endFontTagIfAny();
    }

    private boolean alreadyProcessingFont() {
        return inFont;    
    }

    public void setMarginTop() {
        int marginTop = (int) htmlData.getMarginTop();
        htmlBody.addAttribute("TOPMARGIN", marginTop);
    }

    public void handleNewFontFace() {
        startNewFontFace();
        inFont = true;
    }

    private void restartFontTagIfAny() {
        emitText("<font face=");
        String fontFace = htmlData.getFontFace();
        fontFace = wrapInQuotes(fontFace);
        emitText(fontFace);
        emitText(">");
    }

    private void endFontTagIfAny() {
        if (alreadyProcessingFont()) {
            emitText("</font>");
        }
    }

    /**
     * We need to close previous font (if any) - and output a new
     * font tag
     * 
     */
    private void startNewFontFace() {
        endFontTagIfAny();
        emitText("<font face=");
        String fontFace = htmlData.getFontFace();
        fontFace = wrapInQuotes(fontFace);
        emitText(fontFace);
        emitText(">");
    }
}