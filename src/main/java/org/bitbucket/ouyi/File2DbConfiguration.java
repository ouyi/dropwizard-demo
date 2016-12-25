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

    private String uploadRootDir = "/tmp/file2db/upload";
    private String dateTimePattern = "MM-dd-yyyy HH:mm:ss";
    private String timezone = "Europe/Berlin";

    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @Valid
    @NotNull
    private MessageQueueFactory messageQueue = new MessageQueueFactory();

    @JsonProperty
    public String getUploadRootDir() {
        return uploadRootDir;
    }

    @JsonProperty
    public String getDateTimePattern() {
        return dateTimePattern;
    }

    @JsonProperty
    public String getTimezone() {
        return timezone;
    }

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    @JsonProperty("messageQueue")
    public MessageQueueFactory getMessageQueueFactory() {
        return messageQueue;
    }

    @JsonProperty("messageQueue")
    public void setMessageQueueFactory(MessageQueueFactory factory) {
        this.messageQueue = factory;
    }
}
