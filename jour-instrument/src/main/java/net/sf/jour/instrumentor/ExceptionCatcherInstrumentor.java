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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;
import net.sf.jour.InterceptorException;

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
public class ExceptionCatcherInstrumentor extends AbstractInstrumentor {

    private List<String> exceptions = new ArrayList<>();

    private String code;

    /**
     * Creates a new ExceptionCatcherInstrumentor object.
     */
    public ExceptionCatcherInstrumentor() {
    }

    /**
     * Convenience for defining source code used for catch block which would otherwise be defined using setter
     * @param exceptionType the typeof exception that needs to be caught
     * @param code the source code that will be used for catch block
     * @since 2.1.1
     */
    public ExceptionCatcherInstrumentor(final String exceptionType, final String code) {
        exceptions.add(exceptionType);
        this.code = code;
    }

    /**
     * Convenience for defining source code used for catch block which would otherwise be defined using setter
     * @param exceptionType the typeof exception that needs to be caught
     * @param code the source code that will be used for catch block
     * @since 2.1.1
     */
    public ExceptionCatcherInstrumentor(Class<? extends Throwable> exceptionType, String code) {
        exceptions.add(exceptionType.getName());
        this.code = code;
    }

    /**
     * @param exception the exception type that needs to be caught
     * @since 2.1.1
     */
    public void exceptionType(Class<? extends Throwable> exception) {
        exceptions.add(exception.getName());
    }

    public void exceptionType(String exception) {
        exceptions.add(exception);
    }

	public void code(String code) {
		this.code = code;
	}

    @Override
    public boolean instrumentClass(CtClass clazz) throws InterceptorException {
    	return false;
    }

    @Override
    public boolean instrumentMethod(CtClass clazz, CtMethod method) throws InterceptorException {
        if (method.isEmpty()) {
            return false;
        }

        try {
        	boolean modified = false;
            for (final String exception : exceptions) {
                addCatch(clazz, method, exception);
                modified = true;
            }
			return modified;
        } catch (Exception e) {
            e.printStackTrace();
            throw new InterceptorException("Failed to add catch to method " + method + " of class " + clazz);
        }
    }

    private void addCatch(final CtClass clazz, final CtMethod method, final String exception) throws NotFoundException, CannotCompileException {
        final String mname = method.getName();
        final CtClass etype = ClassPool.getDefault().get(exception);

        final String sourceCode = this.code == null?
            "{ System.out.println(\"Exception " + exception + " at " + clazz.getName() + "." + mname + "\"); throw $e; }" :
            this.code;

        method.addCatch(sourceCode, etype);
    }

    @Override
    public boolean instrumentConstructor(CtClass clazz, CtConstructor constructor) throws InterceptorException {
        return false;
    }

}
