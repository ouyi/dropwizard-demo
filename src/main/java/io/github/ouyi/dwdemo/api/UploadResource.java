package io.github.ouyi.dwdemo.api;

import io.github.ouyi.dwdemo.io.FileStorage;
import io.github.ouyi.dwdemo.mq.WorkQueuePublisher;
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
 * Exposes the endpoint for file upload.
 */
@Path("/upload")
public class UploadResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadResource.class);
    private FileStorage fileStorage;
    private WorkQueuePublisher publisher;

    public UploadResource(FileStorage fileStorage, WorkQueuePublisher publisher) {
        this.fileStorage = fileStorage;
        this.publisher = publisher;
    }

    @PUT
    @Path("{target}")
    public Response upload(@Context HttpServletRequest request, @PathParam("target") String target) throws IOException {
        LOGGER.debug("Uploading to target: " + target);
        fileStorage.writeTo(request.getInputStream(), target);
        LOGGER.info("Uploaded to target: " + target);
        publisher.publish(target);
        return Response.ok().build();
    }


}
