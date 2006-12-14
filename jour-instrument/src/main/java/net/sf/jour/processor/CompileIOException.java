/**
 * 
 */
package net.sf.jour.processor;

import java.io.IOException;

class CompileIOException extends IOException {

	private static final long serialVersionUID = 1L;

	private Throwable cause;
	
	public CompileIOException(Throwable cause) {
		this.cause = cause;
	}
	 public Throwable getCause() {
	    return cause;
	 }
}