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

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
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
public class InstanceCounterInstrumentor extends AbstractInstrumentor
    implements InstrumentorConsts {

    public boolean instrumentClass(CtClass clazz) throws InterceptorException {
        try {
            addCounterDecrementer(clazz);
			return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new InterceptorException(
                "Failed to add instance counter member to class " + clazz);
        }
    }

    public boolean instrumentMethod(CtClass clazz, CtMethod method)
        throws InterceptorException {
			return false;
    }

    public boolean instrumentConstructor(CtClass clazz, CtConstructor constructor)
        throws InterceptorException {
        if (constructor.isClassInitializer()) {
            return false;
        }

        try {
            addCounterIncrementer(clazz, constructor);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new InterceptorException(
                "Failed to add instance counter incrementor to constructor of class " +
                clazz);
        }
    }

    private void addCounterIncrementer(CtClass clazz, CtConstructor constructor)
        throws NotFoundException, CannotCompileException {
        //String cname = constructor.getName();
        String code = "net.sf.jour.rt.agent.Elog.logEvent(new net.sf.jour.rt.agent.InstanceCounterEvent(this.getClass(), true));";
        constructor.insertAfter(code);
    }

    private void addCounterDecrementer(CtClass clazz)
        throws CannotCompileException {
        CtMethod finalize;
        try {
	        finalize = clazz.getDeclaredMethod("finalize",
	                new CtClass[] {});
	        if (finalize != null) {
	            String code = "net.sf.jour.rt.agent.Elog.logEvent(new net.sf.jour.rt.agent.InstanceCounterEvent(this.getClass(), false));";
	            finalize.insertAfter(code, true);
	        }
        } catch (NotFoundException nfe) {
            StringBuffer code = new StringBuffer(
                    "protected void finalize() throws Throwable { super.finalize(); ");
            code.append(
                "net.sf.jour.rt.agent.Elog.logEvent(new net.sf.jour.rt.agent.InstanceCounterEvent(this.getClass(), false));}");
            finalize = CtNewMethod.make(code.toString(), clazz);
            clazz.addMethod(finalize);
        }
    }
    
}
