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
package net.sf.jour.instrumentor;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;
import net.sf.jour.InterceptorException;

public class MakeEmptyMethodInstrumentor extends AbstractInstrumentor {

    @Override
	public boolean instrumentClass(CtClass clazz) throws InterceptorException {
		return false;
	}

    @Override
	public boolean instrumentConstructor(CtClass clazz, CtConstructor constructor) throws InterceptorException {
		return false;
	}

    @Override
	public boolean instrumentMethod(CtClass clazz, CtMethod method) throws InterceptorException {
        // replace the body of the intercepted method with generated code block
		try {
			method.setBody(emptyBody(method.getReturnType()));
		} catch (CannotCompileException e) {
			throw new InterceptorException("Failed to MakeEmptyMethod " + method.getName(), e);
		} catch (NotFoundException e) {
			throw new InterceptorException("Failed to MakeEmptyMethod " + method.getName(), e);
		}
		return true;
	}

	public static String emptyBody(CtClass returnType) {
        StringBuffer body = new StringBuffer();
        body.append("{");
        if (!returnType.isPrimitive()) {
            body.append("return null;");
        } else if (returnType != CtClass.voidType) {
        	if (returnType == CtClass.booleanType) {
        		body.append("return false;");
        	} else {
        		body.append("return 0;");
        	}
        }
        body.append("}");
		return body.toString();
	}
}
