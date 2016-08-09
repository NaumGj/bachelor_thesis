package si.fri.diploma;


import com.google.common.io.Resources;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
	
    public void init( @Observes @Initialized( ApplicationScoped.class ) Object init ) {
        initTimer();
    }
    
    private class MyRunnable implements Runnable{

        private Integer numberOfEvents;

        public MyRunnable(Integer numberOfEvents){
        	this.numberOfEvents = numberOfEvents;
        }


        public void run() {
            LOG.log(Level.INFO, "configuring...");
            
         // set up the producer
            KafkaProducer<String, String> producer = null;
            try (InputStream props = Resources.getResource("producer.props").openStream()) {
                Properties properties = new Properties();
                properties.load(props);
                producer = new KafkaProducer<>(properties);
            } catch (IOException e) {
//    			e.printStackTrace();
            	System.out.println("IO Exception!");
    		}

            LOG.log(Level.INFO, "starting production of events");
            try {
                for (int i = 0; i < this.numberOfEvents; i++) {
                	Thread.sleep(50);
//                	System.out.println(i);
                    // send lots of messages
                	producer.send(new ProducerRecord<String, String>(
                            "fast-messages", i % 6, null,
                            String.format("{\"type\":\"test\", \"t\":%.3f, \"k\":%d}", System.nanoTime() * 1e-9, i)));
//                    producer.send(new ProducerRecord<String, String>(
//                            "fast-messages",
//                            String.format("{\"type\":\"test\", \"t\":%.3f, \"k\":%d}", System.nanoTime() * 1e-9, i)));
//                    System.out.println(i);
                    // every so often send to a different topic
                    if (i % 1000 == 0) {
                        producer.send(new ProducerRecord<String, String>(
                                "fast-messages",
                                String.format("{\"type\":\"marker\", \"t\":%.3f, \"k\":%d}", System.nanoTime() * 1e-9, i)));
//                        producer.send(new ProducerRecord<String, String>(
//                                "summary-markers",
//                                String.format("{\"type\":\"other\", \"t\":%.3f, \"k\":%d}", System.nanoTime() * 1e-9, i)));
                        producer.flush();
                        System.out.println("Sent msg number " + i);
                    }
                }
            } catch (Throwable throwable) {
                System.out.printf("%s", throwable.getStackTrace());
            } finally {
                producer.close();
            }
        }
    }

    public void initTimer() {
    	MyRunnable runnable = new MyRunnable(100000000);

        //use the handle if you need to cancel scheduler or something else
        final ScheduledFuture<?> runnableHandle = scheduler.schedule(runnable, 5, TimeUnit.SECONDS);
    }

}
