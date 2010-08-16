/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import org.pz.platypus.exceptions.FilenameLookupException;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
/**
 * A simple treemap containing the name of the input files that were read. This is 
 * useful in situations where the principal Platypus file includes other files (via
 * the [include:filename] command. In certain listings, we refer to the files by 
 * their number for brevity. This FileList is the list of those files, their number
 * is the key in the tree, their name the value. 0 is always null, so that the first
 * file in the tree is numbered 1.
 * @author alb      
 */
public class FileList 
{  
    /** the array containing the filenames */
    final private TreeMap<Integer, String> filenames;
    
    /** the next number of a file */
    private int next;

    @SuppressWarnings("unchecked")  // don't issue spurious unchecked warnings on tree accesses
                                    // works only with Java 5 compiler, later releases       
    public FileList()
    {
        filenames = new TreeMap();
        filenames.put( 0, null );
        next = 1;
    }
    
    /**
     * Add the filename to the table. First check that it's not null;
     * and that the filename is not already in the list Then add the filename.
     * @param newFilename  name of file to add
     * @throws FilenameLookupException technically, this routine cannot throw this exception
     * @return the filenumber, or Status.INVALID_FILENAME if an error occurs
     */                                      //curr: revisit use of exception. needed?
    public int addFilename( final String newFilename ) throws FilenameLookupException
    {      
        if ( newFilename == null || newFilename.isEmpty() ) {
            return( Status.INVALID_FILENAME );
        }
        
        if ( filenames.containsValue( newFilename )) {
            return( getFileNumber( newFilename ));
        }
        else {
            filenames.put( next++, newFilename);
            return( next - 1 );
        }
    }
    
    /**
     * Find the file name based on the number it was assigned
     * @param fileNumber  the number of the file in the array
     * @throws FilenameLookupException if the fileNumber does not correspond to a filename
     * @return the filename
     */
    public String getFilename( final int fileNumber ) throws FilenameLookupException
    {
        String fName =  filenames.get( fileNumber );
        if( fName == null ) {
            throw( new FilenameLookupException());
        }

        return( fName );
    }

    /**
     * Find the file number based on the name
     * @param filename to get the number for in the tree
     * @return the number
     * @throws FilenameLookupException if filename is not in list
     */
    public int getFileNumber( final String filename ) throws FilenameLookupException
    {
        if( filename == null ) {
            throw( new FilenameLookupException() );
        }

        Set set = filenames.entrySet();
        Iterator iterator = set.iterator();
        while ( iterator.hasNext() ) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if ( entry.getKey().toString().equals( "0" )) {
                continue;  // skip entry 0
            }
            if ( entry.getValue().toString().equals( filename )) {
                String s = entry.getKey().toString();
                return( Integer.valueOf( s ));
            }
        }

        // not found, so throw exception
        throw( new FilenameLookupException() );
    }
    
    /**
     * get number of files stored
     * @return the number of files stored in the tree
     */
    public int getSize()
    {
        return( next - 1 );
    }
}
