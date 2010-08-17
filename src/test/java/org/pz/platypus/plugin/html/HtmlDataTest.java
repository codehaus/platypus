package org.pz.platypus.plugin.html;

import org.custommonkey.xmlunit.Diff;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.plugin.html.commands.HtmlBoldOff;
import org.pz.platypus.plugin.html.commands.HtmlItalicsOff;
import org.pz.platypus.plugin.html.commands.HtmlItalicsOn;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 * <p/>
 * Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 * Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 * @author: atul
 * 
 */
public class HtmlDataTest {

    HtmlData htmlData = null;

    @Before
    public void init() {
        htmlData = new HtmlData(null, null); // TODO: pass instances instead of null
    }

    @Test
    public void testIdentical() throws Exception {
        String myControlXML = "<struct><int>3</int><boolean>false</boolean></struct>";
        String myTestXML = "<struct><boolean>false</boolean><int>3</int></struct>";
        Diff myDiff = new Diff(myControlXML, myTestXML);
        assertTrue("pieces of XML are similar " + myDiff, myDiff.similar());
        assertTrue("but are they identical? " + myDiff, myDiff.identical());
    }
    
    @Test
    public void testTwoTagsAndNoOverLap() {
        htmlData.push("[+b]");
        Assert.assertEquals(htmlData.nestedTags(), "");
        htmlData.push("[+i]");
        Assert.assertEquals(htmlData.nestedTags(), "");
        htmlData.push("[-i]");
        Assert.assertEquals(htmlData.nestedTags(), "[-i]");
        htmlData.push("[-b]");
        Assert.assertEquals(htmlData.nestedTags(), "[-b]");
    }

    @Test
    public void testTwoTagsAndSingleTagOverLap() {
        htmlData.push("[+b]");
        Assert.assertEquals(htmlData.nestedTags(), "");
        htmlData.push("[+i]");
        Assert.assertEquals(htmlData.nestedTags(), "");
        htmlData.push("[-b]");
        Assert.assertEquals(htmlData.nestedTags(), "[-i][-b][+i]");
        htmlData.push("[-i]");
        Assert.assertEquals(htmlData.nestedTags(), "[-i]");        
    }

    @Test
    public void testThreeTagsAndSingleTagOverLap() {
        htmlData.push("[+b]");
        Assert.assertEquals("", htmlData.nestedTags());
        htmlData.push("[+i]");
        Assert.assertEquals("", htmlData.nestedTags());
        htmlData.push("[+u]");        
        Assert.assertEquals("", htmlData.nestedTags());
        htmlData.push("[-b]");
        Assert.assertEquals("[-u][-i][-b][+i][+u]", htmlData.nestedTags());
        htmlData.push("[-u]");
        Assert.assertEquals("[-u]", htmlData.nestedTags());
        htmlData.push("[-i]");
        Assert.assertEquals("[-i]", htmlData.nestedTags());                          
    }

    @Test
    public void testThreeTagsAndTwoTagsOverLap() {
        htmlData.push("[+b]");
        Assert.assertEquals("", htmlData.nestedTags());
        htmlData.push("[+i]");
        Assert.assertEquals("", htmlData.nestedTags());
        htmlData.push("[+u]");
        Assert.assertEquals("", htmlData.nestedTags());
        htmlData.push("[-b]");
        Assert.assertEquals("[-u][-i][-b][+i][+u]", htmlData.nestedTags());
        htmlData.push("[-i]");
        Assert.assertEquals("[-u][-i][+u]", htmlData.nestedTags());
        htmlData.push("[-u]");
        Assert.assertEquals("[-u]", htmlData.nestedTags());
    }

    /** There is no way we can pass a commandTable for creating an HtmlData.
     *  So ignoring this test for now.
     */
    @Test
    @Ignore
    public void testNestedTagsCommands() {
        htmlData.push("[+b]");
        // Assert.assertEquals(htmlData.nestedTags(), "");
        Assert.assertEquals(0, htmlData.getNestedTagCmds().size());

        htmlData.push("[+i]");
        // Assert.assertEquals(htmlData.nestedTags(), "");
        Assert.assertEquals(0, htmlData.getNestedTagCmds().size());
        
        htmlData.push("[-b]");
        List<OutputCommandable> listOfTagCmds = htmlData.getNestedTagCmds();
        // Assert.assertEquals(htmlData.nestedTags(), "[-i][-b][+i]");
        Assert.assertEquals(3, listOfTagCmds.size());        
        for(OutputCommandable cmd: listOfTagCmds) {
            Assert.assertTrue(cmd instanceof HtmlItalicsOff);
            Assert.assertTrue(cmd instanceof HtmlBoldOff);
            Assert.assertTrue(cmd instanceof HtmlItalicsOn);
        }
        htmlData.push("[-i]");
        listOfTagCmds = htmlData.getNestedTagCmds();
        Assert.assertEquals(1, listOfTagCmds.size()); 
        // Assert.assertEquals(htmlData.nestedTags(), "[-i]");
        for(OutputCommandable cmd: listOfTagCmds) {
            Assert.assertTrue(cmd instanceof HtmlItalicsOff);
        }        
    }

}
