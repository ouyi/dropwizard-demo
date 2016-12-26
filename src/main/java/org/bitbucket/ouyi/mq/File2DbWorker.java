package org.bitbucket.ouyi.mq;

/**
 * Created by worker on 12/26/16.
 */
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class File2DbWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageQueueFactory.class);

    public static void main(String[] argv) throws Exception {

        WorkQueueSubscriber subscriber = new MessageQueueFactory().createSubscriber();

        LOGGER.info(" [*] Waiting for messages. To exit press CTRL+C");

        Channel channel = subscriber.getChannel();

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                LOGGER.info(" [x] Received '" + message + "'");
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        subscriber.subscribe(consumer);
    }
}