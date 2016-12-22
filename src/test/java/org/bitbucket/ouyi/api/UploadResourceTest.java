package org.bitbucket.ouyi.api;

import org.bitbucket.ouyi.api.UploadResource;
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
 * Created by worker on 12/16/16.
 */
public class UploadResourceTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void streamToFile() throws Exception {
        String uploadRootDir = temporaryFolder.newFolder().getAbsolutePath();
        String testFileContent = "test file";
        String testFileName = "test.dat";
        Path testFilePath = Paths.get(uploadRootDir, testFileName);
        UploadResource uploadResource = new UploadResource(uploadRootDir);

        InputStream inputStream = new ByteArrayInputStream(testFileContent.getBytes(StandardCharsets.UTF_8));
        uploadResource.streamToFile(inputStream, testFilePath);
        assertThat(testFilePath.toFile().exists()).isTrue();
        assertThat(new String(Files.readAllBytes(testFilePath))).isEqualTo(testFileContent);
    }

}