/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-10 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import org.pz.platypus.GDD;
import org.pz.platypus.Source;
import org.pz.platypus.DefaultValues;
import org.pz.platypus.BulletLists;
import org.pz.platypus.commands.Alignment;
import org.pz.platypus.plugin.common.Underline;
import org.pz.platypus.exceptions.FileCloseException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EmptyStackException;

/**
 * Manage output to the PDF file
 */
public class PdfOutfile
{
    private boolean openStatus = false;

    /** fields with the iT prefix are from iText. This is the iText document -- the PDF file */
    Document iTDocument;

    /** the writer that writes to the iTDocument. Needed by iText */
    PdfWriter iTWriter;

    /** the column that the text is written to in iText. */
    ColumnText iTColumn;

    /** the largest entity written to an iTDocument is a paragraph */
    Paragraph iTPara = null;

    /** the collection of active bullet lists */
    BulletLists bulletLists = null;

    /** iText variable that ColumnText uses to see if there's more to write out */
    int iTStatus;

    /** the name of the output file */
    String pdfFilename;

    /** the PDF data structure */
    PdfData pdfData;

    public PdfOutfile()
    {
        openStatus = false;
        iTPara = null;
        iTWriter = null;
        iTDocument = null;
        bulletLists = new BulletLists();
    }

    /**
     * Open the output file. Does basic checks, calls openPdfFile(), and handles any exceptions
     * @param filename name of file to open
     * @param pdd the PDF data class
     * @throws IOException in event of a problem opening the file
     * @throws IllegalArgumentException if pdd is null
     */
    public void open( final String filename, final PdfData pdd ) throws IOException
    {
        if( pdd == null ) {
            throw new IllegalArgumentException( "null PdfData passed to PdfOutfile.open() ");
        }

        GDD gdd = pdd.getGdd();

        if( filename == null || filename.isEmpty() ) {
            gdd.logSevere( gdd.getLit( "ERROR.MISSING_INPUT_FILE" ) + " " +
                                       gdd.getLit( "EXITING" ));
            throw new IOException();
        }
        else {
            pdfFilename = filename;
        }

        try {
            openPdfFile( gdd, pdd, filename );
        }
        catch( IOException ioe ) {
            gdd.logSevere( gdd.getLit( "ERROR.OPENING_OUTPUT_FILE" ) + ": " +
                    filename + " " + gdd.getLit( "EXITING" ));
            throw new IOException();
        }

        gdd.log( "Opened PDF output file:" + " " + filename );
    }

    /**
     * Low level function for opening an iText file (that is, an iText Document)
     * @param gdd the GDD
     * @param pdf the PDF data
     * @param filename name of file to be opened
     * @throws IOException in the event opening the file runs into an error
     */
    void openPdfFile( final GDD gdd, final PdfData pdf, final String filename )
            throws IOException
    {
        /** for writing to columns */
        PdfContentByte iTContentByte;

        final Rectangle pageSize =
                new Rectangle( pdf.getPageWidth(),
                               pdf.getPageHeight() );

        iTDocument =
                new Document( pageSize,
                              pdf.getMarginLeft(),
                              pdf.getMarginRight(),
                              pdf.getMarginTop(),
                              pdf.getMarginBottom());

        FileOutputStream fos;
        try
        {
            fos = new FileOutputStream( filename );
            iTWriter = PdfWriter.getInstance( iTDocument, fos );
            gdd.log( "Opened PDF output stream: " + filename );
        }
        catch (DocumentException e)
        {
            throw new IOException( "Document Exception" );
        }
        catch( FileNotFoundException e)
        {
            throw new IOException( "Document Not Found" );
        }

        // add the routines for iText to call automatically
//        iTWriter.setPageEvent( new OnPageStart() );
        iTWriter.setPageEvent( new OnPageEnd() );
//
//        // set up the needed iText fields in the footer
//        PageFooterPdf pfpdf = (PageFooterPdf) gdd.getFooter();
//        pfpdf.setDocument( iTDocument );
//        pfpdf.setWriter( iTWriter );

        // add metadata referring to Platypus
        iTDocument.addCreator( gdd.getLit( "CREATED_BY_PLATYPUS" ) + " " +
                               gdd.getLit( "VERSION" ) + "  " +
                               gdd.getLit( "AVAILABLE_AT_PZ_ORG" ));

        // open the document
        iTDocument.open();

        // are the margins mirrored?
        if ( pdf.getMarginsMirrored() ) {
            iTDocument.setMarginMirroring( true );
        }

        // get the ContentByte for writing to the columns
        iTContentByte = iTWriter.getDirectContent();

        iTColumn = new ColumnText( iTContentByte );

        // determine the size of columns based on page size at open
        pdf.setColumns( new Columns( pdf.getColumnCount(), 0f, pdf ));

        openStatus = true;
    }

