package org.bitbucket.ouyi.mq;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Link;

/**
 * Created by worker on 12/26/16.
 */
public class File2DbWorkerConfig {
    @Valid
    @NotNull
    @JsonProperty
    private MessageQueueFactory messageQueue = new MessageQueueFactory();

    @NotNull
    @JsonProperty
    private String transformerEndpoint = "http://localhost:8080/transform";

    public MessageQueueFactory getMessageQueueFactory() {
        return messageQueue;
    }

    public String getTransformerEndpoint() {
        return transformerEndpoint;
    }
}
