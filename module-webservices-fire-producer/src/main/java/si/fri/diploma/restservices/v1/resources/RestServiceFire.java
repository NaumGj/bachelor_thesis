package si.fri.diploma.restservices.v1.resources;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import si.fri.diploma.FireDetectionProducer;
import si.fri.diploma.models.SmokeSensorEvent;
import si.fri.diploma.models.TemperatureSensorEvent;

@RequestScoped
@Path("fire")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RestServiceFire {

	@Inject
    private FireDetectionProducer fireProducer;
	
	@Context 
	private UriInfo uriInfo;

	@Path("/temperature")
    @POST
    public Response createTemperatureEvent(TemperatureSensorEvent event) {

        fireProducer.sendRestTempEvent(event);

        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(Integer.toString(event.getSerialNum()));
        
        return Response.created(builder.build()).entity(event).build();
    }
	
	@Path("/smoke")
    @POST
    public Response createSmokeEvent(SmokeSensorEvent event) {

		fireProducer.sendRestSmokeEvent(event);

        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(Integer.toString(event.getSerialNum()));
        
        return Response.created(builder.build()).entity(event).build();
    }
	
}
