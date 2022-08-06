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

import net.sf.jour.log.Logger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * TODO Add docs
 * Created on 04.12.2004
 *
 * Contributing Author(s):
 *
 *   Misha Lifschitz <mishalifschitz at users.sourceforge.net> (Inital implementation)
 *   Vlad Skarzhevskyy <vlads at users.sourceforge.net> (Inital implementation)
 *
 * @author vlads
 * @version $Revision$ ($Author$) $Date$
 */
public class MatchStringFilterTest {

	protected static final Logger log = Logger.getLogger();

	public static void main(String[] args) {
		MatchStringFilterTest b = new MatchStringFilterTest();
		b.testStringListPatterns();
	}

	private void verify(String pattern, String text, boolean expect) {
		log.debug("verify [" + pattern + "] [" + text + "]");
		MatchStringFilter f = new MatchStringFilter(pattern);
		if (log.isDebugEnabled()) {
			f.debug();
		}
		assertEquals(pattern + " " + text, expect, f.match(text));
	}

	private void verifyList(String pattern, String text, boolean expect) {
		log.debug("verifyList [" + pattern + "] [" + text + "]");
		MatchStringListFilter f = new MatchStringListFilter(pattern);
		if (log.isDebugEnabled()) {
			f.debug();
		}
		assertEquals(pattern + " " + text, expect, f.match(text));
	}

    @Test
	public void testStringPatterns() {
		verify(null, null, false);
		verify("", "", false);
		verify(null, "", false);
		verify("", null, false);

		verify("", "bob", false);
		verify("bob", "", false);

		verify("bob", "bob", true);
		verify("bob", "bobY", false);
		verify("bob*", "bobY", true);

		verify("*bar*", "bar", true);
		verify("*bar*", "anybar", true);

		verify("*bar.*", "bar", false);
		verify("*bar.*", "bar.man", true);
		verify("*bar.*", "anybar", false);
		verify("*bar.*", "anybar.man", true);

		verify("bar[]", "bar", false);
		verify("bar[]", "bar[]", true);
		verify("*.bar[]", "any.bar[]", true);

		verify("*.bar.*", "bar", false);
		verify("*.bar.*", "anybar", false);

		verify("foo.bar", "foo.bar", true);
		verify("foo.bar", "any.bar", false);
		verify("foo.bar", "foo.bar2", false);
		verify("foo.bar", "2foo.bar", false);

		verify("bob.*", "bob.noe", true);
		verify("*.noe", "bob.noe", true);
		verify("!bob.*", "john.noe", true);
	}

    @Test
	public void testStringListPatterns() {
		verifyList(null, null, false);
		verifyList("", "", false);
		verifyList(null, "", false);
		verifyList("", null, false);

		verifyList("", "bob", false);
		verifyList(";", "bob", false);

		verifyList("bob.*", "bob.noe", true);
		verifyList("*.noe", "bob.noe", true);
		verifyList("*;!bob.*", "john.noe", true);

		verifyList("foo.*;bar.*", "foo.bar", true);
		verifyList("foo.*;bar.*", "bar.foo", true);
		verifyList("*; !foo.* ; !bar.* ", "bar.foo", false);
		verifyList("!foo.*;bar.*", "foo.bar", false);
		verifyList("!foo.*;bar.*", "bar.foo.bar", true);
		verifyList("*.examples.*; *.tests.*", "bar.tests.bar", true);
		verifyList("*.examples.*; *.tests.*", "bar.test.bar", false);
		verifyList("*.examples.*; *.tests.*", "bar.examples.bar", true);
		verifyList("*.examples.*; *.tests.*", "bar.example.bar", false);
	}

}