    /**
     * Outputs any material that has not yet been output. Then closes the iText Document.
     * Note that the first text item to be output forces an open of the document. Consult
     * Start.processText(). So, if the file is closed, no text was output.
     *
     * @throws FileCloseException if an error occurs closing the file
     */
    public void close() throws FileCloseException
    {
        if( ! openStatus ) {
            GDD gdd = pdfData.getGdd();
            gdd.logWarning( gdd.getLit( "NO_OUTPUT_FILE" ));
            return;
        }

        // First output any unwritten paragraph text to the column(s)
        if( iTPara != null ) {
            addParagraph( iTPara, iTColumn );
            iTPara = null;
        }

        // if in a bullet list, close the list up
        if( inABulletList() ) {
            endPlainBulletList();
        }
        
        // then output the column content
        addColumnsContentToDocument();

        // now, we can close (note: following steps must occur in this order)
        openStatus =  false;
        try {
            iTDocument.close();
        }
        catch( Exception ex ) {
            // throwing
            // com.lowagie.text.exceptions.IllegalPdfSyntaxException: Unbalanced save/restore state operators.
            throw new FileCloseException( pdfFilename );
        }
    }

    /**
     * If paragraph has content, add it to the content of the current column
     *
     * @param para the iText paragraph to add
     * @param column the iText column to add the paragraph to
     */
    public void addParagraph( Paragraph para, final ColumnText column )
    {
        if ( para == null ) {   // there's no paragraph to add
            return;
        }

        assert( column != null );
        assert( pdfData != null );

        ColumnText outputColumn = column;

        try
        {
            if( ! isOpen() ) {
                makeSureOutfileIsOpen();
                outputColumn = iTColumn;
            }
        }
        catch( IOException ioe ) {
            return; //TODO: Should emit error message    
        }

        if( inABulletList() ) {
            addParagraphToList( para );
        }
        else {
            doParagraphAlignment( para, pdfData );
            doParagraphIndent( para, pdfData );
            doParagraphIndentRight( para, pdfData );
            doFirstLineIndent( para, pdfData );
            doParagraphSpaceBefore( para, pdfData );
            outputColumn.addElement( para );
        }
    }

    /**
     * Converts bullet string into an iText Chunk and calls the method that
     * adds a list using the Chunk to define the bullet symbol.
     *
     * @param bulletSymbol string to use as a bullet symbol (most often, a single character)
     */
    public void startPlainBulletList( final String bulletSymbol )
    {
        String bullet = bulletSymbol;
        if( bullet == null || bullet.isEmpty() ) {
            bullet = DefaultValues.BULLET;
        }

        startPlainBulletList( new Chunk( bullet ));
    }

    /**
     * Adds a new bullet list to the stack of bulleted lists. (These are saved in a stack because
     * lists can nest).
     *
     * @param bulletSymbol symbol to be used as the bullet marker (an iText Chunk)
     */
    public void startPlainBulletList( final Chunk bulletSymbol )
    {
        // set the skip to 0 lines, output old text, start a new paragraph.
        float initialParaSkip = pdfData.getParagraphSkip();
        int initialParaSkipSource = pdfData.getParagraphSkipLine();
        pdfData.setParagraphSkip( 0f, new Source( 0, initialParaSkipSource ));
        startNewParagraph();

        // then reset the paragraph skip to the previous value.
        pdfData.setParagraphSkip( initialParaSkip, new Source( 0, initialParaSkipSource ));

        // create the bullet list
        Chunk bulletMarker = bulletSymbol;
        float indentMargin = 18f;   // for the nonce, all indents are 1/4"

        List bulletList = new List( List.UNORDERED, indentMargin );
        if( bulletMarker == null || bulletMarker.isEmpty() ) {
            bulletMarker = new Chunk( DefaultValues.BULLET );
        }
        bulletList.setListSymbol( bulletMarker );
        bulletLists.add( bulletList );
    }

