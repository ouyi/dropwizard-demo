package org.bitbucket.ouyi.api;

import org.bitbucket.ouyi.business.Transformer;
import org.bitbucket.ouyi.io.FileStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * Exposes the endpoint for file transformation.
 */
@Path("/transform")
public class TransformResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransformResource.class);
    private final FileStorage fileStorage;
    private final Transformer transformer;

    public TransformResource(FileStorage fileStorage, Transformer transformer) {
        this.fileStorage = fileStorage;
        this.transformer = transformer;
    }

    @POST
    @Path("{filename}")
    public Response transform(@PathParam("filename") String filename) throws Exception {
        LOGGER.debug("Transforming file: " + filename);
        this.transformer.transform(fileStorage.readLinesFrom(filename));
        LOGGER.info("Transformed file: " + filename);
        return Response.ok().build();
    }
}
