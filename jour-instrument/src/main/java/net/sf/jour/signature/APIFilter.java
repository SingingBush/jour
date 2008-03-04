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

	public static final String javaLangString = "java.lang.String";

	public static final int PUBLIC = 1;

	public static final int PROTECTED = 2;

	public static final int PACKAGE = 3;

	public static final int PRIVATE = 4;

	private int level;

	private String packages;

	public APIFilter(int level) throws IllegalArgumentException {
		this.level = level;
		if (this.level > PRIVATE) {
			throw new IllegalArgumentException("level " + level);
		}
	}

	public APIFilter(String level) throws IllegalArgumentException {
		this.level = getAPILevel(level);
	}

	public APIFilter(String level, String packages) throws IllegalArgumentException {
		this.level = getAPILevel(level);
		this.packages = packages;
	}

	public static int getAPILevel(String level) throws IllegalArgumentException {
		if (level == null) {
			return PROTECTED;
		} else if (level.equalsIgnoreCase("public")) {
			return PUBLIC;
		} else if (level.equalsIgnoreCase("protected")) {
			return PROTECTED;
		} else if (level.equalsIgnoreCase("package")) {
			return PACKAGE;
		} else if (level.equalsIgnoreCase("private")) {
			return PRIVATE;
		} else {
			throw new IllegalArgumentException("level " + level);
		}
	}

	public boolean isAPIModifier(int mod) {
		if (Modifier.isPublic(mod)) {
			return (level >= PUBLIC);
		} else if (Modifier.isProtected(mod)) {
			return (level >= PROTECTED);
		} else if (Modifier.isPackage(mod)) {
			return (level >= PACKAGE);
		} else if (Modifier.isPrivate(mod)) {
			return (level >= PRIVATE);
		}
		return true;
	}

	public boolean isAPIClass(CtClass klass) {
		return isAPIModifier(klass.getModifiers());
	}

	public boolean isAPIMember(CtMember member) {
		return isAPIModifier(member.getModifiers());
	}

	public static boolean isExportableConstantType(CtClass klass) {
		if (klass.isPrimitive()) {
			return true;
		} else if (javaLangString.equals(klass.getName())) {
			return true;
		}
		return false;
	}

	public APIFilter getLessRestrictiveFilter() throws IllegalArgumentException {
		return new APIFilter(this.level + 1);
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
