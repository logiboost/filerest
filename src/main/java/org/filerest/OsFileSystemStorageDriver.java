package org.filerest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OsFileSystemStorageDriver implements FileStorageDriver {

    private final Path rootPath;

    public OsFileSystemStorageDriver(String root) {
        rootPath = Path.of(root);
    }

    @Override
    public Path saveFile(Path path, InputStream inputStream) throws StorageException {
        Path targetPath = PathResolver.resolvePath(rootPath, path);
        try {
            Files.createDirectories(targetPath.getParent());
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            return targetPath;
        } catch (IOException e) {
            throw new StorageException("Failed to save " + targetPath, e);
        }
    }

    @Override
    public Path updateFile(Path path, InputStream inputStream) throws StorageException {
        return saveFile(path, inputStream);
    }

    @Override
    public InputStream readFile(Path path) throws StorageException {
        try {
            Path filePath = rootPath.resolve(path);
            return Files.newInputStream(filePath);
        } catch (IOException e) {
            throw new StorageException("Failed to read file", e);
        }
    }

    @Override
    public void deleteFile(Path path) throws StorageException {
        try {
            Path filePath = rootPath.resolve(path);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new StorageException("Failed to delete file", e);
        }
    }

    @Override
    public List<File> listFiles(Path directoryPath) throws StorageException {
        Path targetPath = PathResolver.resolvePath(rootPath, directoryPath);
        try (Stream<Path> walk = Files.walk(targetPath, 1)) {
            return walk
                    .filter(path -> !targetPath.equals(path))
                    .filter(path -> Files.isRegularFile(path) || Files.isDirectory(path))
                    .map(path -> File.builder()
                            .path(rootPath.relativize(path))
                            .isDirectory(Files.isDirectory(path))
                            .build())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new StorageException("Failed to list files", e);
        }
    }

    @Override
    public Optional<File> getFileInfo(Path directoryPath) {
        Path targetPath = PathResolver.resolvePath(rootPath, directoryPath);

        if (!Files.exists(targetPath)) {
            return Optional.empty();
        }

        return Optional.of(File.builder()
                            .path(rootPath.relativize(targetPath))
                            .isDirectory(Files.isDirectory(targetPath))
                            .build());
    }

    @Override
    public void xdgOpen(Path path) {
        Path targetPath = PathResolver.resolvePath(rootPath, path);

        // Check if the path exists
        if (Files.exists(targetPath)) {
            try {
                // Prepare the command to open the directory
                ProcessBuilder processBuilder = new ProcessBuilder("xdg-open", targetPath.toString());

                // Start the process asynchronously
                Process process = processBuilder.start();

                // Optionally, you can handle the process output or wait for it non-blockingly
                new Thread(() -> {
                    try {
                        int exitCode = process.waitFor();
                        if (exitCode != 0) {
                            System.err.println("Error opening directory: Exit code " + exitCode);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("Thread interrupted: " + e.getMessage());
                    }
                }).start();
            } catch (IOException e) {
                System.err.println("IOException when trying to open directory: " + e.getMessage());
            }
        } else {
            System.err.println("Directory does not exist: " + targetPath);
        }
    }
}
