package org.bitbucket.ouyi.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;

/**
 * Created by worker on 12/25/16.
 */
public class WorkQueuePublisher {

    private final Channel channel;
    private final String exchangeName;
    private final String routingKey;

    public WorkQueuePublisher(Channel channel, String exchangeName, String routingKey) {
        this.channel = channel;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
    }

    public synchronized void publish(String message) throws IOException {
        channel.basicPublish(exchangeName, routingKey, MessageProperties.PERSISTENT_BASIC, message.getBytes("UTF-8"));
    }
}
