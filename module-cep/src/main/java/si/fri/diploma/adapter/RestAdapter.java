package si.fri.diploma.adapter;


import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import si.fri.diploma.ServiceRegistry;
import si.fri.diploma.listeners.TestListener;
import si.fri.diploma.models.TestEvent;
import si.fri.diploma.statements.TestStatement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This producer will send a bunch of messages to topic "fast-messages". Every so often,
 * it will send a message to "slow-messages". This shows how messages can be sent to
 * multiple topics. On the receiving end, we will see both kinds of messages but will
 * also see how the two topics aren't really synchronized.
 */

@ApplicationScoped
public class RestAdapter {

	@Inject
    private ServiceRegistry services;
	
	public static final Logger LOG = Logger.getLogger(RestAdapter.class.getName());
	
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	private EPServiceProvider epService; 
	
    public void init( @Observes @Initialized( ApplicationScoped.class ) Object init ) {
    	LOG.log(Level.INFO, "Initializing Esper engine");
    	initEsper();
    	LOG.log(Level.INFO, "Initializing timer");
        initTimer();
    }
    
    private class AdapterRunnable implements Runnable {
    	
    	private Client client;
    	ObjectMapper mapper;

    	public AdapterRunnable() {
    		mapper = new ObjectMapper();
    		ClientConfig configuration = new ClientConfig();
    		configuration = configuration.property(ClientProperties.CONNECT_TIMEOUT, 1500);
    		configuration = configuration.property(ClientProperties.READ_TIMEOUT, 1500);
    		this.client = ClientBuilder.newClient(configuration);
//    		for(int i = 0; i < 3; i++) {
//	    		try {
//	    			String url = "http://10.16.0.5:8080/events";
//	    			WebTarget itemsService = client.target(url);
//	    			System.out.println("URI: " + itemsService.getUri());
//	
////	    			Invocation.Builder request = itemsService.request("application/json");
////	    			request = request.property(ClientProperties.CONNECT_TIMEOUT, 1500);
////	    			request = request.property(ClientProperties.READ_TIMEOUT, 1500);
////	    			System.out.println("Invocation builder: " + request.toString());
//	    			Response response = itemsService.request("application/json").get();
//	    			LOG.log(Level.INFO, "Success or timeout");
//	    			//            System.out.println("ENTITY: " + response.getEntity());
//	    			if(response != null) {
//	    				String output = response.readEntity(String.class);
//	    				LOG.log(Level.INFO, "Answer: " + output);
//	    			} else {
//	    				System.out.println("Response is null!");
//	    			}
//	    		} catch (Exception e) {
//	    			System.out.println("EXCEPTION: " + e.getMessage());
//	    		}
//    		}
    		//        	this.client.property(ClientProperties.CONNECT_TIMEOUT, 10000);
    		//        	this.client.property(ClientProperties.READ_TIMEOUT, 10000);

    		//        	System.out.println("ADAPTER RUNNABLE!!!");
    		//
    		//			ArrayList<TestEvent> list = new ArrayList<TestEvent>();
    		//			TestEvent event = new TestEvent();
    		//			event.setType("test");
    		//			event.setT("1");
    		//			event.setK("1");
    		//			list.add(event);
    		//			GenericEntity<List<TestEvent>> entity = new GenericEntity<List<TestEvent>>(list) {};
    		//			System.out.println("ADAPTER RUNNABLE 2!!!");
    		//			Response response = Response.ok(entity).build();
    		//			System.out.println("ADAPTER RUNNABLE 3!!!");
    		//			System.out.println("Entity: " + response.getEntity());
    		//			System.out.println("ADAPTER RUNNABLE 4!!!");
    		//			System.out.println("Entity tag: " + response.getEntityTag());
    		//			String output = response.readEntity(String.class);
    		//        	LOG.log(Level.INFO, "Answer: " + output);
    		//        	List<TestEvent> events = response.readEntity(new GenericType<List<TestEvent>>() {});
    		//        	System.out.println(events);
    	}

