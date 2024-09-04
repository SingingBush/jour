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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;

import net.sf.jour.config.Aspect;
import net.sf.jour.config.AspectProperty;
import net.sf.jour.config.Jour;
import net.sf.jour.config.Pointcut;
import net.sf.jour.filter.ClassFilter;
import net.sf.jour.filter.PointcutListFilter;
import net.sf.jour.instrumentor.Instrumentor;
import net.sf.jour.instrumentor.InstrumentorFactory;
import net.sf.jour.util.ConfigFileUtil;
import net.sf.jour.util.FileUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author michaellif
 */
public class Config {

	private static final Logger log = LoggerFactory.getLogger(Config.class);

	public static final String DEFAULTCONFING_FILE = "jour.xml";

	protected boolean isDebug;

	private boolean isSetSerialVersionUID;

	/**
	 * Key - ClassFilter, value - Instrumentor
	 */
	private final HashMap<ClassFilter, Instrumentor> instrumentors = new HashMap<>();

	public Config() {
		initialize(FileUtil.getFile(DEFAULTCONFING_FILE));
	}

	public Config(String configFileName) {
		if (configFileName == null) {
			configFileName = DEFAULTCONFING_FILE;
		}
		initialize(FileUtil.getFile(configFileName));
	}

    /**
     * @param configLocation A valid URI to the location of a jour config xml file
     * @since 2.1.1
     */
    public Config(final URI configLocation) {
        initialize(configLocation);
    }

    /**
     * @param configLocation A valid URL to the location of a jour config xml file
     */
	public Config(final URL configLocation) {
		initialize(configLocation);
	}

    protected void initialize(URI configLocation) {
        try {
            initialize(configLocation.toURL());
        } catch (MalformedURLException e) {
            log.error(String.format("Invalid URI for jour xml config: %s", configLocation), e);
            throw new RuntimeException(e);
        }
    }

	protected void initialize(URL configLocation) {
		Jour jour = parsJourXML(configLocation);
		if (jour != null) {
			isDebug = jour.isDebug();
			isSetSerialVersionUID = jour.isSetSerialVersionUID();

			final List<Aspect> aspectList = jour.getAspect();

			if (aspectList == null) {
				return;
			}

            for (final Aspect aspectCfg : aspectList) {
                if (aspectCfg.isEnabled()) {
                    final ClassFilter filter = createFilter(aspectCfg.getTypedef());
                    final PointcutListFilter pointcuts = new PointcutListFilter();
                    pointcuts.readConfig(aspectCfg.getPointcut());

                    final Instrumentor instr = InstrumentorFactory.createInstrumentor(aspectCfg.getType(), pointcuts);

                    if (aspectCfg.getProperty() != null) {
                        for (AspectProperty prop : aspectCfg.getProperty()) {
                            setInstrumentorProperty(instr, prop.getName(), prop.getValue());
                        }
                    }
                    instrumentors.put(filter, instr);
                }
            }
		}
	}

	private Jour parsJourXML(URL configLocation) {
		Jour jour = new Jour();
		try {
			final Document xmlDoc = ConfigFileUtil.loadDocument(configLocation);
            final Node jourNode = ConfigFileUtil.getFirstElement(xmlDoc, "jour");
			if (jourNode == null) {
				throw new ConfigException("Invalid XML root");
			}

			jour.setDebug(ConfigFileUtil.getNodeAttribute(jourNode, "debug", false));
			jour.setSetSerialVersionUID(ConfigFileUtil.getNodeAttribute(jourNode, "setSerialVersionUID", false));

            final NodeList aspectList = jourNode.getChildNodes();
			for (int j = 0; j < aspectList.getLength(); j++) {
	            Node aspectNode = aspectList.item(j);
	            if (!"aspect".equals(aspectNode.getNodeName())) {
	            	continue;
	            }

	            Aspect aspect = new Aspect();

	            aspect.setDescr(ConfigFileUtil.getNodeAttribute(aspectNode, "descr"));
	            aspect.setType(ConfigFileUtil.getNodeAttribute(aspectNode, "type"));
	            aspect.setEnabled(ConfigFileUtil.getNodeAttribute(aspectNode, "enabled", true));
	            aspect.setTypedef(ConfigFileUtil.getNodeValue(aspectNode, "typedef"));

	            NodeList aspectChildList = aspectNode.getChildNodes();
	            for (int k = 0; k < aspectChildList.getLength(); k++) {
		            Node aspectChildNode = aspectChildList.item(k);
		            String nodeName = aspectChildNode.getNodeName();

		            if ("pointcut".equals(nodeName)) {
		            	aspect.addPointcut(parsPointcut(aspectChildNode));
		            } else if ("property".equals(nodeName)) {
		            	aspect.addProperty(parsProperty(aspectChildNode));
		            }

	            }
	            jour.addAspect(aspect);
			}
		} catch (ParserConfigurationException | IOException | SAXException e) {
			throw new ConfigException("Error parsing XML", e);
		}
        return jour;
	}

	private AspectProperty parsProperty(Node node) {
		AspectProperty p = new AspectProperty();
		p.setName(ConfigFileUtil.getNodeAttribute(node, "name"));

		String value = ConfigFileUtil.getNodeValue(node, "value");
		if (value == null) {
			value = ConfigFileUtil.getNodeAttribute(node, "value");
		}
		p.setValue(value);
		return p;
	}

	private Pointcut parsPointcut(Node node) {
		Pointcut p = new Pointcut();
		String expr = ConfigFileUtil.getNodeAttribute(node, "expr");
		if (expr == null) {
			expr = node.getNodeValue();
		}
		p.setExpr(expr);
		return p;
	}

	void setInstrumentorProperty(Instrumentor instrumentor, String name, String value) {
		try {
			Method method = instrumentor.getClass().getMethod(name, new Class[] {String.class});
			method.invoke(instrumentor, new Object[] {value});
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new Error("Can't set property " + name, e);
		}
    }

	public boolean isSetSerialVersionUID() {
		return isSetSerialVersionUID;
	}

//	protected void checkUniqueAspect() throws ConfigException {
//		final HashMap<Instrumentor, String> map = new HashMap<>();
//        final Iterator<Map.Entry<ClassFilter, Instrumentor>> iterator = instrumentors.entrySet().iterator();
//
//		while (iterator.hasNext()) {
//			String instrumentor = (String) iterator.next();
//			map.put(instrumentor, "");
//		}
//		if (instrumentors.entrySet().size() > map.size()) {
//			throw new ConfigException("Duplicate aspects in jour.xml are not supported");
//		}
//	}

	protected ClassFilter createFilter(String typedef) {
		return new ClassFilter(typedef);
	}

	public Instrumentor[] getInstrumentors(final String className) throws InterceptorException {
		final List<Instrumentor> instrList = new ArrayList<>();
		final Iterator<ClassFilter> filters = instrumentors.keySet().iterator();

		while (filters.hasNext()) {
			final ClassFilter filter = filters.next();
			if (filter.accept(className)) {
				instrList.add(instrumentors.get(filter));
			}
		}
		return instrList.toArray(new Instrumentor[0]);
	}

	public Instrumentor[] getAllInstrumentors() {
        final List<Instrumentor> instrList = new ArrayList<>();
        final Iterator<ClassFilter> filters = instrumentors.keySet().iterator();

		while (filters.hasNext()) {
			final ClassFilter filter = filters.next();
			instrList.add(instrumentors.get(filter));
		}
		return instrList.toArray(new Instrumentor[0]);
	}
}
