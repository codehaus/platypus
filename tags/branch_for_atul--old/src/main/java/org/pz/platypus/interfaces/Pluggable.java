/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.interfaces;

import org.pz.platypus.GDD;
import org.pz.platypus.CommandLineArgs;

/**
 * Interface for Platypus plugins
 *
 * @author alb
 */
public interface Pluggable
{  
    public void process( GDD gdd, final CommandLineArgs clArgs );
}