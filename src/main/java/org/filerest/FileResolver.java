package org.filerest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.nio.file.Path;

public class FileResolver {

    private final FileStorageRestService fileStorageRestService;

    public FileResolver(FileStorage storage) {
        fileStorageRestService = new FileStorageRestService(storage);
    }

    @GetMapping("/getFile")
    public ResponseEntity<StreamingResponseBody> getFile(@RequestParam("path") String path) {
        return fileStorageRestService.getFile(Path.of(path));
    }

}
