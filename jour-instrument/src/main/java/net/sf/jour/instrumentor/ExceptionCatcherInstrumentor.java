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

    public void exceptionType(String exception) {
        exceptions.add(exception);
    }

	public void code(String code) {
		this.code = code;
	}

    public boolean instrumentClass(CtClass clazz) throws InterceptorException {
    	return false;
    }

    public boolean instrumentMethod(CtClass clazz, CtMethod method)
        throws InterceptorException {
        if (method.isEmpty()) {
            return false;
        }

        try {
        	boolean modified = false;
            for (Iterator iter = exceptions.iterator(); iter.hasNext();) {
                String exception = (String) iter.next();
                addCatch(clazz, method, exception);
				modified = true;
            }
			return modified;
        } catch (Exception e) {
            e.printStackTrace();
            throw new InterceptorException("Failed to add catch to method " +
                method + " of class " + clazz);
        }
    }

    private void addCatch(CtClass clazz, CtMethod method, String exception)
        throws NotFoundException, CannotCompileException {
        String mname = method.getName();
        CtClass etype = ClassPool.getDefault().get(exception);
        StringBuffer codeBuffer = new StringBuffer();
        if (this.code == null) {
        	codeBuffer.append("{ System.out.println(\"Exception ").append(exception).append(" at ");
        	codeBuffer.append(clazz.getName()).append(".").append(mname).append("\");");
        	codeBuffer.append(" throw $e; }");
        } else {
        	codeBuffer.append(this.code);
        }
        method.addCatch(codeBuffer.toString(), etype);
    }

    public boolean instrumentConstructor(CtClass clazz, CtConstructor constructor)
        throws InterceptorException {
			return false;
    }

}
