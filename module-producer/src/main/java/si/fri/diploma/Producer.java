package si.fri.diploma;


import com.google.common.io.Resources;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

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

/**
 * This producer will send a bunch of messages to topic "fast-messages". Every so often,
 * it will send a message to "slow-messages". This shows how messages can be sent to
 * multiple topics. On the receiving end, we will see both kinds of messages but will
 * also see how the two topics aren't really synchronized.
 */

@ApplicationScoped
public class Producer {

	public static final Logger LOG = Logger.getLogger(Producer.class.getName());
	
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	private ScheduledFuture<?> runnableHandle;
	
    public void init( @Observes @Initialized( ApplicationScoped.class ) Object init ) {
        initTimer();
    }
    
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
    }
    
    private class MyRunnable implements Runnable{

        public MyRunnable() {}

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

            LOG.log(Level.INFO, "The Kafka producer is starting production of events");
            try {
                for (int i = 0; i < Integer.MAX_VALUE; i++) {
//                	busyWait();
//                	Thread.sleep(1);
//                	System.out.println(i);
                    // send lots of messages
                	producer.send(new ProducerRecord<String, String>(
                            "fast-messages", i % 6, null,
                            String.format("{\"type\":\"normal\", \"timestamp\":%.5f, \"serial_num\":%d}", System.currentTimeMillis() * 1e-3, i)));
//                    producer.send(new ProducerRecord<String, String>(
//                            "fast-messages",
//                            String.format("{\"type\":\"test\", \"t\":%.3f, \"k\":%d}", System.nanoTime() * 1e-9, i)));
//                    System.out.println(i);
                    if (i % 1000 == 0) {
                        producer.send(new ProducerRecord<String, String>(
                                "fast-messages",
                                String.format("{\"type\":\"marker\", \"timestamp\":%.5f, \"serial_num\":%d}", System.currentTimeMillis() * 1e-3, i)));
//                        producer.send(new ProducerRecord<String, String>(
//                                "summary-markers",
//                                String.format("{\"type\":\"other\", \"t\":%.3f, \"k\":%d}", System.nanoTime() * 1e-9, i)));
                        producer.flush();
                        if (i % 10000 == 0) {
                        	LOG.log(Level.INFO, "Sent msg number " + i);
                        }
                    }
                }
            } catch (Exception e) {
            	LOG.log(Level.SEVERE, "Exception in Kafka producer while sending events! Reason: " + e.getMessage());
            } finally {
                producer.close();
            }
        }
    }

    public void initTimer() {
    	LOG.log(Level.INFO, "Scheduling the Kafka producer...");
    	MyRunnable runnable = new MyRunnable();

        runnableHandle = scheduler.schedule(runnable, 5, TimeUnit.SECONDS);
    }
    
    public void busyWait(){
        final long INTERVAL = 1000;
        long start = System.nanoTime();
        long end = 0;
        do {
            end = System.nanoTime();
        } while(start + INTERVAL >= end);
//        System.out.println(end - start);
    }

}
