package net.sf.jour.signature;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class APICompareTest {

    private APICompare apiCompare;

    private CtClass implOne;
    private CtClass implTwo;
    private CtClass implThree;

    @BeforeEach
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

    @Test
    public void testApiCompareStaticCompareOneToThree() {
        assertThrows(ChangeDetectedException.class, () -> {
            APICompare.compare(implOne, implThree);
        });
    }

    @Test
    public void testApiCompareStaticCompareThreeToOne() {
        assertThrows(ChangeDetectedException.class, () -> {
            APICompare.compare(implThree, implOne);
        });
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
