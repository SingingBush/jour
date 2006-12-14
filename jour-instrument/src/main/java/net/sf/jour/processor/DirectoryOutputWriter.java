package net.sf.jour.processor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DirectoryOutputWriter implements OutputWriter {

	private File dir;

	public DirectoryOutputWriter(File dir) {
		this.dir = dir;
	}

	public boolean needUpdate(Entry entry) {
		return true;
	}

	public void write(Entry entry) throws IOException {
		if (entry.getOrigin() == entry) {
			return;
		}
		String name = entry.getName().replace('/', File.separatorChar);
		File file = new File(dir.getAbsolutePath() + File.separatorChar + name);
		File dir = file.getParentFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (entry.isDirectory()) {
			file.mkdirs();
			return;
		}
		
		OutputStream out = null;
		InputStream in = null;
		try {
			out = new FileOutputStream(file);
			in = entry.getInputStream();

			byte[] b = new byte[256];
			int cnt;
			while ((cnt = in.read(b)) != -1) {
				out.write(b, 0, cnt);
			}
			file.setLastModified(entry.getTime());
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}

	public void close() {

	}

}
