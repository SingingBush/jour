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
package net.sf.jour;

import net.sf.jour.config.Aspect;
import net.sf.jour.config.Jour;
import net.sf.jour.filter.*;
import net.sf.jour.instrumentor.*;
import net.sf.jour.util.*;

import java.net.URL;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * @author michaellif
 */
public class Config {

	protected static final Logger log = Logger.getLogger(Config.class);

	public static final String DEFAULTCONFING_FILE = "jour.xml";

	protected boolean isDebug;

	/**
     * Key - ClassFilter, value - Instrumentor
     */
	private HashMap instrumentors = new HashMap();

	public Config() {
		initialize(FileUtil.getFile(DEFAULTCONFING_FILE));
	}

	public Config(String configFileName) {
		initialize(FileUtil.getFile(configFileName));
	}

	public Config(URL configLocation) {
		initialize(configLocation);
	}
	
	protected void initialize(URL configLocation) {
		Jour config = (Jour) ConfigFileUtil
				.unmarshalConfigFile(configLocation, "/net/sf/jour/config/jour-castor-mapping.xml");
		if (config != null) {
			isDebug = config.isDebug();
			List aspectList = config.getAspect();
			if (aspectList == null) {
				return;
			}
			for (Iterator i = aspectList.iterator(); i.hasNext();) {
				Aspect aspectCfg = (Aspect) i.next();
				if (aspectCfg.isEnabled()) {
					ClassFilter filter = createFilter(aspectCfg.getTypedef());
					PointcutListFilter pointcuts = new PointcutListFilter();
					pointcuts.readConfig(aspectCfg.getPointcut());
					Instrumentor instr = InstrumentorFactory.createInstrumentor(aspectCfg.getType(), pointcuts);
					instrumentors.put(filter, instr);
				}
			}
		}
	}

	protected void checkUniqueAspect() throws ConfigException {
		HashMap map = new HashMap();
		Iterator iter = instrumentors.entrySet().iterator();
		while (iter.hasNext()) {
			String instrumentor = (String) iter.next();
			map.put(instrumentor, "");
		}
		if (instrumentors.entrySet().size() > map.size()) {
			throw new ConfigException("Duplicate aspects in jour.xml are not supported");
		}
	}

	protected ClassFilter createFilter(String typedef) {
		return new ClassFilter(typedef);
	}

	public Instrumentor[] getInstrumentors(String className) throws InterceptorException {
		ArrayList instrList = new ArrayList();
		Iterator filters = instrumentors.keySet().iterator();
		while (filters.hasNext()) {
			ClassFilter filter = (ClassFilter) filters.next();
			if (filter.accept(className)) {
				instrList.add(instrumentors.get(filter));
			}

		}
		return (Instrumentor[]) instrList.toArray(new Instrumentor[0]);
	}

	public Instrumentor[] getAllInstrumentors() {
		ArrayList instrList = new ArrayList();
		Iterator filters = instrumentors.keySet().iterator();
		while (filters.hasNext()) {
			ClassFilter filter = (ClassFilter) filters.next();
			instrList.add(instrumentors.get(filter));

		}
		return (Instrumentor[]) instrList.toArray(new Instrumentor[0]);
	}
}