		public void run() {
        		LOG.log(Level.INFO, "Up and running!");
//        		LOG.log(Level.INFO, services.discoverServiceURI("consumer").toString());
        		List<String> uris = services.discoverServiceURI("consumer");
//        		this.itemsService = client.target("http://10.16.0.6:");
//    			System.out.println("URI: " + this.itemsService.getUri());
//    			
//    			Response response = itemsService.request("application/json").get();
//                LOG.log(Level.INFO, "Success or timeout");
//                String output = response.readEntity(String.class);
//                LOG.log(Level.INFO, "Answer: " + output);
//                List<TestEvent> events = response.readEntity(new GenericType<List<TestEvent>>() {});
//                System.out.println(events);
        		for(String uri : uris) {
        			try {
        				String url = "http://" + services.getUrl("consumer", uri) + ":8080/events";
        				System.out.println(url);
        				WebTarget itemsService = client.target(url);
        				System.out.println("URI: " + itemsService.getUri());
        				
            			itemsService.request("application/json").async()
            					.get(new InvocationCallback<String>() {
            	                    @Override
            	                    public void completed(String answer) {
            	                        // on complete
            	                    	LOG.log(Level.INFO, "ANSWER: " + answer);
            	                    	List<TestEvent> events = null;
										try {
											events = Arrays.asList(mapper.readValue(answer, TestEvent[].class));
										} catch (JsonParseException e) {
											System.out.println("JsonParseException in async request: " + e.getMessage());
										} catch (JsonMappingException e) {
											System.out.println("JsonMappingException in async request: " + e.getMessage());
										} catch (IOException e) {
											System.out.println("IOException in async request: " + e.getMessage());
										}
            	                       	System.out.println(events);
            	                       	for(TestEvent event : events) {
            	                       		System.out.println("Sending " + event.getK());
            	                       		epService.getEPRuntime().sendEvent(event);
            	                       	}
            	                    }

            	                    @Override
            	                    public void failed(Throwable throwable) {
            	                        // on fail
            	                    	System.out.println("FAILED: " + throwable.getMessage());
            	                    }
            	                });
            			LOG.log(Level.INFO, "Success or timeout");
//                        System.out.println("ENTITY: " + response.getEntity());
//                       	List<TestEvent> output = response.readEntity(new GenericType<List<TestEvent>>() {});
//                       	String output = response.readEntity(String.class);
//                       	LOG.log(Level.INFO, "ANSWER: " + output);
//                       	for(TestEvent event : output) {
//                       		System.out.println(event.getK());
//                       	}
        			} catch (Exception e) {
        				System.out.println("EXCEPTION (PROBABLY TIMEOUT): " + e.getMessage());
        			} 
        			
//                    System.out.println("ENTITY: " + response.getEntity());
//                    List<TestEvent> events = response.readEntity(new GenericType<List<TestEvent>>() {});
//                    System.out.println(events);
        		}
//        		LOG.log(Level.INFO, itemsService.getUri().toString());
//        		LOG.log(Level.INFO, itemsService.toString());
//        		LOG.log(Level.INFO, itemsService.request("application/json").toString());
                
//                LOG.log(Level.INFO, response.toString());
//                LOG.log(Level.INFO, Boolean.toString(response.hasEntity()));
//                LOG.log(Level.INFO, "ENTITY CLASS: " + response.getEntity().toString());
//                LOG.log(Level.INFO, "ENTITY TAG: " + response.getEntityTag().toString());
//                LOG.log(Level.INFO, "RESPONSE LENGTH: " + String.valueOf(response.getLength()));
//                LOG.log(Level.INFO, response.getHeaderString("Content-Type"));
               

//                Events events = response.readEntity(Events.class);
//                List<EventGroup> gl = events.getEventGroups();
        }
    }
    
    public void initTimer() {
        AdapterRunnable runnable = new AdapterRunnable();

        LOG.log(Level.INFO, "Scheduling runnable");
         //use the handle if you need to cancel scheduler or something else
        final ScheduledFuture<?> runnableHandle = scheduler.scheduleAtFixedRate(runnable, 5, 5, TimeUnit.SECONDS);
    }
    
    public void initEsper() {
    	// Configure engine with event names to make the statements more readable.
        // This could also be done in a configuration file.
        Configuration configuration = new Configuration();
        configuration.addEventType("TestEvent", TestEvent.class.getName());

        // Get engine instance
        epService = EPServiceProviderManager.getProvider("RestAdapter", configuration);
        
        // Set up statements
        TestStatement testStmt = new TestStatement(epService.getEPAdministrator());
        testStmt.addListener(new TestListener());
        LOG.log(Level.INFO, "Esper engine initialized");
    }
    
}
