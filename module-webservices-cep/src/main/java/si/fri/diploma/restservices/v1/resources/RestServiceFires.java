package si.fri.diploma.restservices.v1.resources;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import si.fri.diploma.listeners.FireDetectionListener;
import si.fri.diploma.models.FireObject;

@RequestScoped
@Path("fires")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RestServiceFires {

	@Inject
    private FireDetectionListener fireListener;

    @GET
    public Response getFires(){

    	List<FireObject> fires = fireListener.getFires();
    	
        return Response.ok().entity(fires).build();
    }
	
}
