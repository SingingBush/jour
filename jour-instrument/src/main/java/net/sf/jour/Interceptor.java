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
package net.sf.jour;

import net.sf.jour.instrumentor.Instrumentor;

import javassist.*;

/**
 * 
 * Created on 02.10.2004
 * 
 * Contributing Author(s):
 * 
 * Misha Lifschitz <mishalifschitz at users.sourceforge.net> (Inital implementation) 
 * Vlad Skarzhevskyy <vlads at users.sourceforge.net> (Inital implementation)
 * 
 * @author michaellif
 * @version $Revision$ ($Author$) $Date$
 */
public class Interceptor {

	ClassPool pool;

	String className;

	Instrumentor[] instrumentors;

	private long countMethods;

	private long countCounstructors;

	/**
	 * Flag any modification to class.
	 */
	boolean modified;

	/**
	 * Creates a new Interceptor object.
	 */
	public Interceptor(Config config, ClassPool pool, String className, Instrumentor[] instrumentors) throws InterceptorException {
		if ((instrumentors == null) || (instrumentors.length == 0)) {
			throw new InterceptorException("Should be at least one instrumentor");
		}

		this.pool = pool;
		this.className = className;
		this.instrumentors = instrumentors;
	}

	public byte[] instrument(byte[] bytes) throws InterceptorException {
		try {
			pool.insertClassPath(new ByteArrayClassPath(className, bytes));

			try {
				return instrument().toBytecode();
			} catch (InterceptorException ie) {
				throw ie;
			} catch (Exception e) {
				e.printStackTrace();
				throw new InterceptorException("Profiling error @instrumentClass@setSerialVersionUID. " + e);
			}
		} catch (InterceptorException e) {
			System.out.println("instrument error " + e + " for class : " + className);
			e.printStackTrace();
		}

		return bytes;
	}

	public CtClass instrument() throws InterceptorException {
		CtClass clazz = null;

		try {
			clazz = pool.get(className);
		} catch (NotFoundException nfe) {
			nfe.printStackTrace();
			throw new InterceptorException("Class " + className + " is not found in class pool." + nfe);
		}

		if ((clazz != null) && !clazz.isInterface() && !clazz.isModified()) {
			try {
				SerialVersionUID.setSerialVersionUID(clazz);
				this.modified = false;
				for (int i = 0; i < instrumentors.length; i++) {
					// instrumentor has a total running count per instance
					// interceptor should return currently modified count
					long beforeCountCounstructors = instrumentors[i].getCountCounstructors();
					long beforeCountMethods = instrumentors[i].getCountMethods();

					// Go to actual instrumentation
					this.modified = instrumentors[i].instrument(clazz) || this.modified;

					this.countCounstructors += instrumentors[i].getCountCounstructors() - beforeCountCounstructors;
					this.countMethods += instrumentors[i].getCountMethods() - beforeCountMethods;
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new InterceptorException("Profiling error @instrumentClass@setSerialVersionUID. " + e);
			}
		}

		return clazz;
	}

	/**
	 * @return Returns true if any modification has been made to the class.
	 */
	public boolean isModified() {
		return modified;
	}

	/**
	 * @return Returns the countCounstructors.
	 */
	public long getCountCounstructors() {
		return countCounstructors;
	}

	/**
	 * @return Returns the countMethods.
	 */
	public long getCountMethods() {
		return countMethods;
	}
}