    /**
     * Adds a paragraph to the currently active bullet list.
     *
     * @param para the paragraph to add
     */
    public void addParagraphToList( Paragraph para )
    {
        if( para == null ) {
            return;  // TODO: should add an error message
        }

        List currList;
        try {
            currList = (List) bulletLists.peek();
        }
        catch( EmptyStackException ese ) {
            return;     // TODO: should add an errro message
        }

        ListItem li = new ListItem( para );
        if( currList != null ) {
            currList.add( li );
        }

        iTPara = null;

    }

    /**
     * End the currently active bullet list. This removes it from the stack of active
     * bullet lists and adds it to the current column.
     */
    public void endPlainBulletList()
    {
        List currList;
        try {
            currList = (List) bulletLists.pop();
        }
        catch( EmptyStackException ese ) {
            return;     // TODO: should add an error message
        }

        // if in a paragraph, add the paragraph to the list before closing the list.
        if( iTPara != null ) {
            addParagraphToList( iTPara );
        }

        // if this list is part of another list (here called the outer list) add it to that list.
        if( ! bulletLists.empty() ) {
            List outerList = (List) bulletLists.peek();
            outerList.add( currList );
        }
        // if not, add it to the column of text.
        else {
            iTColumn.addElement( currList );
        }
    }
    /**
     * Are we in a bullet list?
     *
     * @return true if in a bullet list, false otherwise.
     */
    public boolean inABulletList()
    {
        return( bulletLists.size() > 0 );
    }

    /**
     * Handles indenting the first line of a paragraph. Note: also handles
     * the [noindent] command, which is a one-time command to not indent the
     * present paragraph.
     *
     * @param para the paragraph with the indented first line
     * @param pData the PDF data class containing the indent amount (in points)
     * @return the amount indented
     */
    float doFirstLineIndent( final Paragraph para, final PdfData pData )
    {
        assert( para != null );
        assert( pData != null );

        // if noindent = true, skip the indent but reset noident to false, as it is
        // applicable for only one paragraph before being reset.
        if( pdfData.getNoIndent() ) {
            pdfData.setNoIndent( false, new Source() );  //TODO: get right source? Does it matter?
            return( 0f );
        }
        else {
            float indent =  pData.getFirstLineIndent();
            para.setFirstLineIndent( indent );
            return( indent );
        }
    }

    /**
     * Handles indenting the entire paragraph. Indent occurs from the left margin.
     *
     * @param para paragraph
     * @param pData PDF data containing the amount of indent (in points)
     * @return the amount of indent in points.
     */
    float doParagraphIndent( final Paragraph para, final PdfData pData )
    {
        assert( para != null );
        assert( pData != null );

        float indent =  pData.getParagraphIndent();
        para.setIndentationLeft( indent );
        return( indent );
    }

    /**
     * Handles indenting the entire paragraph. Indent occurs from the right margin.
     *
     * @param para paragraph
     * @param pData PDF data containing the amount of indent (in points)
     * @return the amount of indent in points.
     */
    float doParagraphIndentRight( final Paragraph para, final PdfData pData )
    {
        assert( para != null );
        assert( pData != null );

        float indent =  pData.getParagraphIndentRight();
        para.setIndentationRight( indent );
        return( indent );
    }

