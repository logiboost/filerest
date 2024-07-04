package org.filerest;

import java.nio.file.Path;

public class PathResolver {

    public static Path resolvePath(Path root, Path subpath) {
        if (subpath.toString().equals("/")) {
            return root;
        }

        Path resolvedPath = root.resolve(subpath).normalize();
        if (!resolvedPath.startsWith(root.normalize())) {
            throw new IllegalArgumentException("Subpath is outside the root path");
        }

        return resolvedPath;
    }
}
