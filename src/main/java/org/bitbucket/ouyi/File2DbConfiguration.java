package org.bitbucket.ouyi;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by worker on 12/18/16.
 */
public class File2DbConfiguration extends Configuration {

    private String uploadRootDir = "/tmp/file2db/upload";
    private String transformRootDir = "/tmp/file2db/transform";

    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @JsonProperty
    public String getUploadRootDir() {
        return uploadRootDir;
    }

    @JsonProperty
    public String getTransformRootDir() {
        return transformRootDir;
    }

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }
}
