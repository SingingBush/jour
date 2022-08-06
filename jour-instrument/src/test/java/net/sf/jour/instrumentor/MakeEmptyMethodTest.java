package net.sf.jour.instrumentor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;

import net.sf.jour.InstrumentingClassLoader;
import net.sf.jour.test.Utils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MakeEmptyMethodTest {

    @Test
	public void testMakeEmptyMethod() throws Exception {

		String testClassName = "uut.makeempty.MakeEmptyMethodCase";

		String[] path = new String[] { Utils.getClassResourcePath(testClassName) };

		InstrumentingClassLoader cl = new InstrumentingClassLoader("/makeEmptyMethod.jour.xml", path);

		Class caseClass = cl.loadClass(testClassName);

		Object call = caseClass.newInstance();

		Method method = caseClass.getMethod("getMethodsCalled", null);
		List called = (List) method.invoke(call, null);

		List expected = new Vector();
		expected.add("error");

		assertEquals("All Methtods Called", expected, called);
	}
}
