package net.sf.jour.instrumentor;

import net.sf.jour.InstrumentingClassLoader;
import net.sf.jour.test.Utils;
import org.junit.Test;
import uut.Monitor;

import static org.junit.Assert.assertEquals;

public class ReplaceMethodInstrumentorTest {

    @Test
	public void testExceptionCatcher() throws Exception {

		String testClassName = "uut.replaceMethod.Case";

		String[] path = new String[] { Utils.getClassResourcePath(testClassName) };

		InstrumentingClassLoader cl = new InstrumentingClassLoader("/replaceMethodInstrumentor.jour.xml", path, this.getClass().getClassLoader());

		cl.delegateLoadingOf(Monitor.class.getName());

		Class caseClass = cl.loadClass(testClassName);

		Runnable call = (Runnable)caseClass.newInstance();

		Monitor.flag = 0;
		Monitor.count = 0;

		call.run();

		assertEquals("Monitor call count", 1, Monitor.count);
		assertEquals("Monitor flag value", 1, Monitor.flag);
	}

}
