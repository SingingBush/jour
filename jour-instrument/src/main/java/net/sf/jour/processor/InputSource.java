package net.sf.jour.processor;

import java.util.Enumeration;

public interface InputSource<T extends Entry> {

	Enumeration<T> getEntries();

	void close();
}
