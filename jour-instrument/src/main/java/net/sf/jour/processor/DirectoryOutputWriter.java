package net.sf.jour.processor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DirectoryOutputWriter implements OutputWriter {

	private final File dir;

	public DirectoryOutputWriter(File dir) {
		this.dir = dir;
	}

	@Override
	public boolean needUpdate(Entry entry) {
		// TODO
		return true;
	}

	@Override
	public void write(Entry entry) throws IOException {
		// if (entry.getOrigin() == entry) {
		// return;
		// }
		String name = entry.getName().replace('/', File.separatorChar);
		File file = new File(this.dir.getAbsolutePath() + File.separatorChar + name);
		File dir = file.getParentFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (entry.isDirectory()) {
			file.mkdirs();
			return;
		}

		try (
			final OutputStream out = new FileOutputStream(file);
			final InputStream in = entry.getInputStream()
		) {
			final byte[] b = new byte[256];
			int cnt;
			while ((cnt = in.read(b)) != -1) {
				out.write(b, 0, cnt);
			}
			file.setLastModified(entry.getTime());
		}
	}

	@Override
	public void close() {

	}

}
