package org.bitbucket.ouyi.business;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static java.nio.file.Files.copy;

/**
 * Created by worker on 12/25/16.
 */
public class FileStorage {
    public void streamToFile(InputStream inputStream, java.nio.file.Path targetPath) throws IOException {
        try (InputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
            java.nio.file.Path temp = Files.createTempFile("upload", null);
            copy(bufferedInputStream, temp, StandardCopyOption.REPLACE_EXISTING);
            Files.move(temp, targetPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        }
    }
}
