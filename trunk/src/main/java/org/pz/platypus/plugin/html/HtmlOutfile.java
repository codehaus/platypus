/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.html;

import org.apache.ecs.html.Body;
import org.apache.ecs.html.Html;
import org.apache.ecs.wml.Head;
import org.pz.platypus.GDD;
import org.pz.platypus.exceptions.FileCloseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;


/**
 * Manage output to the PDF file
 */
public class HtmlOutfile
{
    private boolean openStatus = false;

    /** the HTML data structure */
    HtmlData htmlData;

    private String htmlFileName;
    private PrintStream htmlFile;

    Html html = new Html();
    Head head = new Head();
    Body htmlBody = new Body();
    
    private boolean inParagraph = false;
    private int styleNum = 0;
    private int tab = 0;
    private final String styleStem = "platypus";
    private boolean inFont = false;

    public HtmlOutfile()
    {
        openStatus = false;
        htmlBody.setPrettyPrint( true );
        html.addElement( head );
        html.addElement( htmlBody );
    }

    /**
     * Open the output file. Does basic checks, calls openPdfFile(), and handles any exceptions
     * @param gdd Global document data
     * @param filename name of file to open
     * @param pdfData
     * @throws java.io.IOException in event of a problem opening the file
     */
    public void open( final GDD gdd, final String filename, final HtmlData pdfData) throws IOException
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
     * Outputs any material that has not yet been output. Then closes the iText Document.
     * Note that the first text item to be output forces an open of the document. Consult
     * Start.processText(). So, if the file is closed, no text was output.
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
            htmlData.configureBody(htmlBody);
            htmlFile.println(html.toString());
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
        emitText("<p " + "CLASS=" + wrapInQuotes(getLatestStyleName()) + ">");
        setInParagraph(true);
    }

    public void endCurrentParagraphIfAny() {
        if (getInParagraph()) {
            emitText("</p>");
            setInParagraph(false);            
        }
    }

    public boolean getInParagraph() {
        return inParagraph;
    }
    
    private void setInParagraph(boolean b) {
        inParagraph = b;
    }

    /**
     * Writes text to the HTML file. 
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

    public void setHtmlData( final HtmlData newPdfData )
    {
        htmlData = newPdfData;
    }

    public void outputHtmlNewLine() {
        emitText("<br>");
    }

    public void generateStyleClassDefinition() {
        outputStylePreamble();
        outputStyleSpecs();
        outputStylePostScript();
    }

    private void outputStylePostScript() {
        outputClosingBrace();
        tabin();
        outputXmlCommentEnd();
        closeStyleTag();
        outputNewLine();
    }

    private void closeStyleTag() {
        outputCloseTag("STYLE");
    }

    private void outputCloseTag(String tagElem) {
        outputNewLine();
        emitText("</");
        emitText(tagElem);
        emitText(">");                
        outputNewLine();
    }

    private void outputXmlCommentEnd() {
        outputNewLine();
        emitText("-->");
    }

    private void outputClosingBrace() {
        emitText("}");
    }

    private void tabin() {
        --tab;
    }

    private void outputStyleSpecs() {
        outputPaddingLeft();
        outputPaddingRight();
    }

    private void outputPaddingRight() {
        float rindent = htmlData.getParagraphIndentRight();
        emitText("padding-right" + ":" + " " + rindent + "pt" + ";");
        outputNewLine();
    }

    private void outputPaddingLeft() {
        float indent = htmlData.getParagraphIndent();
        emitText("padding-left" + ":" + " " + indent + "pt" + ";");
        outputNewLine();
    }

    private void outputStylePreamble() {
        outputNewLine();
        outputNewLine();
        openStyleTag();
        outputXmlCommentStart();
        outputStyleName();
        openStyleDefinition();
    }

    private void openStyleDefinition() {
        tabout();
        outputOpenBrace();
    }

    private void outputOpenBrace() {
        emitText("{");
        outputNewLine();
    }

    private void tabout() {
        ++tab;
    }

    private void outputNewLine() {
        String newline = System.getProperty("line.separator");
        emitText(newline);
    }

    private void outputStyleName() {
        emitText(".");
        emitText(generateNewStyleName());
        outputNewLine();
    }

    private String generateNewStyleName() {
        ++styleNum;
        return getLatestStyleName();
    }

    private String getLatestStyleName() {
        return styleStem + String.valueOf(styleNum);
    }

    private void outputXmlCommentStart() {
        outputNewLine();
        emitText("<!--");
    }

    private String wrapInQuotes(String s) {
        return "\"" + s + "\"";
    }

    private void openStyleTag() {
        outputOpenTag("STYLE ", "TYPE=", wrapInQuotes("text/css"));
    }

    private void outputOpenTag(String... tagElemSpec) {
        emitText("<");
        for (String s : tagElemSpec) {
            emitText(s);
        }
        emitText(">");
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

    public void handleEof() {
        endCurrentParagraphIfAny();
        endFontTagIfAny();
    }

    private void endFontTagIfAny() {
        if (alreadyProcessingFont()) {
            emitText("</font>");
        }
    }

    private boolean alreadyProcessingFont() {
        return inFont;    
    }

    public void setMarginRight() {
        float marginRight = htmlData.getMarginRight();
        // TODO: not sure how to fix this right now
    }

    public void setMarginTop() {
        int marginTop = (int) htmlData.getMarginTop();
        htmlBody.addAttribute("TOPMARGIN", marginTop);
    }

    public void handleNewFontFace() {
        startNewFontFace();
    }

    private void startNewFontFace() {
        endFontTagIfAny();
        emitText("<font face=");
        String fontFace = htmlData.getFontFace();
        fontFace = wrapInQuotes(fontFace);
        emitText(fontFace);
        emitText(">");
    }
}