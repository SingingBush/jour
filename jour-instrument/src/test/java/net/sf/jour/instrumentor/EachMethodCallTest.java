package net.sf.jour.instrumentor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;

import org.junit.Test;

import javassist.ClassPool;
import javassist.CtClass;
import junit.framework.TestCase;
import net.sf.jour.Config;
import net.sf.jour.Interceptor;
import net.sf.jour.test.Utils;
import uut.makecalls.EachMethodCall;

public class EachMethodCallTest extends TestCase {

	@Test
	public void testEachMethodCallList() throws Exception {
		String testClassName = "uut.makecalls.EachMethodCallCase";

		Config config = new Config("/callEachMethod.jour.xml");

		ClassPool pool = Utils.getClassPool(testClassName);

		Instrumentor[] instrumentors = config.getInstrumentors(testClassName);
		Interceptor interceptor = new Interceptor(config, pool, testClassName, instrumentors);
		CtClass cc = interceptor.instrument();

		InstrumentorResults rc = interceptor.getInstrumentorResults();

		assertTrue("Modified", rc.isModified());

		Class caseClass = cc.toClass();

		EachMethodCall call = (EachMethodCall)caseClass.newInstance();
		call.callEachMethod();
		List list = call.getMethodsCalled();
		List expected = new Vector();

		Method[] mts = call.getClass().getDeclaredMethods();
		for (int i = 0; i < mts.length; i++) {
			if (mts[i].getName().startsWith("test")) {
				expected.add(mts[i].getName());
			}
		}

		assertTrue("All test* Methtods Called", expected.containsAll(list));
	}

}
