package tests.jour.instrumentor;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import net.sf.jour.InterceptorException;
import net.sf.jour.filter.PointcutListFilter;
import net.sf.jour.instrumentor.AbstractInstrumentor;

public class ConfigTestInstrumentor extends AbstractInstrumentor {

	public boolean instrumentClass(CtClass clazz) throws InterceptorException {
		return false;
	}

	public boolean instrumentConstructor(CtClass clazz, CtConstructor constructor) throws InterceptorException {
		return false;
	}

	public boolean instrumentMethod(CtClass clazz, CtMethod method) throws InterceptorException {
		return true;
	}

	public PointcutListFilter getPointcuts() {
		return this.pointcuts;
	}
}
