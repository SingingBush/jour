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

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javassist.ClassPool;
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

    public APICompareConfig config = new APICompareConfig();
    
    public static void compare(String classpath, String signatureFileName, APICompareConfig config, boolean useSystemClassPath, String supportingJars) throws ChangeDetectedException {
        SignatureImport im = new SignatureImport(useSystemClassPath, supportingJars);
        im.load(signatureFileName);
        
        ClassPool classPool = new ClassPool();
        try {
            classPool.appendClassPath(classpath);
            if (supportingJars != null) {
                classPool.appendClassPath(supportingJars);
            }
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
        if (useSystemClassPath) {
            classPool.appendSystemPath();
        }
        
        List classes = im.getClasses();
        APICompare cmp = new APICompare();
        if (config != null) {
            cmp.config = config;
        }
        for (Iterator iterator = classes.iterator(); iterator.hasNext();) {
            CtClass refClass = (CtClass) iterator.next();
            try {
                cmp.compareClasses(refClass, classPool.get(refClass.getName()));
            } catch (NotFoundException e) {
                cmp.fail(refClass.getName() + " is missing");
            }
        }
        if (cmp.changes.size() > 0) {
            throw new ChangeDetectedException(cmp.changes);
        }
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
    
    public List compareClasses(CtClass refClass, CtClass implClass) throws NotFoundException {

        String className = refClass.getName();

        assertEquals(className + " isInterface", refClass.isInterface(), implClass.isInterface());
        assertEquals(className + " getModifiers", refClass.getModifiers(), implClass.getModifiers());

        CtClass[] refInterfaces = refClass.getInterfaces();
        CtClass[] implInterfaces = implClass.getInterfaces();

        assertEquals(className + " interfaces implemented", refInterfaces.length, implInterfaces.length);
        compareInterfaces(refInterfaces, implInterfaces, className);

        if (refClass.getSuperclass() != null) {
            assertEquals(className + " Superclass", refClass.getSuperclass().getName(), implClass.getSuperclass()
                    .getName());
        } else {
            assertNull(className + " Superclass", implClass.getSuperclass());
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
        CtField[] refFields = refClass.getFields();
        CtField[] implFields = implClass.getFields();
        compareFields(refFields, implFields, className, refClass, implClass);

        return changes;
    }
    
    private void compareInterfaces(CtClass[] refInterfaces, CtClass[] implInterfacess, String className) {
        List implNames = new Vector();
        for (int i = 0; i < implInterfacess.length; i++) {
            implNames.add(implInterfacess[i].getName());
        }
        for (int i = 0; i < refInterfaces.length; i++) {
            String interfaceName = refInterfaces[i].getName();
            assertTrue(className + "Interface " + interfaceName, implNames.contains(interfaceName));
        }
    }
    
    
    private Map buildNameMap(CtMember[] members, String className) throws NotFoundException {
        Map namesMap = new Hashtable();
        for (int i = 0; i < members.length; i++) {
            if (ignoreMember(members[i])) {
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

    private boolean ignoreMember(CtMember member) {
        if (Modifier.isPublic(member.getModifiers())) {
            return false;
        } else if (Modifier.isProtected(member.getModifiers())) {
            return false;
        } else {
            return true;
        }
    }

    private int getModifiers(CtMember member) {
        int mod = member.getModifiers();
        if (Modifier.isNative(mod)) {
            mod = mod - Modifier.NATIVE;
        }
        if (Modifier.isSynchronized(mod)) {
            mod = mod - Modifier.SYNCHRONIZED;
        }
        return mod;
    }

    private void compareConstructors(CtConstructor[] refConstructors, CtConstructor[] implConstructors, String className) throws NotFoundException {
        Map implNames = buildNameMap(implConstructors, className);
        int compared = 0;
        for (int i = 0; i < refConstructors.length; i++) {
            if (ignoreMember(refConstructors[i])) {
                continue;
            }
            compareConstructor(refConstructors[i], (CtConstructor) implNames.get(getName4Map(refConstructors[i])),
                    className);
            compared++;
            implNames.remove(getName4Map(refConstructors[i]));
        }
        if (!config.allowAPIextension) {
            return;
        }
        assertEquals(className + " number of Constructors ", compared, implNames.size() + compared);
        for (Iterator i = implNames.keySet().iterator(); i.hasNext();) {
            System.out.println("   extra constructor " + i.next());
        }

    }

    private void compareConstructor(CtConstructor refConstructor, CtConstructor implConstructor, String className) {
        String name = refConstructor.getName();
        assertNotNull(className + " Constructor " + name + " is Missing", implConstructor);
        if (implConstructor == null) {
            return;
        }
        assertEquals(className + ". Constructor " + name + " getModifiers", Modifier
                .toString(getModifiers(refConstructor)), Modifier.toString(getModifiers(implConstructor)));
    }

    private void compareMember(CtMember refMember, CtMember implMember, String className) {
        String name = refMember.getName();
        assertNotNull(className + "." + name + " is Missing", implMember);
        if (implMember == null) {
            return;
        }
        assertEquals(className + "." + name + " getModifiers", Modifier.toString(getModifiers(refMember)), Modifier
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
            if (ignoreMember(refMethods[i])) {
                continue;
            }
            compareMethod(refMethods[i], (CtMethod) implNames.get(getName4Map(refMethods[i])), className);
            compared++;
            implNames.remove(getName4Map(refMethods[i]));
        }
        if (!config.allowAPIextension) {
            return;
        }
        assertEquals(className + " number of Methods ", compared, implNames.size() + compared);
        for (Iterator i = implNames.keySet().iterator(); i.hasNext();) {
            System.out.println("   extra method " + i.next());
        }
    }

    private void compareMethod(CtMethod refMethod, CtMethod implMethod, String className) throws NotFoundException {
        compareMember(refMethod, implMethod, className);
        if (implMethod == null) {
            return;
        }
        String name = refMethod.getName();
        assertEquals(className + "." + name + " getReturnType", refMethod.getReturnType().getName(), implMethod
                .getReturnType().getName());
    }

    private void compareFields(CtField[] refFields, CtField[] implFields, String className, CtClass refClass, CtClass implClass) throws NotFoundException {
        Map implNames = buildNameMap(implFields, className);
        Map implNamesTested = new HashMap();
        int compared = 0;
        for (int i = 0; i < refFields.length; i++) {
            if (ignoreMember(refFields[i])) {
                continue;
            }
            String name = getName4Map(refFields[i]);
            CtField impl = (CtField) implNames.get(name);
            if ((impl == null) && (implNamesTested.containsKey(name))) {
                continue;
            }
            compareField(refFields[i], impl, className, refClass, implClass);
            compared++;
            implNamesTested.put(name, impl);
            implNames.remove(name);
        }
        if (!config.allowAPIextension) {
            return;
        }
        assertEquals(className + " number of Fields ", compared, implNames.size() + compared);
        for (Iterator i = implNames.keySet().iterator(); i.hasNext();) {
            System.out.println("   extra field " + i.next());
        }
    }
    
    private void compareField(CtField refField, CtField implField, String className, CtClass refClass, CtClass implClass) throws NotFoundException {
        String name = refField.getName();
        compareMember(refField, implField, className);
        if (implField == null) {
            return;
        }
        assertEquals(className + "." + name + " getType", refField.getType().getName(), implField.getType().getName());
        if ((Modifier.isFinal(refField.getModifiers())) && (Modifier.isStatic(refField.getModifiers()))) {
            // Compare value
            Object refConstValue = refField.getConstantValue();
            Object implConstValue = implField.getConstantValue();
            if ((refConstValue == null) && (implConstValue == null)) {
                return;
            }

            String value = refConstValue.toString();
            String implValue = implConstValue.toString();
            assertEquals(className + "." + name + " value ", value, implValue);
        }
    }

}
