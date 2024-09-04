package net.sf.jour.processor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;

public class FileEntry implements Entry {

    private final File file;

    private final String name;

    public FileEntry(File file, String baseName) {
        this.file = file;
        this.name = file.getAbsolutePath().substring(baseName.length() + 1).replace('\\', '/');
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return Files.newInputStream(file.toPath());
    }

    @Override
    public Entry getOrigin() {
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getSize() {
        return file.length();
    }

    @Override
    public long getTime() {
        return file.lastModified();
    }

    @Override
    public boolean isClass() {
        return file.getName().endsWith(".class");
    }

    @Override
    public boolean isDirectory() {
        return file.isDirectory();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileEntry)) return false;
        return Objects.equals(file, ((FileEntry) o).file);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(file);
    }
}
