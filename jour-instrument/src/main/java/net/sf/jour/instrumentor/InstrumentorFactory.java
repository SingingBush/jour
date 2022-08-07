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
package net.sf.jour.instrumentor;

import net.sf.jour.InterceptorException;
import net.sf.jour.filter.PointcutListFilter;

/**
 *
 * Contributing Author(s):
 *
 *   Misha Lifschitz <mishalifschitz at users.sourceforge.net> (Inital implementation)
 *   Vlad Skarzhevskyy <vlads at users.sourceforge.net> (Inital implementation)
 *
 * @author michaellif
 * @version $Revision$ ($Author$) $Date$
 */
public class InstrumentorFactory {

	public static Instrumentor createInstrumentor(String instrumentor, PointcutListFilter pointcuts)
			throws InterceptorException {
		Instrumentor instr = null;
		if (instrumentor == null) {
			throw new InterceptorException("Instrumentor is NULL");
		}
		try {
			instr = (Instrumentor) Class.forName(instrumentor).getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new InterceptorException("Failed to instantiate " + instrumentor + "instrumentor.", e);
		}

		instr.setPointcuts(pointcuts);

		return instr;
	}
}
