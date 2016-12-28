package org.bitbucket.ouyi.mq;

/**
 * Created by worker on 12/26/16.
 */
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class File2DbWorker {

    @Parameter(names = {"--help", "-h"}, help = true)
    private boolean help;

    @Parameter(names = {"--config-file", "-c"}, required = true)
    private String configFile;

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageQueueFactory.class);

    public static void main(String[] argv) throws Exception {
        File2DbWorker worker = new File2DbWorker();
        JCommander commandLine = new JCommander(worker, argv);
        commandLine.setProgramName(File2DbWorker.class.getName());
        if (worker.help) {
            commandLine.usage();
            return;
        }
        worker.run();
    }

    private void run() throws IOException, TimeoutException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        File2DbWorkerConfig config = mapper.readValue(new File(configFile), File2DbWorkerConfig.class);

        WorkQueueSubscriber subscriber = config.getMessageQueueFactory().createSubscriber();
        Channel channel = subscriber.getChannel();

        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(config.getTransformerEndpoint());

        Consumer consumer = createConsumer(channel, webTarget, subscriber.isAutoAck());

        subscriber.subscribe(consumer);
    }

    private Consumer createConsumer(final Channel channel, final WebTarget webTarget, boolean autoAck) {
        return new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    LOGGER.info("Received '{}'", message);
                    webTarget.path(message).request().post(Entity.entity(null, MediaType.WILDCARD_TYPE));
                    LOGGER.info("Processed '{}'", message);
                    if (!autoAck) {
                        channel.basicAck(envelope.getDeliveryTag(), false);
                        LOGGER.debug("Ack'ed message: '{}' with tag: '{}'", message, envelope.getDeliveryTag());
                    }
                }
            };
    }
}