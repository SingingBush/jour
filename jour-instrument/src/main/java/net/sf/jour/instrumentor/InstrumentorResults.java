package net.sf.jour.instrumentor;

import java.util.List;

import net.sf.jour.InterceptorException;

public interface InstrumentorResults {

	/**
	 * @return Returns true if any modification has been made to the class.
	 */
	public boolean isModified();

	/**
	 * @return List<CtClass> of created classes, may be empty.
	 * @throws InterceptorException
	 */
	public List getCreatedClasses();
	
    public long getCountCounstructors();

    public long getCountMethods();
}
