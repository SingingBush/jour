package net.sf.jour.instrumentor;

import javassist.CtClass;

import java.util.List;
import java.util.Vector;

public class InstrumentorResultsImpl implements InstrumentorResults {

	private boolean modified;

	private long countCounstructors;

	private long countMethods;

	private List<CtClass> createdClasses;

	public static final InstrumentorResults NOT_MODIFIED = new InstrumentorResultsImpl();

	public InstrumentorResultsImpl() {

	}

	public InstrumentorResultsImpl(long countCounstructors, long countMethods) {
		this.countCounstructors = countCounstructors;
		this.countMethods = countMethods;
		this.modified = true;
	}

	public InstrumentorResultsImpl(long countCounstructors, long countMethods, List<CtClass> createdClasses) {
		this(countCounstructors, countMethods);
		this.createdClasses = createdClasses;
	}

	public InstrumentorResultsImpl(InstrumentorResults r1, InstrumentorResults r2) {
		this.modified = r1.isModified() || r2.isModified();
		if (this.modified) {
			this.countCounstructors = r1.getCountCounstructors() + r2.getCountCounstructors();
			this.countMethods = r1.getCountMethods() + r2.getCountMethods();
			if ((r1.getCreatedClasses() != null) || (r2.getCreatedClasses() != null)) {
				this.createdClasses = new Vector<>();
				if ((r1.getCreatedClasses() != null)) {
					this.createdClasses.addAll(r1.getCreatedClasses());
				}
				if ((r2.getCreatedClasses() != null)) {
					this.createdClasses.addAll(r2.getCreatedClasses());
				}
			}
		}
	}

	/**
	 * @return Returns true if any modification has been made to the class.
	 */
    @Override
	public boolean isModified() {
		return modified;
	}

    @Override
	public long getCountCounstructors() {
		return countCounstructors;
	}

    @Override
	public long getCountMethods() {
		return countMethods;
	}

    @Override
	public List<CtClass> getCreatedClasses() {
		return createdClasses;
	}

}
