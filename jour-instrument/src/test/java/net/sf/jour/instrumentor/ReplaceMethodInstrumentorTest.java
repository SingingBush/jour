package net.sf.jour.instrumentor;

import net.sf.jour.InstrumentingClassLoader;
import net.sf.jour.test.Utils;
import org.junit.Test;
import uut.Monitor;

import static org.junit.Assert.assertEquals;

public class ReplaceMethodInstrumentorTest {

    private static final String TEST_CLASSNAME = "uut.replaceMethod.Case";

    @Test
	public void testExceptionCatcher() throws Exception {
        final String[] path = new String[] { Utils.getClassResourcePath(TEST_CLASSNAME) };

        final InstrumentingClassLoader cl = new InstrumentingClassLoader("/replaceMethodInstrumentor.jour.xml", path, this.getClass().getClassLoader());

		cl.delegateLoadingOf(Monitor.class.getName());

        final Class<?> caseClass = cl.loadClass(TEST_CLASSNAME);

        final Runnable call = (Runnable)caseClass.getDeclaredConstructor().newInstance();

		Monitor.flag = 0;
		Monitor.count = 0;

		call.run();

		assertEquals("Monitor call count", 1, Monitor.count);
		assertEquals("Monitor flag value", 1, Monitor.flag);
	}

}
