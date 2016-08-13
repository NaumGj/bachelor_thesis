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
import si.fri.diploma.listeners.LatencyListener;
import si.fri.diploma.listeners.TestListener;
import si.fri.diploma.models.TestEvent;
import si.fri.diploma.statements.LatencyStatement;
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
    	}

		public void run() {
    		LOG.log(Level.INFO, "Up and running!");
//        	LOG.log(Level.INFO, services.discoverServiceURI("consumer").toString());
    		List<String> uris = services.discoverServiceURI("consumer");
//    		this.itemsService = client.target("http://10.16.0.6:");
//			System.out.println("URI: " + this.itemsService.getUri());
//			
//			Response response = itemsService.request("application/json").get();
//            LOG.log(Level.INFO, "Success or timeout");
//            String output = response.readEntity(String.class);
//            LOG.log(Level.INFO, "Answer: " + output);
//            List<TestEvent> events = response.readEntity(new GenericType<List<TestEvent>>() {});
//            System.out.println(events);
    		for(String uri : uris) {
    			try {
    				String url = "http://" + services.getUrl("consumer", uri) + ":8080/events";
    				WebTarget itemsService = client.target(url);
    				LOG.log(Level.INFO, "URI: " + itemsService.getUri());

    				itemsService.request("application/json").async()
    				.get(new InvocationCallback<String>() {
    					@Override
    					public void completed(String answer) {
    						// on complete
//    						LOG.log(Level.INFO, "ANSWER: " + answer);
    						List<TestEvent> events = null;
    						try {
    							events = Arrays.asList(mapper.readValue(answer, TestEvent[].class));
    						} catch (JsonParseException e) {
    							LOG.log(Level.WARNING, "JsonParseException in async request. Reason: " + e.getMessage());
    						} catch (JsonMappingException e) {
    							LOG.log(Level.WARNING, "JsonMappingException in async request. Reason: " + e.getMessage());
    						} catch (IOException e) {
    							LOG.log(Level.WARNING, "IOException in async request. Reason: " + e.getMessage());
    						}
//    						LOG.log(Level.INFO, "The list of events: " + events.toString());
    						for(TestEvent event : events) {
    							if(Integer.parseInt(event.getSerialNum()) % 1000 == 0) {
    								LOG.log(Level.INFO, "ID: " + event.getSerialNum());
    							}
//    	                       	System.out.println("Sending " + event.getK());
    							if(event != null) {
    								epService.getEPRuntime().sendEvent(event);
    							}
    						}
    					}

    					@Override
    					public void failed(Throwable throwable) {
    						// on fail
    						LOG.log(Level.INFO, "Events request failed. Reason: " + throwable.getMessage());
    					}
    				});
//                        System.out.println("ENTITY: " + response.getEntity());
//                       	List<TestEvent> output = response.readEntity(new GenericType<List<TestEvent>>() {});
//                       	String output = response.readEntity(String.class);
//                       	LOG.log(Level.INFO, "ANSWER: " + output);
//                       	for(TestEvent event : output) {
//                       		System.out.println(event.getK());
//                       	}
    			} catch (Exception e) {
    				LOG.log(Level.WARNING, "Exception in RestAdapter runnable. Reason: " + e.getMessage());
    			} 
    		}
		}
    }
    
    public void initTimer() {
    	LOG.log(Level.INFO, "Scheduling the CEP adapter to poll events...");
        AdapterRunnable runnable = new AdapterRunnable();

        final ScheduledFuture<?> runnableHandle = scheduler.scheduleAtFixedRate(runnable, 5000, 500, TimeUnit.MILLISECONDS);
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
        
        LatencyStatement latencyStmt = new LatencyStatement(epService.getEPAdministrator());
        latencyStmt.addListener(new LatencyListener());
        LOG.log(Level.INFO, "Esper engine initialized");
    }
    
}
