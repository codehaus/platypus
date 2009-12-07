/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

/**
 *  Status and error codes used in Platypus
 */
public class Status
{    
    /*
     * utility values
     */

    public static final boolean ON              =       true;
    public static final boolean OFF             =       false;

    
    public static final int OK                  =         0;    // no error
    public static final int NO_ERROR            =         0;
    
    public static final int COMPLETE            =       Integer.MAX_VALUE;

    /*
     *  status items
     */

    public static final int AT_EOF              =        -1;

    /*
     * error codes. To test for any/all possible errors in code, test for <= Status.ERR
     */
    
    public static final int ERR                 =       -10;    // the generic non-fatal error

    public static final int IO_ERR              =       -11;
    public static final int ILLEGAL_OP          =       -12;
    public static final int OP_NOT_PERMITTED    =       -13;
    public static final int UNSUPPORTED_COMMAND =       -14;

    public static final int FILE_NOT_FOUND_ERR  =       -50;
    public static final int FILE_NOT_READABLE_ERR =     -51;
    public static final int INVALID_INPUT_FILE  =       -52;
    public static final int INVALID_FILENAME    =       -53;

    public static final int INVALID_DATA        =      -100;    // the generic invalid data item
    public static final int INVALID_PARAM       =      -101;
    public static final int INVALID_PARAM_NULL  =      -102;
    public static final int MISSING_PARAM       =      -103;
    public static final int UNFIXABLE_PARSE_ERR =      -104;
    public static final int INVALID_UNIT        =      -105;
    public static final int INVALID_GDD         =      -106;
    public static final int INVALID_COMMAND     =      -108;
    public static final int INVALID_PLUGIN_URL  =      -109;

    public static final int INVALID_TOKEN       =      -120;
    public static final int INVALID_TOKEN_NULL  =      -121;
    public static final int MISSING_TOKEN       =      -122;
    
    public static final int UNCLOSED_COMMENT_BLK=      -151;
    public static final int UNCLOSED_COMMAND    =      -152;

    public static final int DOCUMENT_CLOSED_ERR =      -201;
    
    public static final int ERR_MISSING_CONFIG_FILE =  -401;    //** the new series **/
    public static final int ERR_UNSUPPORTED_FORMAT =   -402;

    public static final int ERR_COMMAND_PARAM   =      -421;

    public static final int UNREACHABLE_ERR     =      -901;    // reached code that s/be unreachable

    public static final int FATAL_ERR           =      -999;    // non-specific fatal error
}
