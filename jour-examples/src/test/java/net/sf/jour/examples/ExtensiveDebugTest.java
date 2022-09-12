package net.sf.jour.examples;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExtensiveDebugTest {

    @Test
	public void testDebugRemoved() {
		final ExtensiveDebug e = new ExtensiveDebug("is removed?");

		assertTrue(e.debugMethodBodyRemoved(), "debug function called but the functionality is removed");
	}

    @Test
    public void testReplaceMethodInstrumentor() {
        final ExtensiveDebug ed = new ExtensiveDebug();

        assertTrue(ed.replaceMe(), "Should have been replaced with \"return true;\"");
        assertFalse(ed.replaceMeOriginal(), "The original method should exist with \"Original\" post-fix");

        assertEquals("it works!", ed.replaceThis(), "The result was originally \"original text\"");
        assertEquals("original text", ed.replaceThis$orig());
    }
}
