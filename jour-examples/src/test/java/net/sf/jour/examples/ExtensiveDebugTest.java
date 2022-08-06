package net.sf.jour.examples;

import org.junit.Test;

import static org.junit.Assert.assertNull;

public class ExtensiveDebugTest {

    @Test
	public void testDebugRemoved() {
		ExtensiveDebug e = new ExtensiveDebug("is removed?");
		assertNull("debug function called", e.debugCalled);
	}

}
