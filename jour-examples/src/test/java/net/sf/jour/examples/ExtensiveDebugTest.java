package net.sf.jour.examples;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

public class ExtensiveDebugTest {

    @Test
	public void testDebugRemoved() {
		final ExtensiveDebug e = new ExtensiveDebug("is removed?");
		assertNull(e.debugCalled, "debug function called");
	}

}
