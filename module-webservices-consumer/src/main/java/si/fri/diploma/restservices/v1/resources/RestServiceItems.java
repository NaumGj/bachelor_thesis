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

import si.fri.diploma.Consumer;
import si.fri.diploma.models.TestEvent;

@RequestScoped
@Path("events")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RestServiceItems {

	@Inject
    private Consumer eventsConsumer;


    @GET
    public Response getEvents(){

        List<TestEvent> events = eventsConsumer.getAndRemoveEvents();

        return Response.ok().entity(events).build();

    }
	
}
