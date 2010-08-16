/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.test.mocks;

import java.io.FileDescriptor;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Mock class that simulates FileWriter for unit tests.
 *
 * Does not actually write anything out to any file.
 *
 * @author alb
 */
public class MockFileWriter extends FileWriter
{
    private boolean closeShouldGenerateException = false;
    private boolean writeShouldGenerateException = false;
    private boolean openStatus = false;
    private int counter;
    private String textWritten = null;

    public MockFileWriter()
    {
        super( new FileDescriptor() );
        openStatus = true;
        counter = 0;
    }

    public MockFileWriter( String s )
    {
        super( new FileDescriptor() );
        openStatus = true;
        counter = 0;
    }

    @Override
    public void close() throws IOException
    {
        counter++;

        if( openStatus == false ) {
            throw new IOException();
        }

        if( closeShouldGenerateException ) {
            throw new IOException();
        }
        else {
            openStatus = false;
        }
    }

    @Override
    public void write( final String text ) throws IOException
    {
        counter++;

        if( writeShouldGenerateException ) {
            throw new IOException();
        }
        if( textWritten == null ) {
            textWritten = text;
        }
        else {
            textWritten += text;
        }
    }

    public int getCounter()
    {
        return( counter );
    }

    public boolean getOpenStatus()
    {
        return( openStatus );
    }

    public String getText()
    {
        return( textWritten );
    }

    public void setCloseShouldGenerateException( final boolean yesOrNo )
    {
        closeShouldGenerateException = yesOrNo;
    }

    public void setWriteShouldGenerateException( final boolean yesOrNo )
    {
        writeShouldGenerateException = yesOrNo;
    }
}
