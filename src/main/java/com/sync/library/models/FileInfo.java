package com.sync.library.models;

import java.util.Objects;

public class FileInfo {
    private String name;
    private String path;
    private long size;
    private long lastModified;
    private boolean isDirectory;
    private String extension;

    public FileInfo(String name, String path, long size, long lastModified, boolean isDirectory, String extension) {
        this.name = name;
        this.path = path;
        this.size = size;
        this.lastModified = lastModified;
        this.isDirectory = isDirectory;
        this.extension = extension;
    }

    public String getName() { return name; }
    public String getPath() { return path; }
    public long getSize() { return size; }
    public long getLastModified() { return lastModified; }
    public boolean isDirectory() { return isDirectory; }
    public String getExtension() { return extension; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileInfo fileInfo = (FileInfo) o;
        return size == fileInfo.size && lastModified == fileInfo.lastModified
                && isDirectory == fileInfo.isDirectory
                && Objects.equals(name, fileInfo.name)
                && Objects.equals(path, fileInfo.path)
                && Objects.equals(extension, fileInfo.extension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path, size, lastModified, isDirectory, extension);
    }

    @Override
    public String toString() {
        return "FileInfo{name='" + name + "', path='" + path + "', size=" + size
                + ", lastModified=" + lastModified + ", isDirectory=" + isDirectory
                + ", extension='" + extension + "'}";
    }
}
