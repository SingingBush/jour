package net.sf.jour.processor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javassist.CannotCompileException;
import javassist.CtClass;

public class InstrumentedCreatedEntry implements Entry {

	private Entry orig; 

	//private CtClass origClass;
	
	private CtClass ctClass;
	
	public InstrumentedCreatedEntry(Entry orig, CtClass origClass, CtClass ctClass) {
		this.orig = orig;
		//this.origClass = origClass;
		this.ctClass = ctClass;
	}

	public InputStream getInputStream() throws IOException {
		try {
			return new ByteArrayInputStream(ctClass.toBytecode());
		} catch (CannotCompileException e) {
			throw new CompileIOException(e);
		}
	}
	
	public Entry getOrigin() {
		return orig;
	}

	public String getName() {
		return "/" + ctClass.getName().replace('.', '/') + ".class";
	}

	public long getSize() {
		return -1;
	}

	public long getTime() {
		return this.orig.getTime();
	}

	public boolean isClass() {
		return true;
	}

	public boolean isDirectory() {
		return false;
	}

}
