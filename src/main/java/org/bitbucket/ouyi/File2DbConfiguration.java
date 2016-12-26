package org.bitbucket.ouyi;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import org.bitbucket.ouyi.mq.MessageQueueFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by worker on 12/18/16.
 */
public class File2DbConfiguration extends Configuration {

    @JsonProperty
    private String storageRoot = "/tmp/file2db/upload";
    @JsonProperty
    private String dateTimePattern = "MM-dd-yyyy HH:mm:ss";
    @JsonProperty
    private String timezone = "Europe/Berlin";
    @Valid
    @NotNull
    @JsonProperty
    private DataSourceFactory database = new DataSourceFactory();
    @Valid
    @NotNull
    @JsonProperty
    private MessageQueueFactory messageQueue = new MessageQueueFactory();

    public String getStorageRoot() {
        return storageRoot;
    }

    public String getDateTimePattern() {
        return dateTimePattern;
    }

    public String getTimezone() {
        return timezone;
    }

    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    public MessageQueueFactory getMessageQueueFactory() {
        return messageQueue;
    }

}
