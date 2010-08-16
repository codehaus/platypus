/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import org.pz.platypus.exceptions.InvalidConfigFileException;
import org.pz.platypus.exceptions.FileCloseException;

import java.util.*;
import java.io.*;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.DocumentException;


/**
 * Map that maps a typeface family to font files on the runtime system that implement that
 * typeface family.
 *
 * Entries consist of a typeface name -> linked-list of file URIs. So, for example,
 *  Arial -> c:\windows\fonts\arial.ttf, c:\windows\fonts\ariali.ttf, etc.
 *
 * This map is consulted when the user specifies a typeface other than the 14 base PDF fonts
 *
 * @author alb
 */
public class TypefaceMap
{
    /* the data structure mapping font family to filenames */
    private HashMap<String, LinkedList<String>> map;

    private GDD gdd;

    public TypefaceMap( GDD Gdd )
    {
        gdd = Gdd;
        map = new HashMap<String, LinkedList<String>>();
    }

    /**
     * Reads the config/fontlist.txt file in PLATYPUS_HOME and loads the entries into the map.
     *
     * @throws InvalidConfigFileException if the fontlist.txt file is missing
     */
    public void loadMapFromFile()
    {
        String defaultFontFile = gdd.getHomeDirectory() + "config/fontlist.txt";
        try {
            loadFromFile( defaultFontFile );
        }
        catch( InvalidConfigFileException icfe ) {
            gdd.logSevere( gdd.getLit( "ERROR.MISSING_FILELIST") + " " + gdd.getLit( "EXITING" ));
            throw new InvalidConfigFileException( "Missing filelist" );
        }
    }

    /**
     * Writes the current map to an output file after sorting the entries on font family.
     *
     * @param filename name of file to write out.
     */
    public void writeMapToFile( final String filename )
    {
        if( filename == null || filename.isEmpty() || map.size() == 0 ) {
            return; //TODO: throw exception
        }

        // sort the file by font family (called entry here)
        Set<String> entries = map.keySet();
        Object[] sEntries =  entries.toArray();
        Arrays.sort( sEntries );

        // open the file for output
        try {
            FileWriter outFile = new FileWriter( filename );
            PrintWriter out = new PrintWriter( outFile );
            for( Object entry : sEntries ) {
                String[] files = getFamilyFilenames( (String) entry );
                if( files.length > 0 ) {
                    writeMapEntryToFile( out, (String) entry, files );
                }
            }
            outFile.close();
            gdd.log( "Completed writing font list to " + filename );
        }
        catch( IOException ioe ) {
            System.out.println( "Exception writing fontlist file in TypefaceMap.writeMapToFile()" );
        }
    }

    /**
     * Writes a single map entry to file
     * @param out the file writer for text
     * @param family the family part of the map entry
     * @param files the files part of the map entry
     */
    void writeMapEntryToFile( PrintWriter out, String family, String[] files )
    {
        for( String file : files ) {
            out.println( family + "=" + file );
        }
    }

    /**
     * Load the font families with their respective font files into the table
     *
     * 1. Get the list of font directories
     * 2. Get the list of fonts files from within those directories
     * 3. For each font file, load its family and name into the table
     * 4. Write out a file with the family and font files to PLATYPUS_HOME/config
     */
    public void loadFamilies()
    {
        String[] fontDirs = new FontDirectoryList( gdd ).getDirs();
        LinkedList<String> fontFiles = getFontFileList( fontDirs );
        BaseFont bf = null;

        for( String fontFilename : fontFiles )
        {
            try {
                loadFontFile( fontFilename, bf );
            }
            catch( Exception e ) {
                System.out.println( "Exception loading: " + fontFilename );
            }
        }
        gdd.log( "Loaded Platypus font-family with " + map.size() + " entries" );
    }

    /**
     * Loads an individual font file to the map. First extracts the family, then if no error
     * occurred, loads the family and the font name to the map.
     *
     * @param fontFilename font file to load to map
     * @param bf is the base font
     */
    public void loadFontFile( final String fontFilename, final BaseFont bf )
    {
        if( fontFilename == null ) {
            return;
        }

        String[] family = extractFamilyNames( fontFilename, bf );
        if( family.length > 0 ) {
            for( String familyName : family)
                addFontFileToFamily( familyName, fontFilename );
        }
    }

    /**
     * Uses iText to extract the family name (or in case of .ttc the names) of the font family.
     *
     * @param fontFilename font file
     * @param bf base font we create to get the family font name
     *
     * @return the font's family name(s) or an empty array if an error occurred.
     *
     * For explanation of .ttc handling, see:
     * http://itextdocs.lowagie.com/tutorial/fonts/getting/index.php
     *
     */
    public String[] extractFamilyNames( String fontFilename, BaseFont bf )
    {
        String familyName[] = new String[1];

        String names[][];
        File f = new File( fontFilename );
        if( f.exists() || ! f.isDirectory() ) {
            try {
                if( fontFilename.toLowerCase().endsWith( ".ttc")) {
                    return( BaseFont.enumerateTTCNames( fontFilename ));
                }

	            bf = BaseFont.createFont( fontFilename, "winansi", BaseFont.NOT_EMBEDDED );
                names = bf.getFamilyFontName();
	        }
            catch( IOException ioe ) {
                gdd.logInfo( "IOException loading " + fontFilename + " into font-family map" );
                return( new String[0] );
            }
            catch( DocumentException de ) {
                gdd.logInfo( "Document Exception loading " + fontFilename + " into font-family map" );
                return(new String[0] );
            }

            if( names!= null && names[0] != null ) {
                familyName[0] = names[0][3];
                return familyName;
            }
            else {
                return( new String[0] );
            }
        }
        return( new String[0] );
    }


