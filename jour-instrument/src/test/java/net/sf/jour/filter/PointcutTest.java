/*
 * Jour - java profiler and monitoring library
 *
 * Copyright (C) 2004 Jour team
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 */
package net.sf.jour.filter;

import java.lang.reflect.Modifier;
import java.util.StringTokenizer;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import net.sf.jour.test.Utils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created on 03.12.2004
 *
 * Contributing Author(s):
 *
 *   Misha Lifschitz <mishalifschitz at users.sourceforge.net> (Inital implementation)
 *   Vlad Skarzhevskyy <vlads at users.sourceforge.net> (Inital implementation)
 *
 * @author vlads
 * @version $Revision$ ($Author$) $Date$
 */
public class PointcutTest {

	public void verifyMod(String patterns, int mod, boolean expect) {
	    PointcutModifierListFiler pm = new PointcutModifierListFiler();
	    pm.addPatterns(patterns);
	    if (MatchFilter.debug) {
	        pm.debug();
	    }
	    assertEquals(expect, pm.match(mod), patterns + " for:" + Modifier.toString(mod));
	}

    @Test
	public void testPointcutModifierFilter() {
	    verifyMod("", Modifier.SYNCHRONIZED, true);
	    verifyMod("public", Modifier.PUBLIC, true);
	    verifyMod("public;", Modifier.PUBLIC, true);
	    verifyMod("public;", Modifier.PRIVATE, false);
	    verifyMod("public;private", Modifier.PUBLIC, true);
	    verifyMod("public;private", Modifier.PRIVATE, true);
	    verifyMod("public;private", Modifier.PRIVATE | Modifier.STATIC, true);
	    verifyMod("public;private", Modifier.SYNCHRONIZED | Modifier.STATIC, false);

	    verifyMod("public;!private", Modifier.PRIVATE, false);
	    verifyMod("!public;private", Modifier.PRIVATE, true);
	    verifyMod("private", Modifier.PRIVATE | Modifier.STATIC, true);
	    verifyMod("private;static", Modifier.PRIVATE | Modifier.STATIC, true);

	    verifyMod("public,synchronized", Modifier.PUBLIC, false);
	    verifyMod("public,synchronized", Modifier.SYNCHRONIZED, false);
	    verifyMod("public,synchronized", Modifier.PUBLIC | Modifier.SYNCHRONIZED, true);
	    verifyMod("public,synchronized", Modifier.PUBLIC | Modifier.SYNCHRONIZED | Modifier.STATIC, true);
	    verifyMod("synchronized,!private", Modifier.SYNCHRONIZED | Modifier.PRIVATE, false);

	    verifyMod("public,synchronized;!static", Modifier.PUBLIC | Modifier.SYNCHRONIZED, true);
	    verifyMod("public,synchronized;!static", Modifier.PUBLIC, false);
	    verifyMod("public,synchronized;!static", Modifier.PUBLIC | Modifier.SYNCHRONIZED | Modifier.STATIC, false);

	    verifyMod("public,!final;static", Modifier.PUBLIC, true);
	    verifyMod("public,!final;static", Modifier.STATIC, true);
	    verifyMod("public,!final;static", Modifier.STATIC | Modifier.FINAL, true);

	    verifyMod("public,static;final,synchronized", Modifier.PUBLIC | Modifier.FINAL, false);
	    verifyMod("public,static;final,synchronized", Modifier.PUBLIC | Modifier.STATIC, true);
	    verifyMod("public,static;final,synchronized", Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL, true);
	    verifyMod("public,static;final,synchronized", Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL | Modifier.SYNCHRONIZED , true);
	    verifyMod("public,static;final,synchronized", Modifier.PUBLIC | Modifier.SYNCHRONIZED , false);

	    verifyMod("public,static;!final,synchronized", Modifier.PUBLIC | Modifier.FINAL, false);
	    verifyMod("public,static;!final,synchronized", Modifier.PUBLIC | Modifier.STATIC, true);
	    verifyMod("public,static;!final,synchronized", Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL, true);
	    verifyMod("public,static;!final,synchronized", Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL | Modifier.SYNCHRONIZED, true);
	    verifyMod("public,static;synchronized,!final", Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL | Modifier.SYNCHRONIZED, true);
	    verifyMod("public,static;!final,synchronized", Modifier.PUBLIC | Modifier.FINAL | Modifier.SYNCHRONIZED, false);
	    verifyMod("public,static;!final,synchronized", Modifier.FINAL | Modifier.SYNCHRONIZED, false);
	    verifyMod("public,static;!final,synchronized", Modifier.SYNCHRONIZED, true);
	}

	private void verify(String pattern, String methodName, boolean expect) {
		Pointcut pc = new Pointcut(pattern);
		assertEquals(expect, pc.acceptMethod(methodName, new String[0], "int"), pattern + " for:" + methodName);
	}

