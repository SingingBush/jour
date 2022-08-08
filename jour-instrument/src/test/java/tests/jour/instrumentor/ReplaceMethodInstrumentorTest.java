package tests.jour.instrumentor;

import net.sf.jour.InstrumentingClassLoader;
import tests.jour.test.Utils;
import org.junit.jupiter.api.Test;
import uut.Monitor;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

		assertEquals(1, Monitor.count, "Monitor call count");
		assertEquals(1, Monitor.flag, "Monitor flag value");
	}

}
