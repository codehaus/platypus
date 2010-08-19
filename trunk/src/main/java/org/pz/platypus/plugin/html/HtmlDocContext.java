/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */


package org.pz.platypus.plugin.html;

import org.pz.platypus.interfaces.OutputCommandable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Html processing context and other intelligence...
 * @author ask
 */

public class HtmlDocContext {
    private HtmlOutfile htmlOutfile;
    
    LinkedList<String> listAsStack = new LinkedList<String>();
    LinkedList<String> nestTagsList = new LinkedList<String>();

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
                outfile.emitText( "<i>" );
            } else if (cmdName.equals( "[-i]" )) {
                outfile.emitText( "</i>" );
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

    public boolean areWeInAParagraphAlready() {
        return false;
    }

    public HtmlOutfile getOutfile()
    {
        return(htmlOutfile);
    }

    public void setOutfile( final HtmlOutfile newOutfile)
    {
        htmlOutfile = newOutfile;
    }

    public void handleFontFace() {
        getOutfile().handleNewFontFace();        
    }
}
