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
    @JsonProperty
    private String host = "localhost";

    @Min(1)
    @Max(65535)
    @JsonProperty
    private int port = 5672;

    @NotEmpty
    @JsonProperty
    private String exchangeName = "filename_exchange";

    @JsonProperty
    private String exchangeType = BuiltinExchangeType.DIRECT.toString().toLowerCase();

    @JsonProperty
    private boolean exchangeDurable = true;

    @NotEmpty
    @JsonProperty
    private String routingKey = "filename_key";

    @JsonProperty
    private boolean queueDurable = true;

    @JsonProperty
    private boolean isQueueExclusive = false;

    @JsonProperty
    private boolean isQueueAutoDelete = false;

    @JsonProperty
    private String queueName = "filename_queue";

    @JsonProperty
    private boolean isAutoAck = false;

    @JsonProperty
    private int prefetchCount = 1;

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    /**
     * Build with lifecycle hooks set up, if the parameter environment is not null.
     *
     * @param environment optional
     */
    public WorkQueuePublisher createPublisher(Environment environment) throws IOException, TimeoutException {
        WorkQueue workQueue = createWorkQueue();
        environment.lifecycle().manage(new Managed() {
            @Override
            public void start() {
            }
            @Override
            public void stop() {
                try {
                    workQueue.getChannel().close();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
                try {
                    workQueue.getConnection().close();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            }
        });
        return new WorkQueuePublisher(workQueue.getChannel(), workQueue.getExchangeName(), workQueue.getRoutingKey());
    }

    public WorkQueueSubscriber createSubscriber() throws IOException, TimeoutException {
        WorkQueue workQueue = createWorkQueue();
        workQueue.getChannel().basicQos(prefetchCount);
        return new WorkQueueSubscriber(workQueue.getChannel(), workQueue.getQueueName(), workQueue.getRoutingKey(), isAutoAck);
    }

    public WorkQueue createWorkQueue() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(getHost());
        factory.setPort(getPort());
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(getExchangeName(), exchangeType, exchangeDurable);
        String generatedQueueName = channel.queueDeclare(queueName, queueDurable, isQueueExclusive, isQueueAutoDelete, null).getQueue();
        channel.queueBind(generatedQueueName, getExchangeName(), getRoutingKey());
        LOGGER.info("Queue {} bound to exchange {} with routing key {}", generatedQueueName, getExchangeName(), getRoutingKey());

        return new WorkQueue(connection, channel, getExchangeName(), generatedQueueName, getRoutingKey());
    }

}