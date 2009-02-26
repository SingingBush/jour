/*
 * Jour - bytecode instrumentation library
 *
 * Copyright (C) 2007-2008 Vlad Skarzhevskyy
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

    static ThreadLocal counters = new ThreadLocal();

    private List changesMissing;

    private List changesExtra;

    private List changesChanges;

    public APICompare() {
        filter = new APIFilter(APIFilter.PROTECTED);
        config = new APICompareConfig();
        changesMissing = new Vector();
        changesExtra = new Vector();
        changesChanges = new Vector();
    }

    public static void compare(String classpath, String signatureFileName, APICompareConfig config, boolean useSystemClassPath, String supportingJars)
            throws ChangeDetectedException {
        List changes = listChanges(classpath, signatureFileName, config, useSystemClassPath, supportingJars);
        if (changes.size() > 0) {
            throw new ChangeDetectedException(changes);
        }
    }

    public static List listChanges(String classpath, String signatureFileName, APICompareConfig config, boolean useSystemClassPath, String supportingJars) {
        counters.set(new Long(0));
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

        // ExportClasses.export("target/test-api-classes", classes);

        APICompare cmp = new APICompare();
        if (config != null) {
            cmp.config = config;
            cmp.filter = new APIFilter(config.apiLevel);
        }

        int classesCount = 0;

        for (Iterator iterator = classes.iterator(); iterator.hasNext();) {
            CtClass refClass = (CtClass) iterator.next();
            if (!cmp.filter.isAPIClass(refClass)) {
                continue;
            }
            classesCount++;
            counters.set(new Long(classesCount));
            CtClass implClass = null;
            try {
                implClass = classPool.get(refClass.getName());
            } catch (NotFoundException e) {
                cmp.fail(refClass.getName() + " is missing");
                cmp.addMissing(refClass);
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

    public static long getClassesCount() {
        return ((Long) counters.get()).longValue();
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

    private void addMissing(Object member) {
        changesMissing.add(member);
    }

    private void addExtra(Object member) {
        changesExtra.add(member);
    }

    private void addChanges(Object member) {
        changesChanges.add(member);
    }

    public Iterator getChangesMissing() {
        return changesMissing.iterator();
    }

    public Iterator getChangesExtra() {
        return changesExtra.iterator();
    }

    public Iterator getChangesChanges() {
        return changesChanges.iterator();
    }

    public List compareClasses(CtClass refClass, CtClass implClass) throws NotFoundException {

        String className = refClass.getName();

        boolean ch = false;

        ch |= assertEquals(className + " isInterface", refClass.isInterface(), implClass.isInterface());
        int modIgnore = 0;
        if (refClass.isInterface()) {
            modIgnore = Modifier.STATIC;
        }
        ch |= assertEquals(className + " getModifiers", new ModifiersValue(refClass.getModifiers() & ~modIgnore), new ModifiersValue(implClass.getModifiers() & ~modIgnore));

        CtClass[] refInterfaces = refClass.getInterfaces();
        CtClass[] implInterfaces = implClass.getInterfaces();

        ch = ch | assertEquals(className + " interfaces implemented", refInterfaces.length, implInterfaces.length);
        ch = ch | compareInterfaces(refInterfaces, implInterfaces, className);

        if (implClass.getSuperclass() == null) {
            // java.lang.Object in CLDC / javassist will reference same class...
            if (refClass.getSuperclass() != null) {
                ch |= assertEquals(className + " Superclass ", "java.lang.Object", refClass.getSuperclass().getName());
            }
        } else if (refClass.getSuperclass() != null) {
            ch |= assertEquals(className + " Superclass", refClass.getSuperclass().getName(), implClass.getSuperclass().getName());
        } else {
            ch |= assertNull(className + " Superclass " + className(implClass.getSuperclass()), implClass.getSuperclass());
        }
        if (ch) {
            addChanges(implClass);
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

    private boolean compareInterfaces(CtClass[] refInterfaces, CtClass[] implInterfacess, String className) {
        List implNames = new Vector();
        for (int i = 0; i < implInterfacess.length; i++) {
            implNames.add(implInterfacess[i].getName());
        }
        boolean ch = false;
        for (int i = 0; i < refInterfaces.length; i++) {
            String interfaceName = refInterfaces[i].getName();
            ch |= assertTrue(className + " should implement interface " + interfaceName, implNames.contains(interfaceName));
        }
        return ch;
    }

    private Map buildNameMap(CtMember[] members, String className) throws NotFoundException {
        Map namesMap = new Hashtable();
        for (int i = 0; i < members.length; i++) {
            if (!isAPIMember(members[i])) {
                // System.out.println("ignore " + members[i].getName());
                continue;
            }
            String name = getName4Map(members[i]);
            if (namesMap.containsKey(name)) {
                CtMember exists = (CtMember) namesMap.get(name);
                if (exists.getDeclaringClass().getName().equals(className)) {
                    continue;
                }
                // throw new Error("duplicate member name " + name + " " +
                // members[i].getName()+ " = " +
                // ((Member)namesMap.get(name)).getName());
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
        int expectedConstructors = 0;
        for (int i = 0; i < refConstructors.length; i++) {
            if (!isAPIMember(refConstructors[i])) {
                implNames.remove(getName4Map(refConstructors[i]));
                continue;
            }
            expectedConstructors++;
            CtConstructor implConstructor = (CtConstructor) implNames.get(getName4Map(refConstructors[i]));
            compareConstructor(refConstructors[i], implConstructor, className);
            implNames.remove(getName4Map(refConstructors[i]));
            if (implConstructor != null) {
                compared++;
            }
        }
        if (config.allowAPIextension) {
            return;
        }
        StringBuffer extra = new StringBuffer();
        for (Iterator i = implNames.keySet().iterator(); i.hasNext();) {
            if (extra.length() > 0) {
                extra.append(", ");
            } else {
                extra.append(", Extra constructor(s) [");
            }
            CtConstructor cx = (CtConstructor) implNames.get(i.next());
            addExtra(cx);
            extra.append(cx.getSignature());
        }
        if (extra.length() > 0) {
            extra.append("]");
        }
        assertEquals(className + " number of Constructors" + extra.toString(), expectedConstructors, implNames.size() + compared);
    }

    private boolean compareThrows(CtBehavior refMethod, CtBehavior implMethod, String className) throws NotFoundException {
        List refNames = new Vector();
        CtClass[] refExceptions = refMethod.getExceptionTypes();
        for (int i = 0; i < refExceptions.length; i++) {
            refNames.add(refExceptions[i].getName());
        }

        boolean ch = false;
        List implNames = new Vector();
        CtClass[] implExceptions = implMethod.getExceptionTypes();
        for (int i = 0; i < implExceptions.length; i++) {
            implNames.add(implExceptions[i].getName());
            String exceptionName = implExceptions[i].getName();
            ch |= assertTrue(className + " " + refMethod.getName() + refMethod.getSignature() + " should not throw " + exceptionName, refNames
                    .contains(exceptionName));

        }

        if (!config.allowThrowsLess) {
            for (int i = 0; i < refExceptions.length; i++) {
                String exceptionName = refExceptions[i].getName();
                ch |= assertTrue(className + " " + refMethod.getName() + refMethod.getSignature() + " should throw " + exceptionName, implNames
                        .contains(exceptionName));
            }
        }
        return ch;
    }

    private void compareConstructor(CtConstructor refConstructor, CtConstructor implConstructor, String className) throws NotFoundException {
        String name = refConstructor.getSignature();
        assertNotNull(className + " Constructor " + name + " is Missing", implConstructor);
        if (implConstructor == null) {
            addMissing(refConstructor);
            return;
        }
        boolean ch = assertEquals(className + ". Constructor " + name + " modifiers", Modifier.toString(getModifiers(refConstructor)), Modifier
                .toString(getModifiers(implConstructor)));
        ch |= compareThrows(refConstructor, implConstructor, className);
        if (ch) {
            addChanges(implConstructor);
        }
    }

    private boolean compareMember(CtMember refMember, CtMember implMember, String className, String signature) {
        String name = refMember.getName();
        assertNotNull(className + "." + name + signature + " is Missing", implMember);
        if (implMember == null) {
            addMissing(refMember);
            return true;
        }
        return assertEquals(className + "." + name + " modifiers", Modifier.toString(getModifiers(refMember)), Modifier.toString(getModifiers(implMember)));
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
        int expectedMethods = 0;
        for (int i = 0; i < refMethods.length; i++) {
            if (!isAPIMember(refMethods[i])) {
                implNames.remove(getName4Map(refMethods[i]));
                continue;
            }
            expectedMethods++;
            CtMethod implMethod = (CtMethod) implNames.get(getName4Map(refMethods[i]));
            compareMethod(refMethods[i], implMethod, className);
            implNames.remove(getName4Map(refMethods[i]));
            if (implMethod != null) {
                compared++;
            }
        }
        if (config.allowAPIextension) {
            return;
        }
        StringBuffer extra = new StringBuffer();
        for (Iterator i = implNames.keySet().iterator(); i.hasNext();) {
            if (extra.length() > 0) {
                extra.append(", ");
            } else {
                extra.append(", Extra method(s) [");
            }
            String extName = (String) i.next();
            CtMethod mx = (CtMethod) implNames.get(extName);
            addExtra(mx);
            extra.append(extName);
        }
        if (extra.length() > 0) {
            extra.append("]");
        }
        assertEquals(className + " number of Methods" + extra.toString(), expectedMethods, implNames.size() + compared);
    }

    private void compareMethod(CtMethod refMethod, CtMethod implMethod, String className) throws NotFoundException {
        boolean ch = compareMember(refMethod, implMethod, className, refMethod.getSignature());
        if (implMethod == null) {
            return;
        }
        String name = refMethod.getName();
        ch |= assertEquals(className + "." + name + " returnType", refMethod.getReturnType().getName(), implMethod.getReturnType().getName());
        ch |= compareThrows(refMethod, implMethod, className);
        if (ch) {
            addChanges(implMethod);
        }
    }

    private void compareFields(CtField[] refFields, CtField[] implFields, String className, CtClass refClass, CtClass implClass) throws NotFoundException {
        Map implNames = buildNameMap(implFields, className);
        int compared = 0;
        int expectedFields = 0;
        for (int i = 0; i < refFields.length; i++) {
            String name = getName4Map(refFields[i]);
            if (!isAPIMember(refFields[i])) {
                implNames.remove(name);
                continue;
            }
            expectedFields++;
            CtField impl = (CtField) implNames.get(name);
            compareField(refFields[i], impl, className, refClass, implClass);
            implNames.remove(name);
            if (impl != null) {
                compared++;
            }
        }
        if (config.allowAPIextension) {
            return;
        }
        StringBuffer extra = new StringBuffer();
        for (Iterator i = implNames.keySet().iterator(); i.hasNext();) {
            if (extra.length() > 0) {
                extra.append(", ");
            } else {
                extra.append(", Extra field(s) [");
            }
            String extName = (String) i.next();
            CtField fx = (CtField) implNames.get(extName);
            addExtra(fx);
            extra.append(extName);
        }
        if (extra.length() > 0) {
            extra.append("]");
        }
        assertEquals(className + " number of Fields" + extra.toString(), expectedFields, implNames.size() + compared);
    }

    private void compareField(CtField refField, CtField implField, String className, CtClass refClass, CtClass implClass) throws NotFoundException {
        String name = refField.getName();
        boolean ch = compareMember(refField, implField, className, "");
        if (implField == null) {
            return;
        }
        ch |= assertEquals(className + "." + name + " type", refField.getType().getName(), implField.getType().getName());
        if ((Modifier.isFinal(refField.getModifiers())) && (Modifier.isStatic(refField.getModifiers()))) {
            // Compare value
            Object refConstValue = refField.getConstantValue();
            Object implConstValue = implField.getConstantValue();
            if ((refConstValue != null) || (implConstValue != null)) {

                String refValue = null;
                if (refConstValue != null) {
                    refValue = refConstValue.toString();
                } else if (refField.getType() == CtClass.booleanType) {
                    refValue = "false";
                }

                String implValue = null;
                if (implConstValue != null) {
                    implValue = implConstValue.toString();
                } else if (implField.getType() == CtClass.booleanType) {
                    implValue = "false";
                }
                ch |= assertEquals(className + "." + name + " value ", refValue, implValue);
            }
        }
        if (ch) {
            addChanges(implField);
        }
    }

}