    /**
     * Implement the line spacing before the paragraph, if any.
     *
     * Note: We do this manually rather than call Paragraph.setSpacingBefore() in iText
     * because setSpacingBefore() in iText affects the current paragraph. So is [paraskip=0]
     * occurs midway in a paragraph, that pargraph will have 0 spacing before it, rather than
     * the previous paraskip value, which was in effect when the paragraph was started. 2009-01-31
     *
     * @param para the paragraph to align
     * @param pData the PdfData container holding the current settings of the PDF output
     */
    void doParagraphSpaceBefore( final Paragraph para, final PdfData pData )
    {
        float skipLines = pData.getParagraphSkip();

        // add 1, as you need 2 NEWLINES for the first line's skip.
        skipLines++;
        while( skipLines-- > 0 ) {
            para.add( new Chunk( Chunk.NEWLINE ));
        }
    }

    /**
     * Implement the paragraph alignment
     *
     * @param para the paragraph to align
     * @param pData the PdfData container holding the current settings of the PDF output
     */
    void doParagraphAlignment( final Paragraph para, final PdfData pData )
    {
        switch( pData.getAlignment() )
        {
            case Alignment.CENTER:
                para.setAlignment( Element.ALIGN_CENTER );
                break;
            case Alignment.JUST:
                para.setAlignment( Element.ALIGN_JUSTIFIED );
                break;
            case Alignment.LEFT:
                para.setAlignment( Element.ALIGN_LEFT );
                break;
            case Alignment.RIGHT:
                para.setAlignment( Element.ALIGN_RIGHT );
                break;
            default:    // ignore in case of other possibility
                break;
        }
    }

    /**
     * The method that renders the PDF file
     */
    public void addColumnsContentToDocument()
    {
        // following test required if the [columns: command appears before any text
        if( iTDocument == null || iTColumn == null ) {
            return;
        }

        iTStatus = ColumnText.NO_MORE_COLUMN;

        try
        {
            while( ColumnText.hasMoreText( iTStatus )) {

                if( pdfData.getCurrColumn() >= pdfData.getColumnCount() ) {
                    iTDocument.newPage();
                    pdfData.setCurrColumn( 0 );
                }

                setColumnSize();

                iTStatus =  iTColumn.go();

                 if ( ColumnText.hasMoreText( iTStatus )) {
                     int currCol = pdfData.getCurrColumn();
                     pdfData.setCurrColumn( currCol + 1 );
                }
            }
        }
        catch ( DocumentException de ) {        //TODO: Replace with true exception handling
            System.out.println( "Exception adding text to iTColumn (ColumnText) in OutfilePdf" );
        }
    }

    /**
     * Set the size of the columns based on their number, page size, and gutter size.
     */
    void setColumnSize()
    {
        // the locations of the edges of the current column
        float leftColEdge, rightColEdge, topColEdge, bottomColEdge;

        leftColEdge = computeLeftColEdge();
        rightColEdge = computeRightColEdge( leftColEdge );
        topColEdge = computeTopColEdge();
        bottomColEdge = computeBottomColEdge( topColEdge );

        iTColumn.setSimpleColumn( leftColEdge,
                                  bottomColEdge,
                                  rightColEdge,
                                  topColEdge );
    }

    /**
     * Compute the bottom edge, which is the lesser of the topEdge - columnHeight or
     * top edge - bottom margin.
     * //TODO: find out why this second element is important. (Converted from Platypus v. 0.1.16)
     *
     * @param topEdge top edge of this column
     * @return bottom edge of the column
     */
    float computeBottomColEdge( final float topEdge )
    {
        Column currCol = pdfData.getColumns().getColumn( pdfData.getCurrColumn() );
        return( Math.min( topEdge - currCol.getHeight(), topEdge - pdfData.getMarginBottom() ));
    }

    /**
     * Computes the left edge of the current column
     *
     * It starts with the left margin and adds the width and gutters of any columns between it
     * and the left margin.
     *
     * @return the left margin of the current column
     */
    float computeLeftColEdge()
    {
        float leftEdge = pdfData.getMarginLeft();
        for( int i = 0; i < pdfData.getCurrColumn(); i++ )
        {
            leftEdge += pdfData.getColumns().getColumn( i ).getWidth() +
                        pdfData.getColumns().getColumn( i ).getGutter();
        }
        return( leftEdge );
    }

