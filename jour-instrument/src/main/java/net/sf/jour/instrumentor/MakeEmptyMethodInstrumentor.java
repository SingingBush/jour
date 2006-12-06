package net.sf.jour.instrumentor;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import net.sf.jour.InterceptorException;

public class MakeEmptyMethodInstrumentor extends AbstractInstrumentor {
	
	boolean instrumentClass(CtClass clazz) throws InterceptorException {
		return false;
	}

	boolean instrumentConstructor(CtClass clazz, CtConstructor constructor) throws InterceptorException {
		return false;
	}

	boolean instrumentMethod(CtClass clazz, CtMethod method) throws InterceptorException {
        // replace the body of the intercepted method with generated code block
		try {
			method.setBody("{}");
		} catch (CannotCompileException e) {
			 throw new InterceptorException("Failed to MakeEmptyMethod " + method.getName(), e);
		}
		return true;
	}
}
