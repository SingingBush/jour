package net.sf.jour.examples;

import junit.framework.TestCase;
import org.junit.Test;

public class ExtensiveDebugTest extends TestCase {

    @Test
	public void testDebugRemoved() {
		ExtensiveDebug e = new ExtensiveDebug("is removed?");
		assertNull("debug function called", e.debugCalled);
	}

}
