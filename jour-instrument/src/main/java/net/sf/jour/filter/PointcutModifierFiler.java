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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.Hashtable;

/**
 * TODO Add docs
 *
 * Created on 07.12.2004 Contributing Author(s):
 *
 * Misha Lifschitz <mishalifschitz at users.sourceforge.net> (Inital
 * implementation) Vlad Skarzhevskyy <vlads at users.sourceforge.net> (Inital
 * implementation)
 *
 * @author vlads
 * @version $Revision$ ($Author$) $Date: 2006-11-19 16:52:09 -0500
 *          (Sun, 19 Nov 2006) $
 */
public class PointcutModifierFiler extends MatchFilter {

	protected static final Logger log = LoggerFactory.getLogger(PointcutModifierFiler.class);

	private static Hashtable<String, Integer> map = buildMap();

	private int modifier;

	public PointcutModifierFiler(String modifier) {
		if (!this.setModifier(modifier)) {
			throw new Error("Wrong modifier " + modifier);
		}
	}

	private static Hashtable<String, Integer> buildMap() {
		map = new Hashtable<>();
		map.put("public", Modifier.PUBLIC);
		map.put("protected", Modifier.PROTECTED);
		map.put("private", Modifier.PRIVATE);
		map.put("abstract", Modifier.ABSTRACT);
		map.put("static", Modifier.STATIC);
		map.put("final", Modifier.FINAL);
		map.put("transient", Modifier.TRANSIENT);
		map.put("volatile", Modifier.VOLATILE);
		map.put("synchronized", Modifier.SYNCHRONIZED);
		map.put("native", Modifier.NATIVE);
		map.put("strictfp", Modifier.STRICT);
		map.put("interface", Modifier.INTERFACE);
		return map;
	}

	public boolean setModifier(String modifier) {
		final Integer i = map.get(modifier.trim());
		if (i == null) {
			log.error("Wrong modifier " + modifier);
			return false;
		}
		this.modifier = i.intValue();
		return true;
	}

	public int matchState(Object obj) {
		if (!(obj instanceof Integer)) {
			return MATCH_NO;
		}

		int mod = ((Integer) obj).intValue();

		if ((mod & this.modifier) != 0) {
			if (debug) {
				log.debug("MATCH " + Modifier.toString(this.modifier) + " for :" + Modifier.toString(mod));
			}
			return MATCH_YES;
		} else {
			if (debug) {
				log.debug("NOTMATCH " + Modifier.toString(this.modifier) + " for :" + Modifier.toString(mod));
			}
			return MATCH_NO;
		}
	}

	public void debug() {
		log.debug(Modifier.toString(this.modifier));
	}

}
