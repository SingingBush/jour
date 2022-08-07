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
package net.sf.jour.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * TODO Add docs
 * Need this Class so runtime would not depend on JAXB.
 *
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

public class ConfigFileUtil extends FileUtil {

	private static final Logger log = LoggerFactory.getLogger(ConfigFileUtil.class);

	public static Document loadDocument(InputStream stream) throws ParserConfigurationException, SAXException, IOException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setValidating(false);

        final DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(null);
        return builder.parse(stream);
	}

    public static Document loadDocument(URL location) throws ParserConfigurationException, SAXException, IOException {
    	InputStream configStream = null;
    	try {
    		try {
    			configStream = location.openStream();
            } catch (IOException e) {
            	throw new Error("resource not found " + location);
    		}
    		return loadDocument(configStream);
		} finally {
			FileUtil.closeQuietly(configStream);
        }
    }

    public static InputStream loadFile(String fileName) {
        final URL location = FileUtil.getFile(fileName);

        if (location != null) {
            try {
                log.info("Using config file " + location);
                return location.openStream();
            } catch (Exception e) {
                log.error("Error reading " + fileName, e);
                return null;
            }
        } else {
            log.error("Config file not found: " + fileName);
            return null;
        }
    }

    public static Node getFirstElement(Document doc, String tagname) {
        final NodeList list = doc.getElementsByTagName(tagname);
        if (list.getLength() == 0) {
            return null;
        }
        return list.item(0);
    }

    public static Node getChildNode(Node node, String tagName) {
        if (node == null) {
            return null;
        }
        final NodeList children =  node.getChildNodes();
        for (int j = 0, cnt = children.getLength(); j < cnt; j++) {
            final Node child = children.item(j);
            if (child != null) {
                final String nodeName = child.getNodeName();
                if (nodeName != null && nodeName.equals(tagName)) {
                    return child;
                }
            }
        }
        return null;
    }

    public static Node[] getChildNodes(Node node, String tagName) {
        if (node == null) {
            return null;
        }
        final List<Node> ret = new Vector<>();
        final NodeList children =  node.getChildNodes();

        for (int j = 0, cnt = children.getLength(); j < cnt; j++) {
            final Node child = children.item(j);
            if (child != null) {
                final String nodeName = child.getNodeName();
                if (nodeName != null && nodeName.equals(tagName)) {
                    ret.add(child);
                }
            }
        }
        return (Node[])ret.toArray(new Node[ret.size()]);
    }

    public static String getNodeValue(Node node, String tagName) {
        if (node == null) {
            return null;
        }
        final NodeList children =  node.getChildNodes();
        for (int j = 0, cnt = children.getLength(); j < cnt; j++) {
            final Node child = children.item(j);
            if (child != null) {
                final String nodeName = child.getNodeName();
                if (nodeName != null && nodeName.equals(tagName)) {
                    final Node firstChild = child.getFirstChild();
                    if (firstChild != null) {
                        final String nodeValue = firstChild.getNodeValue();
                        if (nodeValue != null) {
                            return nodeValue;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static String getNodeAttribute(Node node, String tagName) {
        final NamedNodeMap nodeAttrs = node.getAttributes();
    	if (nodeAttrs == null) {
    		return null;
    	}
        final Node value = nodeAttrs.getNamedItem(tagName);
    	if (value != null) {
    		return value.getNodeValue();
    	} else {
    		return null;
    	}
    }

    public static boolean getNodeAttribute(Node node, String tagName, boolean defaultValue) {
    	String value = getNodeAttribute(node, tagName);
    	if (value == null) {
    		return defaultValue;
    	}
    	return Boolean.valueOf(value).booleanValue();
    }
}
