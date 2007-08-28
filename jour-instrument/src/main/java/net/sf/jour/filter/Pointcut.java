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

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;
import net.sf.jour.log.Logger;
import net.sf.jour.util.RegExUtil;

import org.apache.regexp.RE;

/**
 * @author michaellif
 */
public class Pointcut extends MatchStringFilter {

	protected static final Logger log = Logger.getLogger();

	private static String modifierKeywords = "((final)|(static)|(native)|(synchronized)|(private)|(public)|(protected))";

	private static String modifierListPattern = "(" + modifierKeywords + "(([;,]!?)" + modifierKeywords + ")*)";

	private static String interfaceMethodsPattern = "(\\S*)\\s*(\\S*)\\s*->\\s*(\\S*)\\s*\\((.*)\\)";

	private static String classMethodsPattern = "(\\S*)\\s*(\\S*)\\s*=>\\s*(\\S*)\\s*\\((.*)\\)";

	private static String methodPattern = "(\\S*)\\s*(\\S*)\\s*\\((.*)\\)";

	private String retType = "";

	private MatchStringListFilter retTypeFilter = new MatchStringListFilter();

	private PointcutModifierListFiler modifierListFiler = new PointcutModifierListFiler();

	private String classType = "*";

	private String interfaceType;

	private String classMethodsType;

	private CtClass filterCtClass;

	private String methodName = "";

	PointcutParamsFilter paramsFilter = new PointcutParamsFilter();

	/**
     * Creates a new Pointcut object.
     */
	public Pointcut(String expr) {
		RE regex = new RE(modifierListPattern);
		if (regex.match(expr)) {
			String mod = regex.getParen(0);
			if (debug) {
				log.debug("modexpr: " + expr);
				log.debug("mod: " + mod);
			}
			modifierListFiler.addPatterns(mod);
			expr = expr.substring(mod.length());
		}
		String[] interfaceResult = RegExUtil.match(expr, interfaceMethodsPattern);
		if ((debug) && (interfaceResult.length != 0)) {
			log.debug("interface expr: " + expr);
			for (int i = 0; i < interfaceResult.length; i++) {
				log.debug(i + " " + interfaceResult[i]);
			}
		}
		if (interfaceResult.length == 4) {
			log.debug("use interface expr: " + expr);
			interfaceType = interfaceResult[1];
		} else {
			interfaceResult = RegExUtil.match(expr, classMethodsPattern);
			if (interfaceResult.length == 4) {
				log.debug("use classMethods as filter expr: " + expr);
				classMethodsType = interfaceResult[1];
			}
		}

		if (interfaceResult.length == 4) {
			retType = interfaceResult[0];
			retTypeFilter.addPatterns(retType);

			methodName = interfaceResult[2];
			super.setPattern(methodName);
			paramsFilter.setParams(interfaceResult[3]);

			modifierListFiler.initialized();
		} else {
			String[] result = RegExUtil.match(expr, methodPattern);
			if (debug) {
				log.debug("expr: " + expr);
				for (int i = 0; i < result.length; i++) {
					log.debug(i + " " + result[i]);
				}
			}

			if (result.length == 3) {
				retType = result[0];
				retTypeFilter.addPatterns(retType);
				methodName = result[1];
				super.setPattern(methodName);
				paramsFilter.setParams(result[2]);
				modifierListFiler.initialized();
			} else {
				log.error("wrong pointcut expr \"" + expr + "\"");
			}
		}
	}

	public int matchState(Object obj) {
		if (obj instanceof CtClass) {
			return b2Match(acceptClass((CtClass) obj));
		} else if (obj instanceof CtMethod) {
			return b2Match(acceptMethod((CtMethod) obj));
		} else {
			return super.matchState(obj);
		}
	}

	private CtClass getclassAsMethodsFilter(ClassPool pool) {
		if (this.filterCtClass != null) {
			return this.filterCtClass;
		}

		if (this.interfaceType != null) {
			return loadFilterClass(pool, this.interfaceType);
		} else if (this.classMethodsType != null) {
			return loadFilterClass(pool, this.classMethodsType);
		} else {
			return null;
		}
	}

	private CtClass loadFilterClass(ClassPool pool, String className) {
		if (className == null) {
			return null;
		}
		try {
			this.filterCtClass = pool.get(className);
		} catch (NotFoundException nfe) {
			log.error("Error ", nfe);
			throw new Error(nfe.getMessage());
		}
		return this.filterCtClass;
	}

	public boolean acceptClass(CtClass clazz) {
		return acceptClass(clazz.getName()) && acceptImplements(clazz);
	}

	public boolean acceptImplements(CtClass clazz) {
		if (this.interfaceType == null) {
			return true;
		}
		CtClass ka[];
		try {
			ka = clazz.getInterfaces();
		} catch (NotFoundException e) {
			log.error("Error", e);
			throw new Error(e.getMessage());
		}
		for (int i = 0; i < ka.length; i++) {
			String name = ka[i].getName();
			if (name.equals(interfaceType)) {
				return true;
			}
		}
		return false;
	}

	protected boolean acceptClass(String clazz) {
		return super.accept(getGlobPattern(classType), clazz);
	}

	public boolean acceptMethod(CtMethod method) {
		try {
			String name = method.getName();
			CtClass[] paramTypes = method.getParameterTypes();

			CtClass mFilterClass = getclassAsMethodsFilter(method.getDeclaringClass().getClassPool());
			if (mFilterClass != null) {
				CtMethod has = null;
				try {
					// this method does not search the superclasses
					has = mFilterClass.getDeclaredMethod(name, paramTypes);
				} catch (NotFoundException e) {
					return false;
				}
				if (has == null) {
					return false;
				}
			}

			String[] params = new String[paramTypes.length];
			for (int i = 0; i < paramTypes.length; i++) {
				params[i] = paramTypes[i].getName();
			}

			String retType = method.getReturnType().getName();

			return super.match(name) && retTypeFilter.match(retType) && paramsFilter.match(params)
					&& modifierListFiler.match(method.getModifiers());

		} catch (NotFoundException nfe) {
			log.error("Error", nfe);
			return false;
		}
	}

	boolean acceptMethod(String method, String[] params, String retType) {
		// was TODO to add check for retType and params
		// return super.accept(getGlobPattern(methodName), method);
		return super.match(method) && retTypeFilter.match(retType) && paramsFilter.match(params);
	}

	public boolean acceptConstr(CtConstructor constr) {
		try {
			CtClass[] paramTypes = constr.getParameterTypes();
			String[] params = new String[paramTypes.length];
			for (int i = 0; i < paramTypes.length; i++) {
				params[i] = paramTypes[i].getName();
			}
			return acceptConstr(params);
		} catch (NotFoundException nfe) {
			nfe.printStackTrace();
			return false;
		}
	}

	protected boolean acceptConstr(String[] params) {
		// TODO to add check for params
		// return super.accept(getGlobPattern(methodName), "new");
		return super.match("new");
	}

	public String getMethodName() {
		return methodName;
	}

	public String getRetType() {
		return retType;
	}

}
