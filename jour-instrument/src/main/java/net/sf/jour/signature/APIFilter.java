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
 */
package net.sf.jour.signature;

import javassist.CtClass;
import javassist.CtMember;
import javassist.Modifier;

/**
 * @author vlads
 *
 */
public class APIFilter {

	public static boolean isAPIClass(CtClass klass) {
		int mod = klass.getModifiers();
		if (Modifier.isPrivate(mod)) {
			return false;
		}
		return true;
	}
	
	public static boolean isAPIMember(CtMember member) {
		if (Modifier.isPublic(member.getModifiers())) {
			return true;
		} else if (Modifier.isPrivate(member.getModifiers())) {
			return false;
		} else {
			return true;
		}
	}
	
	static int filterModifiers(int mod) {
		if (Modifier.isNative(mod)) {
			mod = mod - Modifier.NATIVE;
		}
		if (Modifier.isSynchronized(mod)) {
			mod = mod - Modifier.SYNCHRONIZED;
		}
		if (Modifier.isInterface(mod)) {
			mod = mod - Modifier.INTERFACE;
			mod = mod - Modifier.ABSTRACT;
		}
		return mod;
	}
}
