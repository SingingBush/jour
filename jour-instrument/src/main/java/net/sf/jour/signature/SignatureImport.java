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
 * 
 * @version $Id$
 * 
 */
package net.sf.jour.signature;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;

import javax.xml.parsers.ParserConfigurationException;

import net.sf.jour.ConfigException;
import net.sf.jour.instrumentor.MakeEmptyMethodInstrumentor;
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
	
	private List classes = new Vector();
	
	public SignatureImport(boolean useSystemClassPath, String supportingJars) {
		classPool = new ClassPool();	
		if (supportingJars != null) {
            try {
                classPool.appendPathList(supportingJars);
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }
		if (useSystemClassPath) {
		    classPool.appendSystemPath();
		}
	}

	public ClassPool getClassPool() {
		return classPool;
	}
	
	public List getClasses() {
		return this.classes;
	}
	
	public void load(String xmlFileName){
		URL location = FileUtil.getFile(xmlFileName);
		try {
			Document xmlDoc = ConfigFileUtil.loadDocument(location);
			Node rootNode = ConfigFileUtil.getFirstElement(xmlDoc, ExportXML.rootNodeName);
			if (rootNode == null) {
				throw new ConfigException("Invalid XML root");
			}
			
			NodeList classNodeList = rootNode.getChildNodes();
			for (int j = 0; j < classNodeList.getLength(); j++) {
	            Node node = classNodeList.item(j);
	            if ("interface".equals(node.getNodeName())) {
	            	this.classes.add(loadInterface(node));
	            } else if ("class".equals(node.getNodeName())) {
	            	this.classes.add(loadClass(node));
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

	private CtClass loadInterface(Node node) {
		CtClass klass = createInterface(node);
		int mod = decodeModifiers(ConfigFileUtil.getNodeAttribute(node, "modifiers"));
		mod |= Modifier.INTERFACE | Modifier.ABSTRACT;
		klass.setModifiers(mod);
		
		loadHierarchy(klass, node);
		loadMethods(klass, node);
		classNames.add(klass.getName());
		return klass;
	}

    private CtClass loadClass(Node node) {
		CtClass klass = createClass(node);
		
		int mod = decodeModifiers(ConfigFileUtil.getNodeAttribute(node, "modifiers"));
		klass.setModifiers(mod);
		
		loadHierarchy(klass, node);
		loadConstructors(klass, node);
		loadMethods(klass, node);
		loadFields(klass, node);
		classNames.add(klass.getName());
		return klass;
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
	
	private void loadHierarchy(CtClass klass, Node node) {
	    Node implementNode = ConfigFileUtil.getChildNode(node, "implements");
	    if (implementNode == null) {
	        return;
	    }
	    Node[] interfaceList = ConfigFileUtil.getChildNodes(implementNode, "interface");
	    for (int i = 0; i < interfaceList.length; i++) {
            Node interfaceNode = interfaceList[i];
            String interfaceName = ConfigFileUtil.getNodeAttribute(interfaceNode, "name");
            klass.addInterface(createInterface(interfaceName));
        }
	}
	
	private CtClass[] getParameters(Node node) {
		Node[] partNodes = ConfigFileUtil.getChildNodes(node, "parameter");
        CtClass[] parameters = new CtClass[partNodes.length];
        for (int j = 0; j < partNodes.length; j++) {
            parameters[j] = createInterface(ConfigFileUtil.getNodeAttribute(partNodes[j], "name"));
        }
        return parameters;
	}

    private void loadConstructors(CtClass klass, Node node) {
        Node[] list = ConfigFileUtil.getChildNodes(node, "constructor");
        for (int i = 0; i < list.length; i++) {
            CtClass[] parameters = getParameters(list[i]);
            CtConstructor c;
			try {
				c = klass.getDeclaredConstructor(parameters);
			} catch (NotFoundException e) {
				c = new CtConstructor(parameters, klass);
				try {
					klass.addConstructor(c);
				} catch (CannotCompileException e2) {
					throw new RuntimeException(klass.getName(), e2);
				}
			}
			loadExceptions(c, list[i]);
			setModifiers(c, list[i]);
        }
    }

    private void setModifiers(CtBehavior m, Node node) {
    	m.setModifiers(decodeModifiers(ConfigFileUtil.getNodeAttribute(node, "modifiers")));
    }
    
    private int decodeModifiers(String modifiers) {
    	if (modifiers == null) {
    		return 0;
    	}
    	int mod = 0;
    	StringTokenizer st = new StringTokenizer(modifiers, " ");
		if (st.hasMoreTokens()) {
			while (st.hasMoreTokens()) {
				mod |= decodeModifier(st.nextToken()); 
			}
		} else {
			mod = decodeModifier(modifiers);
		}
    	return mod;
    }
    
	private int decodeModifier(String modifier) {
		if (modifier.equalsIgnoreCase("public")) {
			return Modifier.PUBLIC;
		} else if (modifier.equalsIgnoreCase("protected")) {
			return Modifier.PROTECTED;
		} else if (modifier.equalsIgnoreCase("private")) {
			return Modifier.PRIVATE;
		} else if (modifier.equalsIgnoreCase("abstract")) {
			return Modifier.ABSTRACT;
		} else if (modifier.equalsIgnoreCase("static")) {
			return Modifier.STATIC;
		} else if (modifier.equalsIgnoreCase("final")) {
			return Modifier.FINAL;
		} else if (modifier.equalsIgnoreCase("volatile")) {
			return Modifier.TRANSIENT;
		} else if (modifier.equalsIgnoreCase("synchronized")) {
			return Modifier.SYNCHRONIZED;
		} else if (modifier.equalsIgnoreCase("native")) {
			return Modifier.NATIVE;
		} else if (modifier.equalsIgnoreCase("interface")) {
			return Modifier.INTERFACE;
		} else {
			throw new RuntimeException("Invalid modifier [" + modifier + "]");
		}
	}

	private void loadExceptions(CtBehavior m, Node node) {
		Node[] list = ConfigFileUtil.getChildNodes(node, "exception");
		if (list.length == 0) {
			return;
		}
		CtClass[] types = new CtClass[list.length];
		for (int i = 0; i < list.length; i++) {
			types[i] = createClass(ConfigFileUtil.getNodeAttribute(list[i], "name"));
		}
		try {
			m.setExceptionTypes(types);
		} catch (NotFoundException e) {
			throw new RuntimeException("Can't add exceptions", e);
		} 
	}
	
	private void loadMethods(CtClass klass, Node node) {
		Node[] list = ConfigFileUtil.getChildNodes(node, "method");
		for (int i = 0; i < list.length; i++) {
			String mname = ConfigFileUtil.getNodeAttribute(list[i], "name");
			CtClass[] parameters = getParameters(list[i]);
			CtClass returnType = createInterface(ConfigFileUtil.getNodeAttribute(list[i], "return"));
			CtMethod method = new CtMethod(returnType, mname, parameters, klass);
			setModifiers(method, list[i]);
			try {
				if (!klass.isInterface()) {
					method.setBody(MakeEmptyMethodInstrumentor.emptyBody(returnType));
				}
				loadExceptions(method, list[i]);
				klass.addMethod(method);
			} catch (CannotCompileException e) {
				throw new RuntimeException(klass.getName(), e);
			}
		}
	}
    
    private void loadFields(CtClass klass, Node node) {
    	Node[] list = ConfigFileUtil.getChildNodes(node, "field");
		for (int i = 0; i < list.length; i++) {
			String fname = ConfigFileUtil.getNodeAttribute(list[i], "name");
			CtClass fieldType = createInterface(ConfigFileUtil.getNodeAttribute(list[i], "type"));
			CtField field;
			try {
				field = new CtField(fieldType, fname, klass);
	            field.setModifiers(decodeModifiers(ConfigFileUtil.getNodeAttribute(list[i], "modifiers")));
				CtField.Initializer initializer = null;
				String constValue = ConfigFileUtil.getNodeAttribute(list[i], "constant-value");
				if (constValue != null) {
					initializer = createFieldInitializer(fieldType, constValue);
				}
				klass.addField(field, initializer);
			} catch (CannotCompileException e) {
				throw new RuntimeException(klass.getName() + " filed " + fname, e);
			}
		}
    }

    private CtField.Initializer createFieldInitializer(CtClass fieldType, String constValue) {
		if (APIFilter.javaLangString.equals(fieldType.getName())) {
			return CtField.Initializer.constant(constValue);
		} else if (fieldType == CtClass.longType) {
			return CtField.Initializer.constant(Long.valueOf(constValue).longValue());
		} else if (fieldType == CtClass.floatType) {
		    throw new RuntimeException("Not implemented");
		} else if (fieldType == CtClass.doubleType) {
			return CtField.Initializer.constant(Double.valueOf(constValue).doubleValue());
		} else if (fieldType == CtClass.booleanType) {
		    throw new RuntimeException("Not implemented");
//			int b = Boolean.valueOf(constValue).booleanValue()?1:0;
//			return CtField.Initializer.constant(b);
		} else {
			return CtField.Initializer.constant(Integer.valueOf(constValue).intValue());
		}
	}
	
}
