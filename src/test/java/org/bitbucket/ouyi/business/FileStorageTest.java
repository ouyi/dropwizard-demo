package org.bitbucket.ouyi.business;

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
    public void streamToFile() throws Exception {
        String uploadRootDir = temporaryFolder.newFolder().getAbsolutePath();
        String testFileContent = "test file";
        String testFileName = "test.dat";
        Path testFilePath = Paths.get(uploadRootDir, testFileName);

        InputStream inputStream = new ByteArrayInputStream(testFileContent.getBytes(StandardCharsets.UTF_8));
        FileStorage fileStorage = new FileStorage();
        fileStorage.streamToPath(inputStream, testFilePath);
        assertThat(testFilePath.toFile().exists()).isTrue();
        assertThat(new String(Files.readAllBytes(testFilePath))).isEqualTo(testFileContent);
    }

}
