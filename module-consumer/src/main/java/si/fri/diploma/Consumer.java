package si.fri.diploma;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import si.fri.diploma.models.SimpleEvent;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class Consumer {
	
	@Inject
	private ServiceRegistry services;

	private String serviceName = "consumer";
	private String endpointURI;

	public static final Logger LOG = Logger.getLogger(Consumer.class.getName());
	
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	private ScheduledFuture<?> runnableHandle;
	
	private ArrayList<SimpleEvent> consumedEvents = new ArrayList<SimpleEvent>();
	
	/**
	 * Initialize consumption of events.
	 * @param init
	 */
    public void init( @Observes @Initialized( ApplicationScoped.class ) Object init ) {
        initTimer();
    }

    /**
     * Registers the routes service endpoint with the service registry.
     */
    @PostConstruct
    public void registerService() {
    	endpointURI = System.getenv("MY_POD_IP");
    	LOG.log(Level.INFO, "HOSTNAME: " + System.getenv("HOSTNAME"));
    	LOG.log(Level.INFO, "POD's IP: " + System.getenv("MY_POD_IP"));
    	LOG.log(Level.INFO, "Registering consumer service...");
        services.registerService(serviceName, endpointURI);
    }

    /**
     * Unregisters the routes service endpoint with the service registry and destroy runnable handle and scheduler.
     */
    @PreDestroy
    public void unregisterService() {
        LOG.log(Level.INFO, "Destroying scheduled handle and shutting down scheduler...");
    	try {
    		if(!runnableHandle.isCancelled()) {
    			runnableHandle.cancel(true);
    		}
    		if(!scheduler.isShutdown()) {
    			scheduler.shutdown();
    		}
    	} catch (Exception e) {
    		LOG.log(Level.WARNING, "Exception while destroying scheduled handle and shutting down scheduler!");
    	}
    	LOG.log(Level.INFO, "Unregistering consumer service...");
        services.unregisterService(serviceName, endpointURI);
    }
    
    /**
     * Schedule consumption of events.
     */
    public void initTimer() {
    	LOG.log(Level.INFO, "Scheduling the Kafka consumer...");
    	ConsumptionRunnable runnable = new ConsumptionRunnable();

        runnableHandle = scheduler.schedule(runnable, 5, TimeUnit.SECONDS);
    }
    
    private class ConsumptionRunnable implements Runnable {

        public ConsumptionRunnable() {}

        public void run() {
            LOG.log(Level.INFO, "started receiving events");
            
            //configure Kafka consumer
            KafkaConsumer<String, String> consumer = null;
            try (InputStream props = Resources.getResource("consumer.props").openStream()) {
                Properties properties = new Properties();
                properties.load(props);
                if (properties.getProperty("group.id") == null) {
                	LOG.log(Level.WARNING, "The group id is changed since it is not specified in the configuration file.");
                    properties.setProperty("group.id", "group-" + new Random().nextInt(100000));
                }
                consumer = new KafkaConsumer<>(properties);
            } catch (IOException e) {
            	LOG.log(Level.SEVERE, "IO Exception during the set up of the Kafka consumer!");
    		}
            consumer.subscribe(Arrays.asList("fast-messages"));
            
            int timeouts = 0;
            while (true) {
            	ConsumerRecords<String, String> records = consumer.poll(200);
                
            	if (records.count() == 0) {
                    timeouts++;
                } else {
                	LOG.log(Level.INFO, String.format("Got %d records after %d timeouts\n", records.count(), timeouts));
                    timeouts = 0;
                }
                
                ObjectMapper mapper = new ObjectMapper();
                
                for (ConsumerRecord<String, String> record : records) {
                    switch (record.topic()) {
                        case "fast-messages":
                        	JsonNode msg = null;
                        	try {
                        		msg = mapper.readTree(record.value());
                        	} catch (JsonProcessingException e) {
                        		LOG.log(Level.SEVERE, "JsonProcessingException while reading the record value");
                        	} catch (IOException e) {
                        		LOG.log(Level.SEVERE, "IO Exception while reading the record value");
                        	}
//                        	LOG.log(Level.INFO, msg.toString());
//                        	LOG.log(Level.INFO, msg.get("type").asText());
                        	SimpleEvent event = null;
                        	try {
    							event = mapper.treeToValue(msg, SimpleEvent.class);
    							LOG.log(Level.INFO, mapper.writeValueAsString(event));
    						} catch (JsonProcessingException e) {
    							LOG.log(Level.WARNING, "JsonProcessingException in consumer. Reason: " + e.getMessage());
    						}
							
							if(event != null) {
                            	event.setTimestampConsumed(System.currentTimeMillis());
                            	
                            	if("marker".equals(event.getType())) {
    								LOG.log(Level.INFO, "Received message number " + event.getSerialNum());
    							} 
   								consumedEvents.add(event);
                    		}
                        	
	                        break;
                        default:
                        	throw new IllegalStateException("It's not possible to get message on topic " + record.topic());
                    }
                }
            }
        }
    }
    
    /**
     * Return events to the CEP adapter through REST services and delete the events array.
     * @return List of consumed of events since last call of this function
     */
    public List<SimpleEvent> getAndRemoveEvents() {
    	ArrayList<SimpleEvent> eventsCopy = new ArrayList<>(consumedEvents);
    	consumedEvents.clear();
    	return eventsCopy;
    }

}
