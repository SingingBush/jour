package net.sf.jour;

import junit.framework.TestCase;
import net.sf.jour.filter.PointcutListFilter;
import net.sf.jour.instrumentor.ConfigTestInstrumentor;
import net.sf.jour.instrumentor.Instrumentor;
import net.sf.jour.test.Utils;
import net.sf.jour.util.FileUtil;

public class TestConfig extends TestCase {

	public void testAll() {
		assertNotNull("this.class", FileUtil.getFile(Utils.getClassResourceName(this.getClass().getName()), this));
		assertNotNull("Config URL", FileUtil.getFile("/configTest.jour.xml", this));
		
		Config config = new Config(FileUtil.getFile("/configTest.jour.xml", this));
		Instrumentor[] instr = config.getAllInstrumentors();
		assertEquals("All Instrumentors", 2, instr.length);
		instr = config.getInstrumentors("typedef1");
		assertEquals("Instrumentors for typedef1", 1, instr.length);
		PointcutListFilter pointcuts = ((ConfigTestInstrumentor)instr[0]).getPointcuts();
		assertEquals("pointcuts", 2, pointcuts.size());
	}
	
}
