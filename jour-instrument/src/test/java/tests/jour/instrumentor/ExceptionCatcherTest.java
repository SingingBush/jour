package tests.jour.instrumentor;

import net.sf.jour.InstrumentingClassLoader;
import tests.jour.test.Utils;
import org.junit.jupiter.api.Test;
import uut.Monitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class ExceptionCatcherTest {

    private static final String TEST_CLASSNAME = "uut.exceptionCatcher.ThrowExceptionsCase";

    @Test
	public void testExceptionCatcher() throws Exception {
        final String[] path = new String[] { Utils.getClassResourcePath(TEST_CLASSNAME) };

        final InstrumentingClassLoader cl = new InstrumentingClassLoader("/exceptionCatcher.jour.xml", path, this.getClass().getClassLoader());

		cl.delegateLoadingOf(Monitor.class.getName());

        final Class<?> caseClass = cl.loadClass(TEST_CLASSNAME);

		final Runnable call = (Runnable)caseClass.getDeclaredConstructor().newInstance();

		Monitor.caught = null;
		Monitor.flag = 0;
		Monitor.count = 0;

		try {
			call.run();
            fail("Error should be thrown by application under tests");
		} catch (IllegalArgumentException e) {
			assertEquals("Monitor disabled", e.getMessage(), "Caught Exception message (no monitor)");
		}
		assertEquals(1, Monitor.count, "Monitor call count");

		Monitor.flag = 1;
		Monitor.caught = null;
		call.run();

		assertEquals(2, Monitor.count, "Monitor call count");

		assertNotNull(Monitor.caught, "Caught Exception");

		assertEquals("thrown by Method1", Monitor.caught.getMessage());
	}

}
