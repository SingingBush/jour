package net.sf.jour.processor;

import java.util.Enumeration;

public interface InputSource {

	public Enumeration getEntries(); 
	 
	public void close();
}
