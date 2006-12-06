package net.sf.jour.instrumentor;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import net.sf.jour.InterceptorException;
import net.sf.jour.filter.PointcutListFilter;

public class ConfigTestInstrumentor extends AbstractInstrumentor {
	
	boolean instrumentClass(CtClass clazz) throws InterceptorException {
		return false;
	}

	boolean instrumentConstructor(CtClass clazz, CtConstructor constructor) throws InterceptorException {
		return false;
	}

	boolean instrumentMethod(CtClass clazz, CtMethod method) throws InterceptorException {
		return true;
	}
	
	public PointcutListFilter getPointcuts() {
		return this.pointcuts;
	}
}
