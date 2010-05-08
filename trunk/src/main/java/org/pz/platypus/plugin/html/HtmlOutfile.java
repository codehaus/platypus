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

    public HtmlOutfile()
    {
        openStatus = false;
        head.addElement(htmlBody);
        html.addElement(head);
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
     */
    public void startNewParagraph()
    {
        emitText("<p>");
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
        htmlBody.addElement(s);
    }

    // ===== endPage events =======

    // inner class to handle the end of page events

    // ==== getters and setters ====//

    public boolean isOpen()
    {
        return( openStatus );
    }

    public void setHtmlData( final HtmlData newPdfData )
    {
        htmlData = newPdfData;
    }

    // this method is only ever used for testing.

    // this method is only ever used for testing.

    // this method is only ever used for testing.

    public void outputANewLine() {
        emitText("<br>");
    }

}