package net.sf.jour.processor;

import java.util.Enumeration;

public interface InputSource {

	Enumeration getEntries();

	void close();
}
