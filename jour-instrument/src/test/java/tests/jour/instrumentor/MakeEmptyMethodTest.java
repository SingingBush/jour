package tests.jour.instrumentor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;

import net.sf.jour.InstrumentingClassLoader;
import tests.jour.test.Utils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MakeEmptyMethodTest {

    private static final String TEST_CLASSNAME = "uut.makeempty.MakeEmptyMethodCase";

    @Test
	public void testMakeEmptyMethod() throws Exception {

        final String[] path = new String[] { Utils.getClassResourcePath(TEST_CLASSNAME) };

        final InstrumentingClassLoader cl = new InstrumentingClassLoader("/makeEmptyMethod.jour.xml", path);

        final Class<?> caseClass = cl.loadClass(TEST_CLASSNAME);

        final Object call = caseClass.getDeclaredConstructor().newInstance();

        final Method method = caseClass.getMethod("getMethodsCalled", null);
        final List<String> called = (List<String>) method.invoke(call, null);

		final List<String> expected = new Vector<>();
		expected.add("error");

		assertEquals(expected, called, "All Methods Called");
	}
}
