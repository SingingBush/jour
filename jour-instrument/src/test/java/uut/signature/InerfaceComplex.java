/*
 * Jour - bytecode instrumentation library
 *
 * Copyright (C) 2009 Vlad Skarzhevskyy
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
 */
package uut.signature;

public interface InerfaceComplex {

    public static final int CONST_INT = 1;
    
    public static final boolean CONST_BOOL = true;
    
    public static final String CONST_STRING = "StringVal";
    
    public interface InerfaceInner {
        
        public int getInner();
        
    }
    
    public int getInt();
    
}
