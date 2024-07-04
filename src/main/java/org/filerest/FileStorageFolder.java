package org.filerest;

import lombok.RequiredArgsConstructor;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class FileStorageFolder implements FileStorage {

    private final FileStorageDriver driver;
    private final Path folderPath;

    public FileStorageFolder(FileStorageDriver driver, String path) {
        this.driver = driver;
        this.folderPath = Path.of(path);
    }

    @Override
    public Path saveFile(Path path, InputStream inputStream) throws StorageException {
        return driver.saveFile(folderPath.resolve(path), inputStream);
    }

    @Override
    public InputStream readFile(Path path) throws StorageException {
        return driver.readFile(folderPath.resolve(path));
    }

    @Override
    public Path updateFile(Path path, InputStream inputStream) throws StorageException {
        return driver.updateFile(folderPath.resolve(path), inputStream);
    }

    @Override
    public void deleteFile(Path path) throws StorageException {
        driver.deleteFile(folderPath.resolve(path));
    }

    @Override
    public List<File> listFiles(Path path) throws StorageException {
        return driver.listFiles(folderPath.resolve(path));
    }

    @Override
    public Optional<File> getFileInfo(Path path) throws StorageException {
        return driver.getFileInfo(folderPath.resolve(path));
    }

    @Override
    public void xdgOpen(Path path) {
        driver.xdgOpen(folderPath.resolve(path));
    }
}
