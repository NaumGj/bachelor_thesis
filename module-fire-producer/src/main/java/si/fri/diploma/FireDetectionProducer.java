package si.fri.diploma;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import si.fri.diploma.models.SimpleEvent;
import si.fri.diploma.models.SmokeSensorEvent;
import si.fri.diploma.models.TemperatureSensorEvent;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

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
public class FireDetectionProducer {

	public static final Logger LOG = Logger.getLogger(FireDetectionProducer.class.getName());
	
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	private ScheduledFuture<?> runnableHandle;
	
	private static Random rand = new Random();
	
	private ArrayList<TemperatureSensorEvent> tempEvents = new ArrayList<TemperatureSensorEvent>();
	private ArrayList<SmokeSensorEvent> smokeEvents = new ArrayList<SmokeSensorEvent>();
	
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
    	LOG.log(Level.INFO, "Scheduling the Kafka fire events producer...");
    	ProductionRunnable runnable = new ProductionRunnable();

        runnableHandle = scheduler.schedule(runnable, 5, TimeUnit.SECONDS);
    }
    
    private class ProductionRunnable implements Runnable{

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
            
            LOG.log(Level.INFO, "The Kafka producer is starting production of fire events");
            try {
                for (int i = 0; i < Integer.MAX_VALUE; i++) {
//                	busyWait();
                	Thread.sleep(100);
                	
                	if(tempEvents.size() > 0) {
                		for(TemperatureSensorEvent tempEvent : tempEvents) {
                			addPropertiesToEvent(tempEvent, i);
                			producer.send(new ProducerRecord<String, String>("fast-messages", mapper.writeValueAsString(tempEvent)));
                		}
                		tempEvents.clear();
                	} else {
	                	TemperatureSensorEvent tempEvent = createTemperatureEvent(i);
//	                	LOG.log(Level.INFO, mapper.writeValueAsString(event));
//	                	if(i % 100 == 0) {
//	                		tempEvent.setCelsiusTemperature(60.0);
//	                		tempEvent.setRoomNumber(3);
//	                	}
	                	producer.send(new ProducerRecord<String, String>("fast-messages", mapper.writeValueAsString(tempEvent)));
                	}
                	
                	if(smokeEvents.size() > 0) {
                		for(SmokeSensorEvent smokeEvent : smokeEvents) {
                			addPropertiesToEvent(smokeEvent, i);
                			producer.send(new ProducerRecord<String, String>("fast-messages", mapper.writeValueAsString(smokeEvent)));
                		}
                		smokeEvents.clear();
                	} else {
	                	SmokeSensorEvent smokeEvent = createSmokeEvent(i);
//	                	if(i % 100 == 0) {
//	                		smokeEvent.setObscuration(100.0);
//	                		smokeEvent.setRoomNumber(3);
//	                	}
	                	producer.send(new ProducerRecord<String, String>("fast-messages", mapper.writeValueAsString(smokeEvent)));
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
     * Create a temperature event.
     * 
     * @param serialNum	serial number of the event
     * @return
     */
    public TemperatureSensorEvent createTemperatureEvent(Integer serialNum) {
    	TemperatureSensorEvent event = new TemperatureSensorEvent();
    	event.setType("temperature");
    	event.setSerialNum(serialNum);
    	event.setSensorId(rand.nextInt(serialNum % 100 + 1));	// 100 sensors
    	event.setRoomNumber(rand.nextInt(10) + 1);	// 10 rooms
    	event.setCelsiusTemperature(10 * rand.nextDouble() + 20);	// 20 - 30 degrees
    	event.setTimestamp(System.currentTimeMillis());
    	event.setTimestampConsumed((long)0);
    	return event;
    }
    
    /**
     * Create a smoke event.
     * 
     * @param serialNum	serial number of the event
     * @return
     */
    public SmokeSensorEvent createSmokeEvent(Integer serialNum) {
    	SmokeSensorEvent event = new SmokeSensorEvent();
    	event.setType("smoke");
    	event.setSerialNum(serialNum);
    	event.setSensorId(rand.nextInt(serialNum % 100 + 1));	// 100 sensors
    	event.setRoomNumber(rand.nextInt(10) + 1);	// 10 rooms
    	event.setObscuration(5 * rand.nextDouble());	// 0-5 % obscuration/meter
    	event.setTimestamp(System.currentTimeMillis());
    	event.setTimestampConsumed((long)0);
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
    
    /**
     * Busy wait for 1 microsecond.
     */
    public void busyWait(){
        final long INTERVAL = 1000;
        long start = System.nanoTime();
        long end = 0;
        do {
            end = System.nanoTime();
        } while(start + INTERVAL >= end);
    }
    
    public void sendRestTempEvent(TemperatureSensorEvent event) {
    	tempEvents.add(event);
    }
    
    public void sendRestSmokeEvent(SmokeSensorEvent event) {
    	smokeEvents.add(event);
    }
    
    public void addPropertiesToEvent(SimpleEvent event, Integer serialNum) {
    	event.setSerialNum(serialNum);
    	event.setTimestamp(System.currentTimeMillis());
    	if(event instanceof TemperatureSensorEvent) {
    		((TemperatureSensorEvent)event).setSensorId(rand.nextInt(serialNum % 100 + 1));
    	} else if(event instanceof SmokeSensorEvent) {
    		((SmokeSensorEvent)event).setSensorId(rand.nextInt(serialNum % 100 + 1));
    	}
    }

}
