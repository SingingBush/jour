package net.sf.jour.processor;

import java.io.IOException;
import java.io.InputStream;

/**
 * Abstract File system or JarFile entry 
 * 
 * @author vlads
 */

public interface Entry {

	 String getName();  
	
	 boolean isDirectory(); 
	 
	 long getSize();
	 
	 long getTime();
	 
	 boolean isClass(); 
	 
	 InputStream getInputStream() throws IOException;
	 
	 Entry getOrigin();
}
