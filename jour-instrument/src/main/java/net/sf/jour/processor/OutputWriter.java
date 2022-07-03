package net.sf.jour.processor;

import java.io.IOException;

public interface OutputWriter {

	boolean needUpdate(Entry entry);

	void write(Entry entry) throws IOException;

	void close();
}
