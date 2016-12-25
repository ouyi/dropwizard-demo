package org.bitbucket.ouyi.api;

import org.bitbucket.ouyi.io.FileStorage;
import org.bitbucket.ouyi.mq.MessageQueueClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static java.nio.file.Files.copy;

/**
 * Created by worker on 12/16/16.
 */
@Path("/upload")
public class UploadResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadResource.class);
    private FileStorage fileStorage;
    private MessageQueueClient messageQueueClient;

    public UploadResource(FileStorage fileStorage, MessageQueueClient messageQueueClient) {
        this.fileStorage = fileStorage;
        this.messageQueueClient = messageQueueClient;
    }

    @PUT
    @Path("{target}")
    public Response upload(@Context HttpServletRequest request, @PathParam("target") String target) throws IOException {
        LOGGER.debug("Uploading to target: " + target);
        fileStorage.writeTo(request.getInputStream(), target);
        LOGGER.info("Uploaded to target: " + target);
        messageQueueClient.publish(target);
        return Response.ok().build();
    }


}
