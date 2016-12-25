package org.bitbucket.ouyi.api;

import org.bitbucket.ouyi.io.FileStorage;
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

    public UploadResource(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    @PUT
    @Path("{target}")
    public Response upload(@Context HttpServletRequest request, @PathParam("target") String target) throws IOException {
        LOGGER.debug("Uploading to target: " + target);
        fileStorage.writeTo(request.getInputStream(), target);
        LOGGER.info("Uploaded to target: " + target);
        return Response.ok().build();
    }


}
