package org.filerest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class FileStorageRestService {

    protected final FileStorage fileStorage;

    public ResponseEntity<StreamingResponseBody> getFile(Path filePath) {
        try {
            InputStream is = fileStorage.readFile(filePath);
            StreamingResponseBody stream = outputStream -> {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            };
            return ResponseEntity.ok()
                    .contentType(determineContentType(filePath))
                    .body(stream);
        } catch (StorageException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    public ResponseEntity<String> upload(Path filePath, MultipartFile file) {
        try {
            filePath = fileStorage.saveFile(filePath, file.getInputStream());
            return ResponseEntity.ok("File uploaded successfully: " + filePath);
        } catch (IOException | StorageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
        }
    }


    public ResponseEntity<String> update(Path filePath, MultipartFile file) {
        try {
            filePath = fileStorage.updateFile(filePath, file.getInputStream());
            return ResponseEntity.ok("File updated successfully: " + filePath);
        } catch (IOException | StorageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update file: " + e.getMessage());
        }
    }


    public ResponseEntity<String> delete(Path filePath) {
        try {
            fileStorage.deleteFile(filePath);
            return ResponseEntity.ok("File deleted successfully.");
        } catch (StorageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete file: " + e.getMessage());
        }
    }

    public ResponseEntity<List<FileDto>> list(String directoryPath) {
        try {
            List<File> files = fileStorage.listFiles(Paths.get(directoryPath));
            List<FileDto> fileNames = files.stream().map(file -> FileDto.builder()
                    .path(file.getPath().toString())
                    .isDirectory(file.isDirectory())
                    .build()).collect(Collectors.toList());
            return ResponseEntity.ok(fileNames);
        } catch (StorageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<Optional<FileDto>> getFileInfo(String directoryPath) {
        try {
            Optional<File> files = fileStorage.getFileInfo(Paths.get(directoryPath));
            Optional<FileDto> fileNames = files.map(file -> FileDto.builder()
                    .path(file.getPath().toString())
                    .isDirectory(file.isDirectory()).build());
            return ResponseEntity.ok(fileNames);
        } catch (StorageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public void xdgOpen(String directoryPath) {
        fileStorage.xdgOpen(Paths.get(directoryPath));
    }

    private MediaType determineContentType(Path filePath) {
        try {
            // Use Java's Files.probeContentType to determine the content type based on the file extension
            String contentType = Files.probeContentType(filePath);
            // If contentType could not be determined, fall back to the default content type
            if (contentType == null) {
                contentType = "application/octet-stream"; // Binary data
            }
            return MediaType.parseMediaType(contentType);
        } catch (IOException e) {
            // In case of an exception, default to binary type which is a safe option
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

}
