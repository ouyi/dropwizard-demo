package org.bitbucket.ouyi.mq;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Environment;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by worker on 12/25/16.
 */
public class MessageQueueFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageQueueFactory.class);

    @NotEmpty
    private String host = "localhost";

    @Min(1)
    @Max(65535)
    private int port = 5672;

    private String queueName = "filename_queue";

    @JsonProperty
    public String getHost() {
        return host;
    }

    @JsonProperty
    public void setHost(String host) {
        this.host = host;
    }

    @JsonProperty
    public int getPort() {
        return port;
    }

    @JsonProperty
    public void setPort(int port) {
        this.port = port;
    }

    @JsonProperty
    public String getQueueName() {
        return queueName;
    }

    /**
     * Build with lifecycle hooks set up.
     */
    public MessageQueueClient build(Environment environment) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(getHost());
        factory.setPort(getPort());
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(getQueueName(), true, false, false, null);
        MessageQueueClient client = new MessageQueueClient(channel, getQueueName());

        environment.lifecycle().manage(new Managed() {
            @Override
            public void start() {
            }

            @Override
            public void stop() {
                try {
                    channel.close();

                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
                try {
                    connection.close();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            }
        });
        return client;
    }

    /**
     * Build for standalone application.
     */
    public MessageQueueClient build() throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(getHost());
        factory.setPort(getPort());
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(getQueueName(), true, false, false, null);
        MessageQueueClient client = new MessageQueueClient(channel, getQueueName());

        return client;
    }

}