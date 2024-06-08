package org.filerest;

import lombok.Builder;
import lombok.Data;

import java.nio.file.Path;

@Data
@Builder
public class File {

    private Path path;
    private boolean isDirectory;

}
