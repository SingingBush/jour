package net.sf.jour.instrumentor;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.Modifier;
import net.sf.jour.InterceptorException;

public class MakeProtectedMethodInstrumentor extends AbstractInstrumentor {
	
	public boolean instrumentClass(CtClass clazz) throws InterceptorException {
		return false;
	}

	public boolean instrumentConstructor(CtClass clazz, CtConstructor constructor) throws InterceptorException {
		return false;
	}

	public boolean instrumentMethod(CtClass clazz, CtMethod method) throws InterceptorException {
        // replace the body of the intercepted method with generated code block
		int mod = method.getModifiers();
		if (Modifier.isPublic(mod)) {
			method.setModifiers(Modifier.setProtected(mod));
		}
		
		return true;
	}
}
