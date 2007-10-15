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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.jour.util.FileUtil;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author vlads
 *
 */
public class ExportXML {

	public static final String rootNodeName = "signature";
	
	private Document document; 
	
	private APIFilter filter;
		
	public static void export(String reportFile, List classes, APIFilter filter) {

		DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (FactoryConfigurationError e) {
			throw new RuntimeException(e);
		}
		
		ExportXML instance = new ExportXML();
		instance.filter = filter;
		
		instance.document = builder.newDocument();
		
		Element root = instance.document.createElement(rootNodeName);
		instance.document.appendChild(root);
		
		for (Iterator iterator = classes.iterator(); iterator.hasNext();) {
			CtClass klass = (CtClass) iterator.next();
			if (filter.isAPIClass(klass)) {
				root.appendChild(instance.classNode(klass));
			}
		}
		instance.serializeXML(reportFile);
	}
	
	private void serializeXML(String reportFile) {
		OutputStream out = null;
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();

			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			
			DOMSource source = new DOMSource(document);
			
			out = new FileOutputStream(reportFile);

			StreamResult result = new StreamResult(out);
			transformer.transform(source, result);

		} catch (TransformerException e) {
			throw new RuntimeException(e);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			FileUtil.closeQuietly(out);
		}
	}
	
	private void addAttribute(Node node, final String attrName, final String attrValue) {
		Attr attr = document.createAttribute(attrName);
		attr.setValue(attrValue);
		node.getAttributes().setNamedItem(attr);
	}
	
	private void addModifiers(Node node, int mod) {
		mod = APIFilter.filterModifiers(mod);
		if (mod != 0) {
			addAttribute(node, "modifiers", Modifier.toString(mod));
		}
	}
	
	private Node classNode(CtClass klass) {
		String nodeName = klass.isInterface()?"interface":"class";
		Element element = document.createElement(nodeName);
		addAttribute(element, "name", klass.getName()); 
		
		addModifiers(element, klass.getModifiers());
		
		try {
			buildHierarchy(element, klass);
			buildConstructors(element, klass);
			buildMethods(element, klass);
			buildFields(element, klass);
		} catch (NotFoundException e) {
			throw new RuntimeException(klass.getName(), e);
		}
		
		return element;
	}

	private void buildHierarchy(Element element, CtClass klass) throws NotFoundException {
			CtClass superClass = klass.getSuperclass();
			if ((superClass != null) && (!superClass.getName().equals(Object.class.getName()))) {
				addAttribute(element, "extends", superClass.getName());
			}

			CtClass[] interfaces = klass.getInterfaces();
			if (interfaces.length != 0) {
				
				Element interfacesNode = document.createElement("implements");
				element.appendChild(interfacesNode);
				
				for (int i = 0; i < interfaces.length; i++) {
					Element intElement = document.createElement("interface");
					addAttribute(intElement, "name", interfaces[i].getName());
					interfacesNode.appendChild(intElement);
				}
			}
	}
	
	private void buildExceptions(Element element, CtBehavior method) throws NotFoundException {
		CtClass[] exceptions = method.getExceptionTypes();
		for (int k = 0; k < exceptions.length; k++) {
			Element exceptElement = document.createElement("exception");
			addAttribute(exceptElement, "name", exceptions[k].getName());
			element.appendChild(exceptElement);
		}
	}
	
	private void buildParameterTypes(Element element, CtBehavior method) throws NotFoundException {
		CtClass[] params = method.getParameterTypes();
		for (int k = 0; k < params.length; k++) {
			Element paramsElement = document.createElement("parameter");
			addAttribute(paramsElement, "type", params[k].getName());
			element.appendChild(paramsElement);
		}
	}
	
	private void buildConstructors(Element element, CtClass klass) throws NotFoundException {
		CtConstructor[] constructors = klass.getDeclaredConstructors();
		for (int i = 0; i < constructors.length; i++) {
			
			if (!filter.isAPIMember(constructors[i])) {
				continue;
			}
			
			Element constructorElement = document.createElement("constructor");
			addModifiers(constructorElement, constructors[i].getModifiers());
			buildExceptions(constructorElement, constructors[i]);
			buildParameterTypes(constructorElement, constructors[i]);
			element.appendChild(constructorElement);
		}
	}
	
	private void buildMethods(Element element, CtClass klass) throws NotFoundException {
		CtMethod[] methods = klass.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			
			if (!filter.isAPIMember(methods[i])) {
				continue;
			}
			
			Element methElement = document.createElement("method");
			addAttribute(methElement, "name", methods[i].getName());
			addAttribute(methElement, "return", methods[i].getReturnType().getName());
			addModifiers(methElement, methods[i].getModifiers());
			buildExceptions(methElement, methods[i]);
			buildParameterTypes(methElement, methods[i]);		
			
			element.appendChild(methElement);
		}
	}
	
	private void buildFields(Element element, CtClass klass) throws NotFoundException {
		CtField[] fields = klass.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			
			if (!filter.isAPIMember(fields[i])) {
				continue;
			}
			
			Element fieldElement = document.createElement("field");
			addAttribute(fieldElement, "name", fields[i].getName());
			addAttribute(fieldElement, "type", fields[i].getType().getName());
			addModifiers(fieldElement, fields[i].getModifiers());

			int mod = fields[i].getModifiers();
			
			if ((Modifier.isFinal(mod)) && (Modifier.isStatic(mod)) && APIFilter.isExportableConstantType(fields[i].getType())) {
				Object constValue = fields[i].getConstantValue();
				if (constValue != null) {
					addAttribute(fieldElement, "constant-value", constValue.toString());		
				}
			}
			
			element.appendChild(fieldElement);
		}
	}
			
	
}
