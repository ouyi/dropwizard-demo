package org.bitbucket.ouyi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static java.nio.file.Files.copy;

/**
 * Created by worker on 12/16/16.
 */
@Path("/upload")
public class UploadResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadResource.class);
    private final String uploadRootDir;

    public UploadResource(String uploadRootDir) {
        this.uploadRootDir = uploadRootDir;
    }

    @PUT
    @Path("{target}")
    public Response upload(@Context HttpServletRequest request, @PathParam("target") String target) throws IOException {
        LOGGER.debug("Processing request: " + request.toString());
        LOGGER.debug("Uploading to target: " + target);

        streamToFile(request.getInputStream(), Paths.get(uploadRootDir, target));
        return Response.ok().build();
    }

    protected void streamToFile(InputStream inputStream, java.nio.file.Path targetPath) throws IOException {
        try (InputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
            java.nio.file.Path temp = Files.createTempFile("upload", null);
            copy(bufferedInputStream, temp, StandardCopyOption.REPLACE_EXISTING);
            Files.move(temp, targetPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        }
    }
}
