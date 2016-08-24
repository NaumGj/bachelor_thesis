package si.fri.diploma;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import si.fri.diploma.models.SimpleEvent;

import java.io.IOException;
import java.io.InputStream;
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
public class Producer {

	public static final Logger LOG = Logger.getLogger(Producer.class.getName());
	
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	private ScheduledFuture<?> runnableHandle;
	
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
    	LOG.log(Level.INFO, "Scheduling the Kafka producer...");
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
            
            LOG.log(Level.INFO, "The Kafka producer is starting production of events");
            try {
                for (int i = 0; i < Integer.MAX_VALUE; i++) {
//                	busyWait();
                	Thread.sleep(1);
                	
                	SimpleEvent event = createEvent("normal", i);
//                	LOG.log(Level.INFO, mapper.writeValueAsString(event));
                	producer.send(new ProducerRecord<String, String>("fast-messages", mapper.writeValueAsString(event)));
                    
                	if (i % 10000 == 0) {
                		SimpleEvent markerEvent = createEvent("marker", i);
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
     * Create a virtual IoT event.
     * 
     * @param type	the type of the event
     * @param serialNum	serial number of the event
     * @return
     */
    public SimpleEvent createEvent(String type, Integer serialNum) {
    	SimpleEvent event = new SimpleEvent();
    	event.setType(type);
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

}
