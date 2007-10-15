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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMember;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;

/**
 * @author vlads
 *
 */
public class APICompare extends APICompareChangeHelper {

    private APIFilter filter;
    
    private APICompareConfig config;
    
    Map fieldInitializerHack; 
    
    public APICompare () {
        filter = new APIFilter(APIFilter.PROTECTED);
        config = new APICompareConfig();
    }
    
    public static void compare(String classpath, String signatureFileName, APICompareConfig config, boolean useSystemClassPath, String supportingJars) throws ChangeDetectedException {
        List changes = listChanges(classpath, signatureFileName, config, useSystemClassPath, supportingJars);
        if (changes.size() > 0) {
            throw new ChangeDetectedException(changes);
        }
    }
    
    public static List listChanges(String classpath, String signatureFileName, APICompareConfig config, boolean useSystemClassPath, String supportingJars) {
        SignatureImport im = new SignatureImport(useSystemClassPath, supportingJars);
        im.load(signatureFileName);
        
        ClassPool classPool = new ClassPool();
        try {
            classPool.appendPathList(classpath);
            if (supportingJars != null) {
                classPool.appendPathList(supportingJars);
            }
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
        if (useSystemClassPath) {
            classPool.appendSystemPath();
        }
        
        List classes = im.getClasses();
        
        //ExportClasses.export("target/test-api-classes", classes);
        
        APICompare cmp = new APICompare();
        cmp.fieldInitializerHack = im.fieldInitializerHack;
        if (config != null) {
            cmp.config = config;
        }
        if (!cmp.config.allowPackageAPIextension) {
            cmp.filter = new APIFilter(APIFilter.PACKAGE);
        }
        
        for (Iterator iterator = classes.iterator(); iterator.hasNext();) {
            CtClass refClass = (CtClass) iterator.next();
            if (!cmp.filter.isAPIClass(refClass)) {
                continue;
            }
            CtClass implClass = null;
            try {
                implClass = classPool.get(refClass.getName());
            } catch (NotFoundException e) {
                cmp.fail(refClass.getName() + " is missing");
            }
            if (implClass != null) {
                try {
                    cmp.compareClasses(refClass, implClass);
                } catch (NotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return cmp.changes;
    }
    
    public static void compare(CtClass refClass, CtClass implClass) throws ChangeDetectedException {
        APICompare cmp = new APICompare();
        List diff;
        try {
            diff = cmp.compareClasses(refClass, implClass);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
        if (diff.size() > 0) {
            throw new ChangeDetectedException(diff);
        }
    }
    
    private String className(CtClass klass) {
        if (klass == null) {
            return null;
        } else {
            return klass.getName();
        }
    }
    
    public List compareClasses(CtClass refClass, CtClass implClass) throws NotFoundException {

        String className = refClass.getName();

        assertEquals(className + " isInterface", refClass.isInterface(), implClass.isInterface());
        assertEquals(className + " getModifiers", refClass.getModifiers(), implClass.getModifiers());

        CtClass[] refInterfaces = refClass.getInterfaces();
        CtClass[] implInterfaces = implClass.getInterfaces();

        assertEquals(className + " interfaces implemented", refInterfaces.length, implInterfaces.length);
        compareInterfaces(refInterfaces, implInterfaces, className);

        if (implClass.getSuperclass() == null) {
            // java.lang.Object in CLDC / javassist will reference same class...
            if (refClass.getSuperclass() != null) {
                assertEquals(className + " Superclass ", "java.lang.Object", refClass.getSuperclass().getName());
            }
        } else if (refClass.getSuperclass() != null) {
            assertEquals(className + " Superclass", refClass.getSuperclass().getName(), implClass.getSuperclass().getName());
        } else {
            assertNull(className + " Superclass " + className(implClass.getSuperclass()), implClass.getSuperclass());
        }

        // Constructors
        CtConstructor[] refConstructors = refClass.getDeclaredConstructors();
        CtConstructor[] implConstructors = implClass.getDeclaredConstructors();
        compareConstructors(refConstructors, implConstructors, className);

        // Methods
        CtMethod[] refMethods = refClass.getDeclaredMethods();
        CtMethod[] implMethods = implClass.getDeclaredMethods();
        compareMethods(refMethods, implMethods, className);

        // all accessible public fields
        CtField[] refFields = refClass.getDeclaredFields();
        CtField[] implFields = implClass.getDeclaredFields();
        compareFields(refFields, implFields, className, refClass, implClass);

        return changes;
    }
    
    private boolean isAPIMember(CtMember member) {
        return filter.isAPIMember(member);
    }
    
    private void compareInterfaces(CtClass[] refInterfaces, CtClass[] implInterfacess, String className) {
        List implNames = new Vector();
        for (int i = 0; i < implInterfacess.length; i++) {
            implNames.add(implInterfacess[i].getName());
        }
        for (int i = 0; i < refInterfaces.length; i++) {
            String interfaceName = refInterfaces[i].getName();
            assertTrue(className + " should implement interface " + interfaceName, implNames.contains(interfaceName));
        }
    }
    
    
    private Map buildNameMap(CtMember[] members, String className) throws NotFoundException {
        Map namesMap = new Hashtable();
        for (int i = 0; i < members.length; i++) {
            if (!isAPIMember(members[i])) {
                //System.out.println("ignore " + members[i].getName());
                continue;
            }
            String name = getName4Map(members[i]);
            if (namesMap.containsKey(name)) {
                CtMember exists = (CtMember)namesMap.get(name);
                if (exists.getDeclaringClass().getName().equals(className)) {
                    continue;
                }
                //throw new Error("duplicate member name " + name + " " + members[i].getName()+ " = " + ((Member)namesMap.get(name)).getName());
            }
            namesMap.put(name, members[i]);
        }
        return namesMap;
    }

    private int getModifiers(CtMember member) {
        int mod = member.getModifiers();
        if (Modifier.isNative(mod)) {
            mod = mod - Modifier.NATIVE;
        }
        if (Modifier.isSynchronized(mod)) {
            mod = mod - Modifier.SYNCHRONIZED;
        }
        if (Modifier.isStrict(mod)) {
            mod = mod - Modifier.STRICT;
        }
        return mod;
    }

    private void compareConstructors(CtConstructor[] refConstructors, CtConstructor[] implConstructors, String className) throws NotFoundException {
        Map implNames = buildNameMap(implConstructors, className);
        int compared = 0;
        for (int i = 0; i < refConstructors.length; i++) {
            if (!isAPIMember(refConstructors[i])) {
                implNames.remove(getName4Map(refConstructors[i]));
                continue;
            }
            compareConstructor(refConstructors[i], (CtConstructor)implNames.get(getName4Map(refConstructors[i])), className);
            compared++;
            implNames.remove(getName4Map(refConstructors[i]));
        }
        if (config.allowAPIextension) {
            return;
        }
        StringBuffer extra = new StringBuffer(); 
        for (Iterator i = implNames.keySet().iterator(); i.hasNext();) {
            if (extra.length() > 0) {
                extra.append(", ");
            }
            extra.append(((CtConstructor)implNames.get(i.next())).getSignature());
        }
        assertEquals(className + " number of Constructors, Extra constructor(s) [" + extra.toString() + "]", compared, implNames.size() + compared);
    }

    private void compareThrows(CtBehavior refMethod, CtBehavior implMethod, String className) throws NotFoundException {
        List refNames = new Vector();
        CtClass[] refExceptions = refMethod.getExceptionTypes();
        for (int i = 0; i < refExceptions.length; i++) {
            refNames.add(refExceptions[i].getName());
        }

        List implNames = new Vector();
        CtClass[] implExceptions = implMethod.getExceptionTypes();
        for (int i = 0; i < implExceptions.length; i++) {
            implNames.add(implExceptions[i].getName());
            String exceptionName = implExceptions[i].getName();
            assertTrue(className + " " + refMethod.getName() + refMethod.getSignature() + " should not throw " + exceptionName, refNames
                    .contains(exceptionName));

        }
        
        if (!config.allowThrowsLess) {
            for (int i = 0; i < refExceptions.length; i++) {
                String exceptionName = refExceptions[i].getName();
                assertTrue(className + " " + refMethod.getName() + refMethod.getSignature() + " should throw " + exceptionName, implNames
                        .contains(exceptionName));
            }
        }
    }
    
    private void compareConstructor(CtConstructor refConstructor, CtConstructor implConstructor, String className) throws NotFoundException {
        String name = refConstructor.getName();
        assertNotNull(className + " Constructor " + name + " is Missing", implConstructor);
        if (implConstructor == null) {
            return;
        }
        assertEquals(className + ". Constructor " + name + " modifiers", Modifier
                .toString(getModifiers(refConstructor)), Modifier.toString(getModifiers(implConstructor)));
        compareThrows(refConstructor, implConstructor, className);
    }

    private void compareMember(CtMember refMember, CtMember implMember, String className, String signature) {
        String name = refMember.getName();
        assertNotNull(className + "." + name + signature + " is Missing", implMember);
        if (implMember == null) {
            return;
        }
        assertEquals(className + "." + name + " modifiers", Modifier.toString(getModifiers(refMember)), Modifier
                .toString(getModifiers(implMember)));
    }

    private String getName4Map(CtMember member) throws NotFoundException {
        StringBuffer name = new StringBuffer();
        name.append(member.getName());
        if ((member instanceof CtMethod) || (member instanceof CtConstructor)) {
            // Overloaded Methods should have different names
            CtClass[] param;
            if (member instanceof CtMethod) {
                param = ((CtMethod) member).getParameterTypes();
            } else if (member instanceof CtConstructor) {
                param = ((CtConstructor) member).getParameterTypes();
            } else {
                throw new Error("intenal test error");
            }
            name.append("(");
            for (int i = 0; i < param.length; i++) {
                if (i != 0) {
                    name.append(" ,");
                }
                name.append(param[i].getName());
            }
            name.append(")");
        }
        return name.toString();
    }

    private void compareMethods(CtMethod[] refMethods, CtMethod[] implMethods, String className) throws NotFoundException {
        Map implNames = buildNameMap(implMethods, className);
        int compared = 0;
        for (int i = 0; i < refMethods.length; i++) {
            if (!isAPIMember(refMethods[i])) {
                implNames.remove(getName4Map(refMethods[i]));
                continue;
            }
            compareMethod(refMethods[i], (CtMethod) implNames.get(getName4Map(refMethods[i])), className);
            compared++;
            implNames.remove(getName4Map(refMethods[i]));
        }
        if (config.allowAPIextension) {
            return;
        }
        StringBuffer extra = new StringBuffer(); 
        for (Iterator i = implNames.keySet().iterator(); i.hasNext();) {
            if (extra.length() > 0) {
                extra.append(", ");
            }
            extra.append(i.next());
        }
        assertEquals(className + " number of Methods, Extra method(s) [" + extra.toString() + "]", compared, implNames.size() + compared);
    }

    private void compareMethod(CtMethod refMethod, CtMethod implMethod, String className) throws NotFoundException {
        compareMember(refMethod, implMethod, className, refMethod.getSignature());
        if (implMethod == null) {
            return;
        }
        String name = refMethod.getName();
        assertEquals(className + "." + name + " returnType", refMethod.getReturnType().getName(), implMethod
                .getReturnType().getName());
        compareThrows(refMethod, implMethod, className);
    }

    private void compareFields(CtField[] refFields, CtField[] implFields, String className, CtClass refClass, CtClass implClass) throws NotFoundException {
        Map implNames = buildNameMap(implFields, className);
        int compared = 0;
        for (int i = 0; i < refFields.length; i++) {
            String name = getName4Map(refFields[i]);
            if (!isAPIMember(refFields[i])) {
                implNames.remove(name);
                continue;
            }
            compared++;
            CtField impl = (CtField) implNames.get(name);
            compareField(refFields[i], impl, className, refClass, implClass);
            implNames.remove(name);
        }
        if (config.allowAPIextension) {
            return;
        }
        StringBuffer extra = new StringBuffer(); 
        for (Iterator i = implNames.keySet().iterator(); i.hasNext();) {
            if (extra.length() > 0) {
                extra.append(", ");
            }
            extra.append(i.next());
        }
        assertEquals(className + " number of Fields, Extra field(s) [" + extra.toString() + "]", compared, implNames.size() + compared);
    }
    
    private void compareField(CtField refField, CtField implField, String className, CtClass refClass, CtClass implClass) throws NotFoundException {
        String name = refField.getName();
        compareMember(refField, implField, className, "");
        if (implField == null) {
            return;
        }
        assertEquals(className + "." + name + " type", refField.getType().getName(), implField.getType().getName());
        if ((Modifier.isFinal(refField.getModifiers())) && (Modifier.isStatic(refField.getModifiers()))) {
            // Compare value
            Object refConstValue = refField.getConstantValue();
            Object implConstValue = implField.getConstantValue();
            if ((refConstValue == null) && (implConstValue == null)) {
                return;
            }

            String refValue = null;
            if (refConstValue != null) {
                refValue = refConstValue.toString();
            } else if (refField.getType() == CtClass.booleanType) {
                refValue = "false";
            } else if (fieldInitializerHack != null) {
                refValue = (String)fieldInitializerHack.get(className + "." + name);
            }
            
            String implValue = null;
            if (implConstValue != null) {
                implValue = implConstValue.toString();
            } else if (implField.getType() == CtClass.booleanType) {
                implValue = "false";
            }
            assertEquals(className + "." + name + " value ", refValue, implValue);
        }
    }

}
