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

import si.fri.diploma.ShopliftingProducer;
import si.fri.diploma.models.ExitEvent;
import si.fri.diploma.models.PaidEvent;
import si.fri.diploma.models.ShelfEvent;

@RequestScoped
@Path("shoplifting")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RestServiceShoplifting {

	@Inject
    private ShopliftingProducer shopProducer;
	
	@Context 
	private UriInfo uriInfo;

	@Path("/shelf")
    @POST
    public Response createShelfEvent(ShelfEvent event) {

		shopProducer.sendRestShelfEvent(event);

        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(Integer.toString(event.getSerialNum()));
        
        return Response.created(builder.build()).entity(event).build();
    }
	
	@Path("/paid")
    @POST
    public Response createPaidEvent(PaidEvent event) {

		shopProducer.sendRestPaidEvent(event);

        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(Integer.toString(event.getSerialNum()));
        
        return Response.created(builder.build()).entity(event).build();
    }
	
	@Path("/exit")
    @POST
    public Response createExitEvent(ExitEvent event) {

		shopProducer.sendRestExitEvent(event);

        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(Integer.toString(event.getSerialNum()));
        
        return Response.created(builder.build()).entity(event).build();
    }
	
}
