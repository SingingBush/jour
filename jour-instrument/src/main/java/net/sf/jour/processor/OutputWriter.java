package net.sf.jour.processor;

import java.io.IOException;

public interface OutputWriter {

	public boolean needUpdate(Entry entry);
	
	public void write(Entry entry) throws IOException;

	public void close();
}