    @Test
	public void testPointcut() {
		Pointcut pc = new Pointcut("* *(..)");
		assertEquals("*", pc.getMethodName());
		assertEquals("*", pc.getRetType());
		assertTrue(pc.acceptMethod("bob", new String[0], "int"));

		verify("* *(..)", "bob", true);
		verify("* !*(..)", "bob", false);

		verify("* bob*(..)", "bobY", true);

		verify("* bob(..)", "bob", true);

		verify("* !bob*(..)", "bobY", false);

		verify("* foo*(..)", "bob", false);

	}

	private void verifyList(String patterns, String methodName, boolean expect) throws Exception {
	    PointcutListFilter pointcuts = new PointcutListFilter();

	    StringTokenizer tokenizer = new StringTokenizer(patterns, "|");
		while (tokenizer.hasMoreTokens()) {
			String str = tokenizer.nextToken().trim();
			pointcuts.addPointcut(str);
		}
	    ClassPool pool = Utils.getClassPool("uut.pointcut.PointcutCase");
	    CtClass clazz = pool.get("uut.pointcut.PointcutCase");
	    CtMethod method = clazz.getDeclaredMethod(methodName);

	    assertEquals(expect, pointcuts.match(method), patterns + " for:" + methodName);
	}

    @Test
	public void testPointcutList() throws Exception {
	    verifyList("* *(..)", "getFoo", true);
	    verifyList("* *(..)|!* set*(..)", "getFoo", true);
	    verifyList("* *(..)|!* get*(..)", "getFoo", false);
	    verifyList("* *(..)|!* set*(..)", "doFoo", true);
	}

	public void xtestPointcutRetType() throws Exception {
	    // Test retType filter
	    verifyList("int *(..)", "getint", true);
	    verifyList("void *(..)", "setint", true);
	    verifyList("java.lang.String *(..)", "getint", false);
	    verifyList("int *(..)", "getString", false);
	    verifyList("java.lang.String *(..)", "getString", true);
	    verifyList("java.lang.* *(..)", "getString", true);
	    verifyList("java.lang.*;int *(..)", "getString", true);
	    verifyList("java.lang.*;int *(..)", "getint", true);
	    verifyList("java.lang.*;int *(..)", "getBar", false);
	    verifyList("java.lang.*;int get*(..)", "getBar", false);
	    verifyList("java.util.*;int get*(..)", "getBar", true);
	    verifyList("java.lang.String getStringArray()", "getStringArray", false);
	    verifyList("java.lang.String[] getStringArray()", "getStringArray", true);
	}

    @Test
	public void testPointcutParamType() throws Exception {
	    verifyList("* *(int)", "getint", false);
	    verifyList("* *()", "getint", true);
	    verifyList("* *(int)", "getFooBar", true);

	    // public int doFoo(String foo, List bar)
	    verifyList("* *(java.lang.String,..)", "doFoo", true);
	    verifyList("* *(java.lang.String,*)", "doFoo", true);
	    verifyList("* *(java.lang.String,int)", "doFoo", false);
	    verifyList("* *(java.lang.String,java.util.List)", "doFoo", true);
	    verifyList("* *(java.*,java.*)", "doFoo", true);
	    verifyList("* * ( java.* , java.* ) ", "doFoo", true);
	    verifyList("* * ( .. ) ", "doFoo", true);
	}

    @Test
	public void testPointcutModifier() throws Exception {
	    verifyList("public;!static;!final * *()", "getint", true);
	    verifyList("!public;!static;!final * *()", "getint", false);

	    verifyList("private * *()", "getintprivate", true);
	    verifyList("!private * *()", "getintprivate", false);

	    verifyList("!static,!final * *()", "getintprivate", false);
	    verifyList("synchronized,private * *()", "getintprivatesyn", true);
	    verifyList("synchronized,!private * *()", "getintprivatesyn", false);
	    verifyList("synchronized,static,private * *()", "getintprivatesynstat", true);
	    verifyList("synchronized;static;private * *()", "getintprivatesynstat", true);
	    verifyList("synchronized;!static;private * *()", "getintprivatesynstat", false);

	    verifyList("private * *(..)", "setint", false);
	}

    @Test
	public void testInterfacesImplements() throws Exception {
	    verifyList("* uut.pointcut.PointcutCaseInterface->*(..)", "getFooBar", true);
	    verifyList("* uut.pointcut.PointcutCaseInterface->get*(..)", "getFooBar", true);
	    verifyList("* uut.pointcut.PointcutCaseInterface->*(..)", "getFoo", false);
	    verifyList("* uut.pointcut.PointcutCaseInterface->get*(..)", "getFoo", false);
	}

    @Test
	public void testClassAsFilter() throws Exception {
	    verifyList("* uut.pointcut.PointcutCaseClassAsFilter=>*(..)", "getBar", true);
	    verifyList("* uut.pointcut.PointcutCaseClassAsFilter=>get*(..)", "getBarList", false);
	    verifyList("* uut.pointcut.PointcutCaseClassAsFilter=>*(..)", "getFoo", false);
	    verifyList("* uut.pointcut.PointcutCaseClassAsFilter=>get*(..)", "getFoo", false);
	}

}
