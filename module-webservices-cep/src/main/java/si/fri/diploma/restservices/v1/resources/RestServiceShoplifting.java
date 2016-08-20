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

import si.fri.diploma.listeners.ShopliftingListener;
import si.fri.diploma.models.StolenProductObject;

@RequestScoped
@Path("stolenproducts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RestServiceShoplifting {

	@Inject
    private ShopliftingListener shopListener;

    @GET
    public Response getStolenProducts(){

    	List<StolenProductObject> stolenProducts = shopListener.getStolenProducts();
    	
        return Response.ok().entity(stolenProducts).build();
    }
	
}
