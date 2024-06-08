package org.filerest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class FileRestController extends FileStorageRestService {
    public FileRestController(FileStorageDriver fileStorageDriver) {
        super(fileStorageDriver);
    }

    @GetMapping("/getFile")
    public ResponseEntity<StreamingResponseBody> getFile(@RequestParam("path") String path) {
        Path filePath = Paths.get(path);
        return getFile(filePath);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("path") String path) {
        Path filePath = Paths.get(path, file.getOriginalFilename());
        return upload(filePath, file);
    }

    @PutMapping("/update/{filename}")
    public ResponseEntity<String> updateFile(@PathVariable String filename,
                                             @RequestParam("path") String path,
                                             @RequestParam("file") MultipartFile file) {
        Path filePath = Paths.get(path, filename);
        return update(filePath, file);
    }

    @DeleteMapping("/delete/{filename}")
    public ResponseEntity<String> deleteFile(@PathVariable String filename,
                                             @RequestParam("path") String path) {
        Path filePath = Paths.get(path, filename);
        return delete(filePath);
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileDto>> listFiles(@RequestParam(value = "path", required = false) String directoryPath) {
        return list(directoryPath == null ? "" : directoryPath);
    }

    @GetMapping("/info")
    public ResponseEntity<Optional<FileDto>> fileInfo(@RequestParam(value = "path", required = false) String directoryPath) {
        return getFileInfo(directoryPath == null ? "" : directoryPath);
    }

    @GetMapping("/open")
    public void open(@RequestParam(value = "path", required = false) String directoryPath) {
        xdgOpen(directoryPath == null ? "" : directoryPath);
    }
}
