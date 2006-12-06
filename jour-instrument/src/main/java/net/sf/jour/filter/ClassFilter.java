/*
 * Jour - java profiler and monitoring library
 *
 * Copyright (C) 2004 Jour team
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
package net.sf.jour.filter;


/**
 * @author michaellif
 */
public class ClassFilter extends MatchStringListFilter {

	// private String[] typedefArray;
	// private Pattern[] patternArray;
	// private RE[] patternArray;

	public ClassFilter(String def) {
		super(def);
		/*
         * StringTokenizer tokenizer = new StringTokenizer(def,",");
         * typedefArray = new String[tokenizer.countTokens()]; //patternArray =
         * new Pattern[tokenizer.countTokens()]; patternArray = new
         * RE[tokenizer.countTokens()]; int i = 0; while
         * (tokenizer.hasMoreTokens()) { typedefArray[i] =
         * tokenizer.nextToken().trim(); patternArray[i] =
         * getGlobPattern(typedefArray[i]); i++; }
         */
	}

	public boolean accept(String clazz) {
		return match(clazz);
	}

	/*
     * public boolean accept(String clazz) { if (typedefArray.length == 0) {
     * return true; } for (int i = 0; i < patternArray.length; i++) { if
     * (super.accept(patternArray[i], clazz)) { return true; } } return false; }
     */
}
