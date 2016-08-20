package si.fri.diploma;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import si.fri.diploma.models.ExitEvent;
import si.fri.diploma.models.PaidEvent;
import si.fri.diploma.models.ShelfEvent;
import si.fri.diploma.models.SimpleEvent;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class ShopliftingProducer {

	public static final Logger LOG = Logger.getLogger(ShopliftingProducer.class.getName());
	
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	private ScheduledFuture<?> runnableHandle;
	
	private ArrayList<ShelfEvent> shelfEvents = new ArrayList<ShelfEvent>();
	private ArrayList<PaidEvent> paidEvents = new ArrayList<PaidEvent>();
	private ArrayList<ExitEvent> exitEvents = new ArrayList<ExitEvent>();
	
	/**
	 * Initialize production of events.
	 * @param init
	 */
    public void init( @Observes @Initialized( ApplicationScoped.class ) Object init ) {
        initTimer();
    }
    
    /**
     * Destroy runnable handle and scheduler.
     */
    @PreDestroy
    public void destroyScheduler() {
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
    }
    
    /**
     * Schedule production of events.
     */
    public void initTimer() {
    	LOG.log(Level.INFO, "Scheduling the Kafka shoplifting events producer...");
    	ProductionRunnable runnable = new ProductionRunnable();

        runnableHandle = scheduler.schedule(runnable, 5, TimeUnit.SECONDS);
    }
    
    private class ProductionRunnable implements Runnable {

        public ProductionRunnable() {}

        public void run() {
            LOG.log(Level.INFO, "Setting up the Kafka producer...");
            
            // set up the producer
            KafkaProducer<String, String> producer = null;
            try (InputStream props = Resources.getResource("producer.props").openStream()) {
                Properties properties = new Properties();
                properties.load(props);
                producer = new KafkaProducer<>(properties);
            } catch (IOException e) {
            	LOG.log(Level.SEVERE, "IO Exception during the set up of the Kafka producer!");
    		}

            ObjectMapper mapper = new ObjectMapper();
            
            LOG.log(Level.INFO, "The Kafka producer is starting production of shop events");
            try {
                for (int i = 0; i < Integer.MAX_VALUE; i++) {
                	
                	if(shelfEvents.size() > 0 || paidEvents.size() > 0 || exitEvents.size() > 0) {
                		for(ShelfEvent shelfEvent : shelfEvents) {
                			addPropertiesToEvent(shelfEvent, i);
                			producer.send(new ProducerRecord<String, String>("fast-messages", mapper.writeValueAsString(shelfEvent)));
                		}
                		shelfEvents.clear();

                		Thread.sleep(500);
                		
                		for(PaidEvent paidEvent : paidEvents) {
                			addPropertiesToEvent(paidEvent, i);
                			producer.send(new ProducerRecord<String, String>("fast-messages", mapper.writeValueAsString(paidEvent)));
                		}
                		paidEvents.clear();
                		
                		Thread.sleep(500);
                		
                		for(ExitEvent exitEvent : exitEvents) {
                			addPropertiesToEvent(exitEvent, i);
                			producer.send(new ProducerRecord<String, String>("fast-messages", mapper.writeValueAsString(exitEvent)));
                		}
                		exitEvents.clear();
                		
                		Thread.sleep(500);
                		
                	} else {
                		ShelfEvent shelfEvent = createShelfEvent(i);
	                	producer.send(new ProducerRecord<String, String>("fast-messages", mapper.writeValueAsString(shelfEvent)));
                	
	                	Thread.sleep(500);
                	
                		PaidEvent paidEvent = createPaidEvent(i);
	                	producer.send(new ProducerRecord<String, String>("fast-messages", mapper.writeValueAsString(paidEvent)));
                	
	                	Thread.sleep(500);
                	
                		ExitEvent exitEvent = createExitEvent(i);
	                	producer.send(new ProducerRecord<String, String>("fast-messages", mapper.writeValueAsString(exitEvent)));
	                	
	                	Thread.sleep(500);
                	}
                    
                	if (i % 1000 == 0) {
                		SimpleEvent markerEvent = createMarkerEvent(i);
                    	producer.send(new ProducerRecord<String, String>("fast-messages", mapper.writeValueAsString(markerEvent)));
                        producer.flush();
                       	LOG.log(Level.INFO, "Sent message number " + i);
                    }
                }
            } catch (Exception e) {
            	LOG.log(Level.SEVERE, "Exception in Kafka producer while sending events! Reason: " + e.getMessage());
            } finally {
                producer.close();
            }
        }
    }
    
    /**
     * Create a shelf event.
     * 
     * @param serialNum	serial number of the event
     * @return
     */
    public ShelfEvent createShelfEvent(Integer serialNum) {
    	ShelfEvent event = new ShelfEvent();
    	event.setType("shelf");
    	event.setSerialNum(serialNum);
    	event.setTimestamp(System.currentTimeMillis());
    	event.setTimestampConsumed((long)0);
    	event.setProductId(Integer.toString(serialNum));
    	return event;
    }
    
    /**
     * Create a paid event.
     * 
     * @param serialNum	serial number of the event
     * @return
     */
    public PaidEvent createPaidEvent(Integer serialNum) {
    	PaidEvent event = new PaidEvent();
    	event.setType("paid");
    	event.setSerialNum(serialNum);
    	event.setTimestamp(System.currentTimeMillis());
    	event.setTimestampConsumed((long)0);
    	event.setProductId(Integer.toString(serialNum));
    	return event;
    }
    
    /**
     * Create an exit event.
     * 
     * @param serialNum	serial number of the event
     * @return
     */
    public ExitEvent createExitEvent(Integer serialNum) {
    	ExitEvent event = new ExitEvent();
    	event.setType("exit");
    	event.setSerialNum(serialNum);
    	event.setTimestamp(System.currentTimeMillis());
    	event.setTimestampConsumed((long)0);
    	event.setProductId(Integer.toString(serialNum));
    	return event;
    }

    /**
     * Create a marker event.
     * 
     * @param serialNum	serial number of the event
     * @return
     */
    public SimpleEvent createMarkerEvent(Integer serialNum) {
    	SimpleEvent event = new SimpleEvent();
    	event.setType("marker");
    	event.setSerialNum(serialNum);
    	event.setTimestamp(System.currentTimeMillis());
    	event.setTimestampConsumed((long)0);
    	return event;
    }
    
    public void sendRestShelfEvent(ShelfEvent event) {
    	shelfEvents.add(event);
    }
    
    public void sendRestPaidEvent(PaidEvent event) {
    	paidEvents.add(event);
    }
    
    public void sendRestExitEvent(ExitEvent event) {
    	exitEvents.add(event);
    }
    
    public void addPropertiesToEvent(SimpleEvent event, Integer serialNum) {
    	event.setSerialNum(serialNum);
    	event.setTimestamp(System.currentTimeMillis());
    }

}
