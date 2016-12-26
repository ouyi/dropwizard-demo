package org.bitbucket.ouyi.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;

/**
 * Created by worker on 12/25/16.
 */
public class WorkQueue {

    private final Connection connection;
    private final Channel channel;
    private final String exchangeName;
    private final String queueName;
    private final String routingKey;

    public WorkQueue(Connection connection, Channel channel, String exchangeName, String queueName, String routingKey) {
        this.connection = connection;
        this.channel = channel;
        this.exchangeName = exchangeName;
        this.queueName = queueName;
        this.routingKey = routingKey;
    }

    public Connection getConnection() {
        return connection;
    }

    public Channel getChannel() {
        return channel;
    }

    public synchronized void publish(String message) throws IOException {
        channel.basicPublish(exchangeName, routingKey, MessageProperties.PERSISTENT_BASIC, message.getBytes("UTF-8"));
    }

    public void subscribe(boolean autoAck, Consumer consumer) throws IOException {
        channel.basicConsume(queueName, autoAck, consumer);
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public String getQueueName() {
        return queueName;
    }
}
