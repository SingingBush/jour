package net.sf.jour.instrumentor;

import java.util.List;

import javassist.CtClass;
import net.sf.jour.InterceptorException;

public interface InstrumentorResults {

	/**
	 * @return Returns true if any modification has been made to the class.
	 */
	boolean isModified();

	/**
	 * @return List<CtClass> of created classes, may be empty.
	 * @throws InterceptorException
	 */
	List<CtClass> getCreatedClasses();

    long getCountCounstructors();

    long getCountMethods();
}
