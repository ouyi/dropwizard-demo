package org.bitbucket.ouyi;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static java.nio.file.Files.copy;

/**
 * Created by worker on 12/16/16.
 */
@Path("/upload")
public class UploadResource {

    private String uploadRootDir;

    public UploadResource(String uploadRootDir) {
        this.uploadRootDir = uploadRootDir;
    }

    @PUT
    @Path("{target}")
    public Response upload(@Context HttpServletRequest request, @PathParam("target") String target) throws IOException {
        streamToFile(request.getInputStream(), Paths.get(uploadRootDir, target));
        return Response.ok().build();
    }

    protected void streamToFile(InputStream inputStream, java.nio.file.Path targetPath) throws IOException {
        try (InputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
            copy(bufferedInputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
