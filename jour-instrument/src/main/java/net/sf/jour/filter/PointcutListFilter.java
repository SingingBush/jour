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

/**
 *
 * Created on 05.12.2004 Contributing Author(s):
 *
 * Misha Lifschitz <mishalifschitz at users.sourceforge.net> (Inital
 * implementation) Vlad Skarzhevskyy <vlads at users.sourceforge.net> (Inital
 * implementation)
 *
 * @author vlads
 * @version $Revision$ ($Author$) $Date: 2006-11-19 16:52:09 -0500
 *          (Sun, 19 Nov 2006) $
 */
public class PointcutListFilter extends MatchListFilter {

	private static final Logger log = LoggerFactory.getLogger(PointcutListFilter.class);

	public boolean readContext(Object o) {
		if (o instanceof net.sf.jour.config.Pointcut) {
			addPointcut((net.sf.jour.config.Pointcut) o);
			return true;
		} else {
			return super.readContext(o);
		}
	}

	public void addPointcut(net.sf.jour.config.Pointcut element) {
		addPointcut(element.getExpr());
	}

	public void addPointcut(String expr) {
		if (expr == null) {
			return;
		}
		String texpr = expr.trim();
		if (texpr.startsWith("!")) {
			texpr = texpr.substring(1);
			addExclude(new Pointcut(texpr));
		} else {
			addInclude(new Pointcut(texpr));
		}
	}

}
