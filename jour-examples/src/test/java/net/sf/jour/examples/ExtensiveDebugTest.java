package net.sf.jour.examples;

import junit.framework.TestCase;

public class ExtensiveDebugTest extends TestCase {

	public void testDebugRemoved() {
		ExtensiveDebug e = new ExtensiveDebug("is removed?");
		assertNull("debug function called", e.debugCalled);
	}
	
}
