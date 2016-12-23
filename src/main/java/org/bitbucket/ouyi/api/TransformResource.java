package org.bitbucket.ouyi.api;

import org.bitbucket.ouyi.business.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by worker on 12/20/16.
 */
@Path("/transform")
public class TransformResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransformResource.class);
    private final String uploadRootDir;
    private final Transformer transformer;

    public TransformResource(String uploadRootDir, Transformer transformer) {
        this.uploadRootDir = uploadRootDir;
        this.transformer = transformer;
    }

    @POST
    public Response transform(@QueryParam("filename") String filename) throws Exception {
        LOGGER.debug("Transforming file: " + filename);
        this.transformer.transform(Files.lines(Paths.get(uploadRootDir, filename)));
        LOGGER.info("Transformed file: " + filename);
        return Response.ok().build();
    }
}
