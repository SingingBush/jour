/*
 * Jour - bytecode instrumentation library
 *
 * Copyright (C) 2007 Vlad Skarzhevskyy
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
package net.sf.jour.signature;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import javax.xml.parsers.ParserConfigurationException;

import net.sf.jour.ConfigException;
import net.sf.jour.util.ConfigFileUtil;
import net.sf.jour.util.FileUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author vlads
 *
 */
public class SignatureImport {

	private ClassPool classPool;

	private List classNames = new Vector();
	
	public SignatureImport() {
		classPool = new ClassPool();	
	}

	public ClassPool getClassPool() {
		return classPool;
	}
	
	public void load(String xmlFileName){
		URL location = FileUtil.getFile(xmlFileName);
		try {
			Document xmlDoc = ConfigFileUtil.loadDocument(location);
			Node rootNode = ConfigFileUtil.getFirstElement(xmlDoc, XMLExport.rootNodeName);
			if (rootNode == null) {
				throw new ConfigException("Invalid XML root");
			}
			
			NodeList classNodeList = rootNode.getChildNodes();
			for (int j = 0; j < classNodeList.getLength(); j++) {
	            Node node = classNodeList.item(j);
	            if ("interface".equals(node.getNodeName())) {
	            	loadInterface(node);
	            } else if ("class".equals(node.getNodeName())) {
	            	loadClass(node);
	            } else if (node.hasChildNodes()) {
	            	throw new ConfigException("Invalid XML node " + node.getNodeName());	
	            }
			}
		} catch (ParserConfigurationException e) {
			throw new ConfigException("Error parsing XML", e);
		} catch (SAXException e) {
			throw new ConfigException("Error parsing XML", e);
		} catch (IOException e) {
			throw new ConfigException("Error parsing XML", e);
		}

	}

	private void loadInterface(Node node) {
		CtClass klass = createInterface(node);
		
		classNames.add(klass.getName());
	}

	private void loadClass(Node node) {
		CtClass klass = createClass(node);
		
		classNames.add(klass.getName());
	}
	
	private CtClass createClass(Node node) {
		String classname = ConfigFileUtil.getNodeAttribute(node, "name");
		String superclassName = ConfigFileUtil.getNodeAttribute(node, "extends");
		
		CtClass klass;
		try {
			klass = classPool.get(classname);
		} catch (NotFoundException e) {
			klass = classPool.makeClass(classname, createClass(superclassName));
		}
		return klass;
	}

	private CtClass createClass(String classname) {
		if (classname == null) {
			return null;
		}
		CtClass klass;
		try {
			klass = classPool.get(classname);
		} catch (NotFoundException e) {
			klass = classPool.makeClass(classname);
		}
		return klass;
	}
	

	private CtClass createInterface(Node node) {
		String classname = ConfigFileUtil.getNodeAttribute(node, "name");
		String superclassName = ConfigFileUtil.getNodeAttribute(node, "extends");
		
		CtClass klass;
		try {
			klass = classPool.get(classname);
		} catch (NotFoundException e) {
			klass = classPool.makeInterface(classname, createInterface(superclassName));
		}
		return klass;
	}

	private CtClass createInterface(String classname) {
		if (classname == null) {
			return null;
		}
		CtClass klass;
		try {
			klass = classPool.get(classname);
		} catch (NotFoundException e) {
			klass = classPool.makeInterface(classname);
		}
		return klass;
	}

	public List getClassNames() {
		return this.classNames;
	}
	
}
