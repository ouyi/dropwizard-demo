package org.bitbucket.ouyi.io;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by worker on 12/25/16.
 */
public class FileStorageTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void streamToTarget() throws Exception {
        String storageRoot = temporaryFolder.newFolder().getAbsolutePath();
        String testFileContent = "test file";
        String testFileName = "test.dat";
        InputStream inputStream = new ByteArrayInputStream(testFileContent.getBytes(StandardCharsets.UTF_8));
        FileStorage fileStorage = new FileStorage(storageRoot);

        fileStorage.writeTo(inputStream, testFileName);

        Path testFilePath = Paths.get(storageRoot, testFileName);
        assertThat(testFilePath.toFile().exists()).isTrue();
        assertThat(new String(Files.readAllBytes(testFilePath))).isEqualTo(testFileContent);
    }

}
