package org.filerest;

import lombok.Builder;
import lombok.Data;

import java.nio.file.Path;

@Data
@Builder
public class FileDto {

    private String path;
    private boolean isDirectory;

}
