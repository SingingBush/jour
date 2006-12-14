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
package net.sf.jour.instrumentor;

import net.sf.jour.InterceptorException;
import net.sf.jour.filter.*;

import java.util.*;

import org.apache.log4j.Logger;

import javassist.*;

/**
 *
 * Contributing Author(s):
 *
 *   Misha Lifschitz <mishalifschitz at users.sourceforge.net> (Inital implementation)
 *   Vlad Skarzhevskyy <vlads at users.sourceforge.net> (Inital implementation)
 *
 * @author michaellif
 * @version $Revision$ ($Author$) $Date$
 */
public abstract class AbstractInstrumentor implements Instrumentor {

	protected static final Logger log = Logger.getLogger(AbstractInstrumentor.class);

	protected PointcutListFilter pointcuts;

	private long countMethods;

	private long countCounstructors;

	protected List createdClasses;
	
	protected AbstractInstrumentor() {
		log.debug("AbstractInstrumentor Created");
		createdClasses = new Vector(); 
	}

	public List instrument(CtClass clazz) throws InterceptorException {
		if (clazz.isInterface()) {
			return null;
		}
		boolean modified = false;
		
		HashMap instrumented = new HashMap();
		
		//log.debug("Start instrumenting:" + clazz.getName());
		
		for (Iterator i = pointcuts.iterator(); i.hasNext();) {
			Pointcut pointcut = (Pointcut) i.next();

			if (pointcut.acceptClass(clazz)) {
				
				if (!instrumented.containsKey(clazz)) {
					log.debug("instrumenting class:" + clazz.getName());
					modified = instrumentClass(clazz) || modified;
					instrumented.put(clazz, null);
				}

				Iterator methods = Arrays.asList(clazz.getDeclaredMethods()).iterator();
				while (methods.hasNext()) {
					CtMethod method = (CtMethod) methods.next();
					if (!instrumented.containsKey(method) && pointcut.acceptMethod(method)
					// This will check the pointcut exceptions.
							&& pointcuts.match(method)) {
						
						log.debug("instrumenting method:" + clazz.getName() + "." + method.getName() + "(" + method.getSignature() + ")");
						
						if (instrumentMethod(clazz, method)) {
							countMethods++;
							modified = true;
						}
						instrumented.put(method, null);
					}
				}

				Iterator constructors = Arrays.asList(clazz.getConstructors()).iterator();
				while (constructors.hasNext()) {
					CtConstructor constructor = (CtConstructor) constructors.next();
					//TODO check to process static constructors
					if (!constructor.isClassInitializer() && pointcut.acceptConstr(constructor)
							&& !instrumented.containsKey(constructor)) {
						log.debug("instrumenting constructor:" + clazz.getName() + "." + constructor.getName() + "(" + constructor.getSignature() + ")");
						if (instrumentConstructor(clazz, constructor)) {
							countCounstructors++;
							modified = true;
						}
						instrumented.put(constructor, null);
					}
				}
			}
		}
		log.debug("End instrumenting:" + clazz.getName());
		if (modified) {
			return createdClasses;
		} else {
			return null;
		}
	}

	public long getCountCounstructors() {
		return countCounstructors;
	}

	public long getCountMethods() {
		return countMethods;
	}

	public void setPointcuts(PointcutListFilter pointcuts) {
		this.pointcuts = pointcuts;
	}

	public abstract boolean instrumentClass(CtClass clazz) throws InterceptorException;

	public abstract boolean instrumentMethod(CtClass clazz, CtMethod method) throws InterceptorException;

	public abstract boolean instrumentConstructor(CtClass clazz, CtConstructor constructor) throws InterceptorException;
}