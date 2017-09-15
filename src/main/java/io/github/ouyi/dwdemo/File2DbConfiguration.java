package io.github.ouyi.dwdemo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.github.ouyi.dwdemo.mq.MessageQueueFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * An instance of this class will be created by the framework based on the yml config file.
 *
 * All fields marked with <code>@JsonProperty</code> are configurable.
 */
public class File2DbConfiguration extends Configuration {

    @JsonProperty
    private String storageRoot = "/tmp/dwdemo/upload";
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
