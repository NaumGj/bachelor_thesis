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

import si.fri.diploma.listeners.LatencyListener;
import si.fri.diploma.models.LatencyObject;

@RequestScoped
@Path("latency")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RestServiceLatency {

	@Inject
    private LatencyListener latencyListener;

    @GET
    public Response getLatency(){

        List<LatencyObject> latency = latencyListener.getAvgLatencies();
    	
        return Response.ok().entity(latency).build();
    }
	
}
