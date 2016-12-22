package org.bitbucket.ouyi.api;

import org.bitbucket.ouyi.business.Transformer;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * Created by worker on 12/20/16.
 */
@Path("/transform")
public class TransformResource {

    private final Transformer transformer;

    public TransformResource(Transformer transformer) {
        this.transformer = transformer;
    }

    @POST
    public Response transform(@QueryParam("filename") String filename) throws Exception {
        this.transformer.transform(filename);
        return Response.ok().build();
    }
}
