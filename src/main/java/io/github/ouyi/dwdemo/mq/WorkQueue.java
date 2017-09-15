package io.github.ouyi.dwdemo.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

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
