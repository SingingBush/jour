package net.sf.jour.instrumentor;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import net.sf.jour.InterceptorException;

public class MakeEmptyMethodInstrumentor extends AbstractInstrumentor {
	
	public boolean instrumentClass(CtClass clazz) throws InterceptorException {
		return false;
	}

	public boolean instrumentConstructor(CtClass clazz, CtConstructor constructor) throws InterceptorException {
		return false;
	}

	public boolean instrumentMethod(CtClass clazz, CtMethod method) throws InterceptorException {
        // replace the body of the intercepted method with generated code block
		try {
			method.setBody("{}");
		} catch (CannotCompileException e) {
			 throw new InterceptorException("Failed to MakeEmptyMethod " + method.getName(), e);
		}
		return true;
	}
}
