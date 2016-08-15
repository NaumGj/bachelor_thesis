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

import si.fri.diploma.listeners.CountListener;
import si.fri.diploma.models.CountsObject;

@RequestScoped
@Path("counts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RestServiceCounts {

	@Inject
    private CountListener countsListener;

    @GET
    public Response getCounts(){

    	List<CountsObject> counts = countsListener.getCounts();
    	
        return Response.ok().entity(counts).build();
    }
	
}
