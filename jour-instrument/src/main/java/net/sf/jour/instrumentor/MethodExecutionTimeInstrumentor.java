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

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewMethod;
import net.sf.jour.InterceptorException;

/**
 *
 * Contributing Author(s):
 *
 *   Misha Lifschitz <mishalifschitz at users.sourceforge.net> (Inital implementation)
 *   Vlad Skarzhevskyy <vlads at users.sourceforge.net> (Inital implementation)
 *
 * @author vlads
 * @version $Revision$ ($Author$) $Date$
 */
public class MethodExecutionTimeInstrumentor extends AbstractInstrumentor implements InstrumentorConsts {

    @Override
	public boolean instrumentClass(CtClass clazz) throws InterceptorException {
		return false;
    }

    @Override
    public boolean instrumentMethod(CtClass clazz, CtMethod method) throws InterceptorException {
        addTiming(clazz, method);
		return true;
    }

    @Override
    public boolean instrumentConstructor(CtClass clazz, CtConstructor constructor) throws InterceptorException {
         return false;
    }

    private static void addTiming(CtClass clazz, CtMethod method) throws InterceptorException {
        try {
            final String originalMethodName = method.getName();
            //  rename old method to synthetic name, then duplicate the
            //  method with original name for use as interceptor
            final String nname = "jour$"+ originalMethodName + "$impl";
            method.setName(nname);
            final CtMethod mnew = CtNewMethod.copy(method, originalMethodName, clazz, null);

            //  start the body text generation by saving the start time
            //  to a local variable, then call the timed method; the
            //  actual code generated needs to depend on whether the
            //  timed method returns a value
            final String type = method.getReturnType().getName();

            final StringBuffer body = new StringBuffer();
            body.append("{\nlong start = System.currentTimeMillis();\n");
            if (!"void".equals(type)) {
                body.append(type + " result = ");
            }
            body.append(nname + "($$);\n");

            //  finish body text generation with call to print the timing
            //  information, and return saved value (if not void)
            //body.append("net.sf.jour.rt.agent.Elog.logEvent(new net.sf.jour.rt.agent.MethodExecutionTimeEvent(\"" + mname + "\", System.currentTimeMillis()-start));\n");
            body.append("System.out.println(\"" + originalMethodName + "\" + (System.currentTimeMillis() - start));\n");
            if (!"void".equals(type)) {
                body.append("return result;\n");
            }
            body.append("}");

            //  replace the body of the interceptor method with generated
            //  code block and add it to class
            mnew.setBody(body.toString());
            clazz.addMethod(mnew);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InterceptorException("Failed to add timing to method " + method.getName(), e);
        }
    }
}
