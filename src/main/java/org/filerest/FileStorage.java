package org.filerest;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface FileStorage {
    Path saveFile(Path path, InputStream inputStream) throws StorageException;
    InputStream readFile(Path path) throws StorageException;
    Path updateFile(Path path, InputStream inputStream) throws StorageException;
    void deleteFile(Path path) throws StorageException;
    List<File> listFiles(Path directoryPath) throws StorageException;
    Optional<File> getFileInfo(Path directoryPath) throws StorageException;
    void xdgOpen(Path path);
}
