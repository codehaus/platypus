/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import java.util.Stack;

/**
 * Keeps track of bulleted lists that are in use.
 *
 * @author alb
 */
public class BulletLists extends Stack<Object>
{
    Stack<Object> lists = null;

    public BulletLists()
    {
        lists = new Stack<Object>();
    }
}
