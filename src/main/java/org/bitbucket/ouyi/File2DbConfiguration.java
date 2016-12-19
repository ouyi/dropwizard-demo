package org.bitbucket.ouyi;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

/**
 * Created by worker on 12/18/16.
 */
public class File2DbConfiguration extends Configuration{
    private String uploadRootDir = "/tmp/file2db";

    @JsonProperty
    public String getUploadRootDir() {
        return uploadRootDir;
    }
}
