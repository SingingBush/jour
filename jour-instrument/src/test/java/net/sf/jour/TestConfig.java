package net.sf.jour;

import net.sf.jour.filter.PointcutListFilter;
import net.sf.jour.instrumentor.ConfigTestInstrumentor;
import net.sf.jour.instrumentor.Instrumentor;
import net.sf.jour.test.Utils;
import net.sf.jour.util.FileUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestConfig {

    @Test
	public void testAll() {
		assertNotNull(FileUtil.getFile(Utils.getClassResourceName(this.getClass().getName()), this));
		assertNotNull(FileUtil.getFile("/configTest.jour.xml", this));

		final Config config = new Config(FileUtil.getFile("/configTest.jour.xml", this));
        Instrumentor[] instr = config.getAllInstrumentors();
		assertEquals(2, instr.length, "All Instrumentors");

		instr = config.getInstrumentors("typedef1");
		assertEquals(1, instr.length, "Instrumentors for typedef1");

        final PointcutListFilter pointcuts = ((ConfigTestInstrumentor)instr[0]).getPointcuts();
		assertEquals(2, pointcuts.size());
	}

}
