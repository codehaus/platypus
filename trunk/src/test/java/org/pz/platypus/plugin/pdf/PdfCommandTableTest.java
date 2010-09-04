/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.interfaces.IOutputCommand;
import org.pz.platypus.GDD;

public class PdfCommandTableTest
{
    PdfCommandTable pct;
    GDD gdd;

    @Before
    public void setUp()
    {
        pct = new PdfCommandTable();
        gdd = new GDD();
        gdd.initialize();
    }

    @Test
    public void testConstructor()
    {
        assertEquals( 0, pct.getSize() );
    }

    @Test
    public void testLoad()
    {
        assertTrue( pct.getSize() == 0 );
        pct.loadCommands();
        assertTrue( pct.getSize() > 1 );
    }

    @Test
    public void getNewParagraph()
    {
        final String newPara= "[]";
        pct.loadCommands();
        IOutputCommand oc = pct.getCommand( newPara );
        assertEquals( newPara, oc.getRoot() );
    }

    @Test
    public void getInvalidCommand()
    {
        final String nec= "[non-existentCommand]";
        pct.loadCommands();
        IOutputCommand oc = pct.getCommand( nec );
        assertEquals( null, oc );
    }

    @Test
    public void getNullCommand()
    {
        pct.loadCommands();
        IOutputCommand oc = pct.getCommand( null );
        assertEquals( null, oc );
    }
}