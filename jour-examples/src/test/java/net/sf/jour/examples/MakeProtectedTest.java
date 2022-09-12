package net.sf.jour.examples;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Samael Bate (singingbush)
 * created on 12/09/2022
 */
public class MakeProtectedTest {

    @Test
    void testMethodOne() throws NoSuchMethodException {
        final Method method = MakeProtected.class.getDeclaredMethod("methodOne");
        assertTrue(Modifier.isProtected(method.getModifiers()));
    }

    @Test
    void testMethodTwo() throws NoSuchMethodException {
        final Method method = MakeProtected.class.getDeclaredMethod("methodTwo");
        assertTrue(Modifier.isProtected(method.getModifiers()));
    }

    @Test
    void testMethodThree() throws NoSuchMethodException {
        final Method method = MakeProtected.class.getDeclaredMethod("methodThree");
        assertTrue(Modifier.isProtected(method.getModifiers()));
    }
}