    /**
     * Compute the right edge of the current column. It's the left edge + column width
     * @param leftEdge location of left edge
     * @return right edge
     */
    float computeRightColEdge( final float leftEdge )
    {
        Column currCol = pdfData.getColumns().getColumn( pdfData.getCurrColumn() );
        return( leftEdge + currCol.getWidth() );
    }

    /**
     * Compute the top edge of the curren column, which is the page height (PDF coordinates
     * start at lower left corner) - top margin - vertical skip.
     *
     * @return  top edge
     */
    float computeTopColEdge()
    {
        Column currCol = pdfData.getColumns().getColumn( pdfData.getCurrColumn() );
        return( pdfData.getPageHeight() - pdfData.getMarginTop() - currCol.getVertSkip() );
    }

    /**
     * High level skip to the top of the next page. Writes out all pending text before skipping.
     */
    public void newPage()
    {
        if( iTPara != null && iTPara.size() > 0 ) {
            addParagraph( iTPara, iTColumn );
        }

        addColumnsContentToDocument();
        newPageLowLevel();
    }

    /**
     * Forces a new page without any of the accompanying processing (although pageEnd() and
     * newPage() events in iText are still performed). For example, if we are in mid-paragraph,
     * the paragraph is not added to the current page; etc.
     *
     * @return wasPageAdded true if it was, false if an error occurred
     */
    public boolean newPageLowLevel()
    {
        boolean wasPageAdded;

        try
        {
            iTWriter.setPageEmpty( false );           // forces the page to be emitted.
            wasPageAdded = iTDocument.newPage();
        }
        catch( Exception e )
        {
            wasPageAdded = false;
        }
        return( wasPageAdded );
    }

    /**
     * Starts a new iText Paragraph. First writes out any content from the previous
     * paragraph if there remains anything unwritten to the output file.
     */
    public void startNewParagraph()
    {
        if( iTPara != null ) {
            addParagraph( iTPara, iTColumn );
        }

        iTPara = new Paragraph( pdfData.getLeading() );
        pdfData.setEolPending( false );  //TODO: Is this still needed?
    }

    /**
     * Make sure file is open in the event that the first item is not text, such
     * as if it is a URL.
     *
     * @throws IOException in the event that the call to open() throws this exception
     */
    void makeSureOutfileIsOpen() throws IOException
    {
        GDD gdd = pdfData.getGdd();
        String outputFilename = gdd.getSysStrings().getString( "_outputFile" );

        if( !isOpen() ) {
            try {
                open( outputFilename , pdfData );
            }
            catch( IOException ioe ) {
                // Just rethrow. The error message for the exception was already shown in the open() method.
                throw new IOException();
            }
        }
    }

    /**
     * Writes text to the PDF file. Because text can be written either as a Chunk or a Phrase
     * in iText, this method has to manage both entities within the larger context of a Paragraph.
     *
     * @param s the text to be written
     */
    public void emitText( String s )
    {
        assert( s != null ) : "s parameter null in PdfOutfile.emitText()";

        // check if we are in an existing paragraph. If not, create a new one.
        if ( iTPara == null ) {
           startNewParagraph();
        }

        // now create a Chunk containing the text in String s using the font in pdfData
        final Chunk chunk = new Chunk( s, pdfData.getFont().getItextFont() );

        // if strikethrough is on, then set it here for this chunk
        if( pdfData.getStrikethru() ) {
            float lineLocation =
                                pdfData.getFontSize() / DefaultValues.FONT_SIZE_TO_STRIKETHRU_RATIO;
            chunk.setUnderline( DefaultValues.UNDERLINE_THICKNESS, lineLocation );
        }

        if ( pdfData.getUnderline().isInEffect() ) {
            Underline ul = pdfData.getUnderline();
            chunk.setUnderline( ul.geTthickness(), ul.getPosition() );
        }

        iTPara.add( chunk );
    }

