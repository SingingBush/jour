package net.sf.jour.instrumentor;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.sf.jour.InstrumentingClassLoader;
import net.sf.jour.test.Utils;
import uut.Monitor;

public class ExceptionCatcherTest extends TestCase {

	public void testExceptionCatcher() throws Exception {

		String testClassName = "uut.exceptionCatcher.ThrowExceptionsCase";

		String[] path = new String[] { Utils.getClassResourcePath(testClassName) };

		InstrumentingClassLoader cl = new InstrumentingClassLoader("/exceptionCatcher.jour.xml", path, this.getClass().getClassLoader());

		cl.delegateLoadingOf(Monitor.class.getName());
		
		Class caseClass = cl.loadClass(testClassName);

		Runnable call = (Runnable)caseClass.newInstance();

		Monitor.caught = null;
		Monitor.flag = 0;
		Monitor.count = 0;

		try {
			call.run();
			Assert.fail("Error should be thrown by application under tests");
		} catch (IllegalArgumentException e) {
			assertEquals("Caught Exception message (no monitor)", "Monitor disabled", e.getMessage());
		}
		assertEquals("Monitor call count", 1, Monitor.count);
		
		Monitor.flag = 1;
		Monitor.caught = null;
		call.run();
		
		assertEquals("Monitor call count", 2, Monitor.count);
		
		assertNotNull("Caught Exception", Monitor.caught); 
		
		assertEquals("Caught Exception message", "thrown by Method1", Monitor.caught.getMessage());
	}
	
}