    /**
     * Return a linked list of font files found in the past list of font directories.
     * Font files must use a supported format: .ttf, .otf, .afm, or .ttc
     *
     * @param fontDirs array of directory names that should contain fonts
     * @return linked list of filenames. Will have length = 0, if not font files were found.
     */
    LinkedList<String> getFontFileList( final String[] fontDirs )
    {
        LinkedList<String> fontFiles = new LinkedList<String>();

        for( String dirName : fontDirs )
        {
            File dir = new File( dirName );
            String[] fileNames = dir.list();
            if( fileNames != null && fileNames.length != 0 ) {
                for( String fName : fileNames )
                {
                    if( fName.toLowerCase().endsWith(".ttf") ||
                        fName.toLowerCase().endsWith(".ttc") ||
                        fName.toLowerCase().endsWith(".otf") ||
                        fName.toLowerCase().endsWith(".afm") ||
                        fName.toLowerCase().endsWith(".pfm")) {
                        fontFiles.add( dirName + "/" + fName );
                    } // end if filename has correct extension
                } // end for every file name
            } // end if there are fileNames
        }

        gdd.log( "Located " + fontFiles.size() + " font files" );
        return( fontFiles );
    }

    /**
     * As a family name with a linked list of one or more associated font files
     * @param family  font family (key)
     * @param filenames font files associated with the family (value)
     */
    public void addFamily( final String family, final LinkedList<String> filenames )
    {
        map.put( family, filenames );
    }

    /**
     * add a filename to a family, as long as the filename was not previously added .
     *
     * @param family the family name
     * @param filename the filename to add
     */
    public void addFontFileToFamily( final String family, final String filename )
    {
        if( family == null || filename == null ) {
            throw new IllegalArgumentException( "null passed to TypefaceMap.addFile()" );
        }

        LinkedList<String> fontFiles = map.get( family );

        // if the family is not found in the map (so this is the first entry)
        if( fontFiles == null ) {
            LinkedList<String> ll = new LinkedList<String>();
            ll.add( filename );
            addFamily( family, ll );
            return;
        }

        // family already exists, so add filename, as long as it hasn't previously been added
        if( ! fontFiles.contains( filename )) {
            fontFiles.add( filename );
        }
    }

    /**
     * Returns the list of filenames that correspond to a font/typeface family.
     *
     * @param family the family to look up
     * @return an array of filenames; an empty array in the event that the family is not found
     */
    public String[] getFamilyFilenames( final String family )
    {
        if( family == null ) {
            throw new IllegalArgumentException( "null passed to TypefaceMap.get()" );
        }

        LinkedList<String> typeface = map.get( family );
        if( typeface == null ) {
            return( new String[] {} );
        }

        int size = typeface.size();
        String[] filenames = new String[size];

        int i = 0;
        for( String filename : typeface ) {
            filenames[i++] = filename;
        }

        return( filenames );
    }

    /**
     * Load the Typeface map from a configuration file consisting on line entries of the form:
     * family=fontFilename;
     *
     * @param filename name of the file to read
     */
    public void loadFromFile( final String filename )
    {
        final String thisMethod = "TypefaceMap.loadFromFile()";

        if( filename == null ) {
            throw new InvalidConfigFileException( "Invalid filename: [null]", thisMethod );
        }

        File f = new File( filename );
        if( ! f.isFile() || f.length() == 0L || ! f.canRead() ) {
            throw new InvalidConfigFileException( "Invalid filename: " + filename, thisMethod );
        }

        BufferedReader inReader;
        try {
              inReader = new BufferedReader( new FileReader( filename ));
        }
        catch ( FileNotFoundException e ) {
            throw new InvalidConfigFileException( "Error reading: " + filename, thisMethod );
        }

        String line;
        try {
            while (( line = inReader.readLine()) != null ) {
                loadLine( line.trim() );
            }
        }
        catch( IOException ioe ) {
            throw new InvalidConfigFileException( "Error reading: " + filename, thisMethod );
        }
        finally {
            try {
                inReader.close();
            }
            catch( IOException ioe ) {
                throw new FileCloseException( filename + " in " +thisMethod );
            }
        }
    }

    /**
     * Loads a line from the config file. Note: lines commencing with a # are comments
     *
     * @param line the line read from the file containing: family=file name/location
     */
    public void loadLine( final String line )
    {
        String family, fileLocation;

        if ( line == null || line.isEmpty() ) {
            return;
        }

        if ( line.startsWith( "#" )) { // comment lines start with #
            return;
        }

        int equalsSign = line.indexOf( "=" );
        if ( equalsSign < 0 ) {
            return; //curr: issue err msg here
        }

        family = line.substring( 0, equalsSign );
        fileLocation = line.substring( equalsSign + 1, line.length() );
        addFontFileToFamily( family, fileLocation );
    }

    /**
     * Used mostly for testing
     * @return the hash map
     */
    public HashMap<String, LinkedList<String>> getMap()
    {
        return( map );
    }
}