    /**
     * Emit a single character in the current font, size, etc. This is used to output
     * a character by its Unicode value and is called by PdfSymbol, which handles the
     * processing of symbols and special characters.
     *
     * @param ch char to be emitted
     * @param fontName fontName to use. If null, use current font.
     */
    public void emitChar( final String ch, final String fontName)
    {
        assert( ch != null ) : "ch parameter null in PdfOutfile.emitChar()";

        if( iTPara == null ) {
            startNewParagraph();
        }

        FontSelector fs = new FontSelector();
        if( fontName == null || fontName.isEmpty() ) {
            fs.addFont( pdfData.getFont().getItextFont() );
        }
        else {
            PdfFont newFont = new PdfFont( pdfData, fontName, pdfData.getFont() );
            fs.addFont( newFont.getItextFont() );
        }
        
        Phrase phr = fs.process( ch );
        iTPara.add( phr );
    }

    /**
     * Emits a URL with the specified cover text. Creates a clickable link in the PDF doc.
     * If no cover text is null (so, not specified), then the text defaults to the URL itself.
     * (In other words, http://pz.org would print as is.)
     *
     * @param url the URL of the link
     * @param text words to be printed and made linkable (in lieu of printing the URL)
     */
    public void addUrl( final String url, final String text )
    {
        if( url == null ) {
            return;
        }

        String coverText = text;
        if( coverText == null ) {
            coverText = url;
        }

        Anchor anchor = new Anchor( coverText, pdfData.getFont().getItextFont() );
        anchor.setReference( url );

        try {
            makeSureOutfileIsOpen();
        }
        catch( IOException ioe ) {
            return;
        }
        
        if( iTPara == null ) {
            startNewParagraph();
        }

        iTPara.add( anchor );
    }

    /**
     * Turn on margin mirroring.
     */
    public void setMarginsMirrored()
    {
        iTDocument.setMarginMirroring( true );
    }

    // ===== endPage events =======

    // inner class to handle the end of page events
    class OnPageEnd extends PdfPageEventHelper
    {

        public OnPageEnd() {}

        /**
         * Lists the sequence of actions to perform
         * .
         * @param writer  used for writing the footer
         * @param document the document in which the footer will be written
         */
        @Override
        public void onEndPage( final PdfWriter writer, final Document document )
        {
            pdfData.setPageNumber( pdfData.getPageNumber() + 1 );
            footerProcessing();
            postFooterProcessing();
            updatePageSize();
            updateAllMargins();
        }

        public void updateAllMargins()
        {
            GDD gdd = pdfData.getGdd();

            /** the document's current bottom margin */
            float docBM = iTDocument.bottomMargin();
            float docLM = iTDocument.leftMargin();
            float docRM = iTDocument.rightMargin();
            float docTM = iTDocument.topMargin();

            /** the current values as specified in  PDF data, (the new value, if any) */
            float newBM = pdfData.getMarginBottom();
            float newLM = pdfData.getMarginLeft();
            float newRM = pdfData.getMarginRight();
            float newTM = pdfData.getMarginTop();

            // validate that the margins are not excessively sized.
            // They were previously checked for not being too small or individually too big.
            if( newLM + newRM >= iTDocument.getPageSize().getWidth() ) {
                gdd.logWarning( gdd.getLit( "ERROR.LEFT_RIGHT_MARGINS_TOO_BIG" ) + ":\n" +
                                gdd.getLit( "\t" + "LEFT_MARGIN" + ":  " + newLM + "\n" ) +
                                gdd.getLit( "\t" + "RIGHT_MARGIN" + ":  " + newRM + "\n" ) +
                                gdd.getLit( "RESET_TO_CURRENT_SIZE" ));

                // reset the margins to the current settings, so we don't go through this
                // logging step at every new page.
                pdfData.setMarginRight( docRM, pdfData.getMarginRightLine() );
                pdfData.setMarginLeft( docLM, pdfData.getMarginLeftLine() );

                return;
            }

            if( newTM + newBM >= iTDocument.getPageSize().getHeight() ) {
                gdd.logWarning( gdd.getLit( "ERROR.TOP_BOTTOM_MARGINS_TOO_BIG" ) + ":\n" +
                                gdd.getLit( "\t" + "TOP_MARGIN" + ":  " + newTM + "\n" ) +
                                gdd.getLit( "\t" + "BOTTOM_MARGIN" + ":  " + newBM + "\n" ) +
                                gdd.getLit( "RESET_TO_CURRENT_SIZE" ));

                // reset the margins to the current settings, so we don't go through this
                // logging step at every new page.
                pdfData.setMarginTop( docTM, pdfData.getMarginTopLine() );
                pdfData.setMarginBottom( docBM, pdfData.getMarginBottomLine() );
                return;
            }

            // set the margins if any of them has changed.
            if( docBM != newBM || docLM != newLM || docRM != newRM || docTM != newTM ) {
                iTDocument.setMargins( newLM, newRM, newTM, newBM );
            }
        }
        /**
         * Checks to see whether the page size has changed from the current page size.
         * If so, the next page is set to the new page size.
         */
        public void updatePageSize()
        {
            // the page size as currently specified in the present document
            float docH = iTDocument.getPageSize().getHeight();
            float docW = iTDocument.getPageSize().getWidth();

            // the page size as kept in pdfData, which will reflect any recent changes
            float currH = pdfData.getPageHeight();
            float currW = pdfData.getPageWidth();

            if( docH != currH || docW != currW ) {
                Rectangle newPage = new Rectangle( currW, currH );
                iTDocument.setPageSize( newPage  );
            }
        }

