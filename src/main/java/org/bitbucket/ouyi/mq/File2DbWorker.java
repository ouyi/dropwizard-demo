package org.bitbucket.ouyi.mq;

/**
 * Created by worker on 12/26/16.
 */
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

public class File2DbWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageQueueFactory.class);

    public static void main(String[] argv) throws Exception {

        WorkQueueSubscriber subscriber = new MessageQueueFactory().createSubscriber();
        Channel channel = subscriber.getChannel();

        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:8080/transform");

        Consumer consumer = createConsumer(channel, webTarget);

        subscriber.subscribe(consumer);
    }

    private static Consumer createConsumer(final Channel channel, final WebTarget webTarget) {
        return new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    LOGGER.info("Received '" + message + "'");

                    webTarget.path(message).request().post(Entity.entity(null, MediaType.WILDCARD_TYPE));
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            };
    }
}