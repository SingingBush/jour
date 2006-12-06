package net.sf.jour.examples;

public class ExtensiveDebug {

	String debugCalled;
	
	public ExtensiveDebug(String param) {
		debug("ExtensiveDebug created", param);
	}

	public void debug(String message, String v) {
		debugCalled = v;
		System.out.println(message + " " + v);
	}
}