        /**
         * Print a footer if the number of pages to skip before a footer has been exceeded.
         */
        public void footerProcessing()
        {
            Footer f = pdfData.getFooter();

            if( f.shouldWrite() && pdfData.getPageNumber() > f.getPagesToSkip() ) {

                PdfContentByte cb = iTWriter.getDirectContent();
                cb.saveState();
                final String footerText = getFooterText();
                final BaseFont footerBaseFont = f.getFont().getItextFont().getBaseFont();
                final float footerFontSize = f.getFont().getItextFont().getCalculatedSize();
                final float footerTextLength = footerBaseFont.getWidthPoint( footerText, 10 );
                final float footerBaseline = iTDocument.bottom() - f.getBaseline();

                // the actual process of writing to the footer's absolute location
                cb.beginText();
                cb.setFontAndSize( footerBaseFont, footerFontSize );
                cb.setTextMatrix( iTDocument.right() - footerTextLength, footerBaseline );
                cb.showText( footerText );
                cb.endText();
                cb.restoreState();
            }
        }

        /**
         * Create the text string we'll use. Currently em-dash page# em-dash
         * @return the footer string
         */
        private String getFooterText()
        {
            final String footerText = "\u2014 " + pdfData.getPageNumber() + " \u2014";
            return( footerText );
        }

        /**
         * Processing to do after the footer is printed out
         */
        public void postFooterProcessing()
        {
            pdfData.getColumns().startColumnsAtTop( pdfData );
        }
    }

    // ==== getters and setters ====//

    /**
     * Gets the Y-position from iTColumn
     *
     * @return the vertical position of the current writing position
     */

    public float getYposition()
    {
        return( iTColumn.getYLine() );
    }

    public int getAddStatus()
    {
        return( iTStatus );
    }

    public boolean isOpen()
    {
        return( openStatus );
    }

    public void setPdfData( final PdfData newPdfData )
    {
        pdfData = newPdfData;
    }

    public float getColumnWidth()
    {
        return( iTColumn.getFilledWidth() );
    }

    public Paragraph getItPara()
    {
        return( iTPara );
    }

    // this method is used for testing

    public void setBulletLists( BulletLists newBulletLists )
    {
        bulletLists = newBulletLists;
    }

    // this method is only ever used for testing.
    public void setItPara( Paragraph newPara )
    {
        iTPara = newPara;
    }

    // this method is only ever used for testing.
    public void setItColumn( ColumnText ct )
    {
        iTColumn = ct;
    }

    // this method is only ever used for testing.
    public ColumnText getItColumn()
    {
        return( iTColumn );
    }
}
