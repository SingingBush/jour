package net.sf.jour.instrumentor;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewMethod;
import net.sf.jour.InterceptorException;
import net.sf.jour.util.StringUtil;

public class ReplaceMethodInstrumentor extends AbstractInstrumentor {

	private String code;

	private String oldMethodSufix = "$orig";

    public ReplaceMethodInstrumentor() {
    }

    /**
     * Convenience for defining source code used for replacement which would otherwise be defined using setter
     * @param code the source code that will be used to replace method body
     * @since 2.1.1
     */
    public ReplaceMethodInstrumentor(final String code) {
        this.code = code;
    }

    /**
     * @param code the source code that will be used to replace method body
     */
    public void code(String code) {
		this.code = code;
	}

	public void oldMethodSufix(String oldMethodSufix) {
		this.oldMethodSufix = oldMethodSufix;
	}

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
		try {
            final String mname = method.getName();
			//  rename old method to synthetic name, then duplicate the
			//  method with original name for use as interceptor
			final String nname = mname + oldMethodSufix;
			method.setName(nname);

            final CtMethod mnew = CtNewMethod.copy(method, mname, clazz, null);

			// replace the body of the interceptor method with generated
			// code block and add it to class
            if(code == null) {
                log.warn("No replacement source code provided. '{}' method will be replaced by empty implementation", mname);

                final CtClass returnType = method.getReturnType();

                if(method.getReturnType() == null) {
                    mnew.setBody(StringUtil.replaceAll("", "$origMethodName", nname));
                } else {
                    if(returnType.isPrimitive()) {
                        log.warn("Return type is primitive so cannot return null (default behaviour for no provided source).");
                        throw new InterceptorException("Cannot replace method body " + mname + " without replacement source being provided");
                    }

                    // todo: handle Optional<> when min Java version increased. Default to return Optional.empty();

                    mnew.setBody(StringUtil.replaceAll("return null;", "$origMethodName", nname));
                }
            } else {
                mnew.setBody(StringUtil.replaceAll(code, "$origMethodName", nname));
            }
			clazz.addMethod(mnew);
		} catch (Exception e) {
			throw new InterceptorException("Failed to replace method body " + method.getName(), e);
		}
		return true;
	}

}
