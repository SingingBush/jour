package net.sf.jour.instrumentor;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import net.sf.jour.InterceptorException;
import net.sf.jour.filter.Pointcut;

public class CallEachMethodInstrumentor extends AbstractInstrumentor {

	Pointcut pointcut = new Pointcut("* test*()");
	
	
	public boolean instrumentClass(CtClass clazz) throws InterceptorException {
		return false;
	}

	public boolean instrumentConstructor(CtClass clazz, CtConstructor constructor) throws InterceptorException {
		return false;
	}

	public boolean instrumentMethod(CtClass clazz, CtMethod method) throws InterceptorException {
		Iterator methods = Arrays.asList(clazz.getDeclaredMethods()).iterator();
		List testMethods = new Vector();
		while (methods.hasNext()) {
			CtMethod testMethod = (CtMethod) methods.next();
			if (pointcut.acceptMethod(testMethod)) {
				testMethods.add(testMethod);
			}
		}
		if (testMethods.isEmpty()) {
			return false;
		}
		StringBuffer body = new StringBuffer();
		body.append("{\n");
		for(Iterator i = testMethods.iterator(); i.hasNext(); ) {
			CtMethod testMethod = (CtMethod)i.next();
			body.append(testMethod.getName()).append("();\n");
		}
		body.append("}");
        // replace the body of the intercepted method with generated code block
		try {
			method.setBody(body.toString());
		} catch (CannotCompileException e) {
			 throw new InterceptorException("Failed to add CallEachMethod " + method.getName() + " " + body.toString(), e);
		}
		return true;
	}

}
