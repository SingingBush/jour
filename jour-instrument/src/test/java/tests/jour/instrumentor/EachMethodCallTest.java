package tests.jour.instrumentor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;

import org.junit.jupiter.api.Test;

import javassist.ClassPool;
import javassist.CtClass;
import net.sf.jour.Config;
import net.sf.jour.Interceptor;
import net.sf.jour.instrumentor.Instrumentor;
import net.sf.jour.instrumentor.InstrumentorResults;
import tests.jour.test.Utils;
import uut.makecalls.EachMethodCall;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EachMethodCallTest {

    private static final String TEST_CLASSNAME = "uut.makecalls.EachMethodCallCase";

	@Test
	public void testEachMethodCallList() throws Exception {
        final Config config = new Config("/callEachMethod.jour.xml");

        final ClassPool pool = Utils.getClassPool(TEST_CLASSNAME);

        final Instrumentor[] instrumentors = config.getInstrumentors(TEST_CLASSNAME);
        final Interceptor interceptor = new Interceptor(config, pool, TEST_CLASSNAME, instrumentors);
        final CtClass cc = interceptor.instrument();

        final InstrumentorResults rc = interceptor.getInstrumentorResults();

		assertTrue(rc.isModified());

        final Class<?> caseClass = cc.toClass();

        final EachMethodCall call = (EachMethodCall)caseClass.getDeclaredConstructor().newInstance();
		call.callEachMethod();
        final List list = call.getMethodsCalled();
        final List<String> expected = new Vector<>();

        final Method[] mts = call.getClass().getDeclaredMethods();

		for (int i = 0; i < mts.length; i++) {
			if (mts[i].getName().startsWith("test")) {
				expected.add(mts[i].getName());
			}
		}

		assertTrue(expected.containsAll(list), "All test* Methtods Called");
	}

}
