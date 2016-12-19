package org.bitbucket.ouyi;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by worker on 12/18/16.
 */
public class File2DbApplication extends Application<File2DbConfiguration>{
    public static void main(String[] args) throws Exception {
        new File2DbApplication().run(args);
    }
    @Override
    public void run(File2DbConfiguration configuration, Environment environment) throws Exception {
        String uploadRootDir = configuration.getUploadRootDir();
        Files.createDirectories(Paths.get(uploadRootDir));
        final UploadResource uploadResource = new UploadResource(uploadRootDir);
        environment.jersey().register(uploadResource);
    }
}
