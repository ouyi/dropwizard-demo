package org.bitbucket.ouyi.mq;

import com.rabbitmq.client.Channel;

import java.io.IOException;

/**
 * Created by worker on 12/25/16.
 */
public class MessageQueueClient {

    private final Channel channel;
    private final String routingKey;
    private final String exchangeName;

    public MessageQueueClient(Channel channel, String exchangeName, String routingKey) throws IOException {
        this.channel = channel;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
    }

    public synchronized void publish(String message) throws IOException {
        channel.basicPublish(exchangeName, routingKey, null, message.getBytes("UTF-8"));
    }
}
