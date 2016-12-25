package org.bitbucket.ouyi.mq;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rabbitmq.client.BuiltinExchangeType;
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

    @NotEmpty
    private String exchangeName = "filename_exchange";

    private String exchangeType = BuiltinExchangeType.DIRECT.toString();

    private boolean exchangeDurable = true;

    @NotEmpty
    private String routingKey = "filename_key";

    private boolean queueDurable = true;

    private boolean isQueueExclusive = false;

    private boolean isQueueAutoDelete = false;

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
    public String getExchangeName() {
        return exchangeName;
    }

    @JsonProperty
    public String getRoutingKey() {
        return routingKey;
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

        channel.exchangeDeclare(getExchangeName(), exchangeType, exchangeDurable);
        String queueName = channel.queueDeclare("", queueDurable, isQueueExclusive, isQueueAutoDelete, null).getQueue();
        System.out.println("queueName: " + queueName);
        channel.queueBind(queueName, getExchangeName(), getRoutingKey());

        MessageQueueClient client = new MessageQueueClient(channel, getExchangeName(), getRoutingKey());

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

}