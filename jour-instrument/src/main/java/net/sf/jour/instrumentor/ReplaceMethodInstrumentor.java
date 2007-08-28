package net.sf.jour.instrumentor;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewMethod;
import net.sf.jour.InterceptorException;

public class ReplaceMethodInstrumentor  extends AbstractInstrumentor {

	private String code;
	
	private String oldMethodSufix = "$orig";
	
	public void code(String code) {
		this.code = code;
	}
	
	public void oldMethodSufix(String oldMethodSufix) {
		this.oldMethodSufix = oldMethodSufix;
	}
	
	public boolean instrumentClass(CtClass clazz) throws InterceptorException {
		return false;
	}

	public boolean instrumentConstructor(CtClass clazz, CtConstructor constructor) throws InterceptorException {
		return false;
	}

	public boolean instrumentMethod(CtClass clazz, CtMethod method) throws InterceptorException {
		try {
			String mname = method.getName();
			//  rename old method to synthetic name, then duplicate the
			//  method with original name for use as interceptor
			String nname = mname + oldMethodSufix;
			method.setName(nname);

			CtMethod mnew = CtNewMethod.copy(method, mname, clazz, null);

			//  replace the body of the interceptor method with generated
			//  code block and add it to class
			mnew.setBody(code.replace("$origMethodName", nname));
			clazz.addMethod(mnew);
		} catch (Exception e) {
			throw new InterceptorException("Failed to replace method body " + method.getName(), e);
		}
		return true;
	}

}
