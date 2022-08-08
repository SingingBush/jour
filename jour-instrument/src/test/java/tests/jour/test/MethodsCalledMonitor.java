package tests.jour.test;

import java.util.List;
import java.util.Vector;

public abstract class MethodsCalledMonitor {

	private List methodsCalled = new Vector();

	public void called() {
		Throwable t = new Throwable();
		StackTraceElement[] e = t.getStackTrace();
		methodsCalled.add(e[1].getMethodName());
	}

	public void called(String name) {
		methodsCalled.add(name);
	}

	public List getMethodsCalled() {
		return methodsCalled;
	}

}
