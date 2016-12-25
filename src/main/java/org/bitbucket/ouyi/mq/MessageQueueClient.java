package org.bitbucket.ouyi.mq;

import com.rabbitmq.client.Channel;

import java.io.IOException;

/**
 * Created by worker on 12/25/16.
 */
public class MessageQueueClient {

    private final Channel channel;
    private final String queueName;

    public MessageQueueClient(Channel channel, String queueName) throws IOException {
        this.channel = channel;
        this.queueName = queueName;
    }

    public void publish(String message) throws IOException {
        channel.basicPublish("", queueName, null, message.getBytes("UTF-8"));
    }
}
