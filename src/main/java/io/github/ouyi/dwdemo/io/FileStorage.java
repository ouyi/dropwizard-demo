package io.github.ouyi.dwdemo.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import static java.nio.file.Files.copy;

/**
 * Created by worker on 12/25/16.
 */
public class FileStorage {

    private final String storageRoot;

    public FileStorage(String storageRoot) {
        this.storageRoot = storageRoot;
    }

    public void writeTo(InputStream inputStream, String filename) throws IOException {
        streamToPath(inputStream, Paths.get(storageRoot, filename));
    }

    protected void streamToPath(InputStream inputStream, java.nio.file.Path targetPath) throws IOException {
        try (InputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
            java.nio.file.Path temp = Files.createTempFile(FileStorage.class.getSimpleName(), ".tmp");
            copy(bufferedInputStream, temp, StandardCopyOption.REPLACE_EXISTING);
            Files.move(temp, targetPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        }
    }

    public Stream<String> readLinesFrom(String filename) throws IOException {
        return Files.lines(Paths.get(storageRoot, filename));
    }
}
