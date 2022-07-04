package net.sf.jour.signature;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class APICompareTest {

    private APICompare apiCompare;

    private CtClass implOne;
    private CtClass implTwo;
    private CtClass implThree;

    @Before
    public void setUp() throws Exception {
        apiCompare = new APICompare();

        final ClassPool classPool = ClassPool.getDefault();
        this.implOne = classPool.get(ImplOne.class.getName());
        this.implTwo = classPool.get(ImplTwo.class.getName());
        this.implThree = classPool.get(ImplThree.class.getName());
    }

    @Test
    public void testApiCompareClassesOneToTwo() throws NotFoundException {
        List<String> results = apiCompare.compareClasses(implOne, implTwo);

        assertTrue(results.isEmpty());

        assertFalse(apiCompare.getChangesChanges().hasNext());
        assertFalse(apiCompare.getChangesMissing().hasNext());
        assertFalse(apiCompare.getChangesExtra().hasNext());
    }

    @Test
    public void testApiCompareClassesOneToThree() throws NotFoundException {
        List<String> results = apiCompare.compareClasses(implOne, implThree);

        assertFalse(results.isEmpty());

        assertFalse(apiCompare.getChangesChanges().hasNext());
        assertFalse(apiCompare.getChangesMissing().hasNext());
        assertTrue(apiCompare.getChangesExtra().hasNext());
    }

    @Test
    public void testApiCompareClassesThreeToOne() throws NotFoundException {
        List<String> results = apiCompare.compareClasses(implThree, implOne);

        assertFalse(results.isEmpty());

        assertFalse(apiCompare.getChangesChanges().hasNext());
        assertTrue(apiCompare.getChangesMissing().hasNext());
        assertFalse(apiCompare.getChangesExtra().hasNext());
    }

    @Test
    public void testApiCompareStaticCompareOneToTwo() throws ChangeDetectedException {
        APICompare.compare(implOne, implTwo);
        assertTrue(true);
    }

    @Test(expected = ChangeDetectedException.class)
    public void testApiCompareStaticCompareOneToThree() throws ChangeDetectedException {
        APICompare.compare(implOne, implThree);
        fail();
    }

    @Test(expected = ChangeDetectedException.class)
    public void testApiCompareStaticCompareThreeToOne() throws ChangeDetectedException {
        APICompare.compare(implThree, implOne);
        fail();
    }





    interface MyTestScenario {
        void standardMethod();
    }

    static class ImplOne implements MyTestScenario {
        @Override
        public void standardMethod() {}
    }

    static class ImplTwo implements MyTestScenario {
        @Override
        public void standardMethod() {}
    }

    static class ImplThree implements MyTestScenario {
        public ImplThree(String arg) {}

        @Override
        public void standardMethod() {}

        public void anotherPublicMethod() {}
    }
}
