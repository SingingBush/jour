package net.sf.jour.processor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javassist.CannotCompileException;
import javassist.CtClass;

public class InstrumentedEntry implements Entry {

	private Entry orig; 
	
	private CtClass ctClass;
	
	public InstrumentedEntry(Entry orig, CtClass ctClass) {
		this.orig = orig;
		this.ctClass = ctClass;
	}
	
	private static class CompileIOException extends IOException {

		private static final long serialVersionUID = 1L;

		private Throwable cause;
		
		public CompileIOException(Throwable cause) {
			this.cause = cause;
		}
		 public Throwable getCause() {
		    return cause;
		 }
	}
	
	public InputStream getInputStream() throws IOException {
		try {
			return new ByteArrayInputStream(ctClass.toBytecode());
		} catch (CannotCompileException e) {
			throw new CompileIOException(e);
		}
	}

	public String getName() {
		return this.orig.getName();
	}

	public long getSize() {
		return this.orig.getSize();
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
