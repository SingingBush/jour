/*
 * Jour - bytecode instrumentation library
 *
 * Copyright (C) 2007 Vlad Skarzhevskyy
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * 
 * @version $Id$
 * 
 */
package net.sf.jour.signature;

import java.util.Iterator;
import java.util.List;

/**
 * @author vlads
 *
 */
public class ChangeDetectedException extends Exception {

    private static final long serialVersionUID = 1L;

    public ChangeDetectedException(String message) {
        super(message);
    }
    
    ChangeDetectedException(List changed) {
        this(chageList(changed));
    }
    
    private static String chageList(List changed) {
        StringBuffer b = new StringBuffer();
        for (Iterator iterator = changed.iterator(); iterator.hasNext();) {
            String v = (String) iterator.next();
            if (b.length() > 0) {
                b.append("\n");    
            }
            b.append(v);
        }
        return b.toString();
    }

}
