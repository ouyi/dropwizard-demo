package org.bitbucket.ouyi.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;

/**
 * Created by worker on 12/25/16.
 */
public class WorkQueueSubscriber {

    private final Channel channel;
    private final String queueName;
    private final String routingKey;
    private final boolean autoAck;

    public WorkQueueSubscriber(Channel channel, String queueName, String routingKey, boolean autoAck) {
        this.channel = channel;
        this.queueName = queueName;
        this.routingKey = routingKey;
        this.autoAck = autoAck;
    }

    public void subscribe(Consumer consumer) throws IOException {
        channel.basicConsume(queueName, autoAck, consumer);
    }

    public Channel getChannel() {
        return channel;
    }

    public boolean isAutoAck() {
        return autoAck;
    }
}
