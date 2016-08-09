package si.fri.diploma;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;

import org.HdrHistogram.Histogram;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import si.fri.diploma.models.TestEvent;

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

/**
 * This producer will send a bunch of messages to topic "fast-messages". Every so often,
 * it will send a message to "slow-messages". This shows how messages can be sent to
 * multiple topics. On the receiving end, we will see both kinds of messages but will
 * also see how the two topics aren't really synchronized.
 */

@ApplicationScoped
public class Consumer {
	
	@Inject
	private ServiceRegistry services;

	private String serviceName = "consumer";
	private String endpointURI;

	public static final Logger LOG = Logger.getLogger(Consumer.class.getName());
	
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	private ArrayList<TestEvent> events = new ArrayList<TestEvent>();
	
    public void init( @Observes @Initialized( ApplicationScoped.class ) Object init ) {
        initTimer();
    }

//    /**
//     * <p>Retrieves the routes service base URI from the 'BASE_URI' environment variable.</p>
//     */
//    public Consumer() {
//    	System.out.println("POD's IP in service: " + System.getenv("MY_POD_IP"));
//        endpointURI = System.getenv("MY_POD_IP");
//    }

    /**
     * <p>Registers the routes service endpoint with the service registry.</p>
     */
    @PostConstruct
    public void registerService() {
    	LOG.log(Level.INFO, "IP: " + System.getenv("MY_POD_IP"));
    	System.out.println("POD's IP in service: " + System.getenv("MY_POD_IP"));
        endpointURI = System.getenv("MY_POD_IP");
    	System.out.println("Register consumer service");
        services.registerService(serviceName, endpointURI);
    }

    /**
     * <p>Unregisters the routes service endpoint with the service registry.</p>
     */
    @PreDestroy
    public void unregisterService() {
    	System.out.println("Unregister consumer service");
        services.unregisterService(serviceName, endpointURI);
    }
    
    private class ConsumerRunnable implements Runnable{

        public ConsumerRunnable() {}

        public void run() {
            LOG.log(Level.INFO, "started receiving events");
            
            System.out.println("HOSTNAME:" + System.getenv("HOSTNAME"));
            System.out.println("POD's IP" + System.getenv("MY_POD_IP"));
            
         // set up house-keeping
            ObjectMapper mapper = new ObjectMapper();
//            Histogram stats = new Histogram(1, 10000000, 2);
//            Histogram global = new Histogram(1, 10000000, 2);

            // and the consumer
            KafkaConsumer<String, String> consumer = null;
            try (InputStream props = Resources.getResource("consumer.props").openStream()) {
                Properties properties = new Properties();
                properties.load(props);
                if (properties.getProperty("group.id") == null) {
                	System.out.println("I'm changing the group id");
                    properties.setProperty("group.id", "group-" + new Random().nextInt(100000));
                }
                consumer = new KafkaConsumer<>(properties);
            } catch (IOException e) {
//    			e.printStackTrace();
            	System.out.println("IO Exception!");
    		}
            consumer.subscribe(Arrays.asList("fast-messages"));
            int timeouts = 0;
            //noinspection InfiniteLoopStatement
            while (true) {
//            	System.out.println("I'M POLLING!");
                // read records with a short timeout. If we time out, we don't really care.
                ConsumerRecords<String, String> records = consumer.poll(200);
                if (records.count() == 0) {
                    timeouts++;
                } else {
                    System.out.printf("Got %d records after %d timeouts\n", records.count(), timeouts);
//                    for (ConsumerRecord<String, String> record : records) {
//                    	System.out.println("Topic: " + record.topic());
//                    	System.out.println("Partition: " + record.partition());
//                    	System.out.println("Offset: " + record.offset());
//                    	System.out.println("Value: " + record.value());
//                    }
//                    System.out.println(records);
                    timeouts = 0;
                }
//                System.out.println("OUT");
                for (ConsumerRecord<String, String> record : records) {
//                	System.out.println("TOPIC: " + record.topic());
                    switch (record.topic()) {
                        case "fast-messages":
//                        	System.out.println("I'm in fast-messages case");
                            // the send time is encoded inside the message
    					JsonNode msg = null;
    					try {
    						msg = mapper.readTree(record.value());
    					} catch (JsonProcessingException e) {
//    						e.printStackTrace();
    						System.out.println("JSON Processing Exception!");
    					} catch (IOException e) {
//    						e.printStackTrace();
    						System.out.println("IO Exception!");
    					}
//    					System.out.println("I'm in fast-messages case 1");
                            switch (msg.get("type").asText()) {
                                case "test":
//                                	System.out.println("I'm in fast-messages case test");
//                                    long latency = (long) ((System.nanoTime() * 1e-9 - msg.get("t").asDouble()) * 1000);
//                                    stats.recordValue(latency);
//                                    System.out.println("Latency recorded in stats");
//                                    global.recordValue(latency);
//                                    System.out.println("Latency recorded in global");
//                                    System.out.println(msg.get("k"));
                                	TestEvent event = new TestEvent();
//                                	System.out.println("I'm in fast-messages case test");
                                	event.setType(msg.get("type").asText());
//                                	System.out.println("I'm in fast-messages case test");
                                	event.setT(msg.get("t").asText());
//                                	System.out.println("I'm in fast-messages case test");
                                	event.setK(msg.get("k").asText());
//                                	System.out.println("I'm in fast-messages case test");
                                	events.add(event);
                                    System.out.println(msg.get("k").asInt() % 6);
                                    break;
                                case "marker":
                                	System.out.println("I'm in fast-messages case marker");
                                    // whenever we get a marker message, we should dump out the stats
                                    // note that the number of fast messages won't necessarily be quite constant
//                                    System.out.printf("%d messages received in period, latency(min, max, avg, 99%%) = %d, %d, %.1f, %d (ms)\n",
//                                            stats.getTotalCount(),
//                                            stats.getValueAtPercentile(0), stats.getValueAtPercentile(100),
//                                            stats.getMean(), stats.getValueAtPercentile(99));
//                                    System.out.printf("%d messages received overall, latency(min, max, avg, 99%%) = %d, %d, %.1f, %d (ms)\n",
//                                            global.getTotalCount(),
//                                            global.getValueAtPercentile(0), global.getValueAtPercentile(100),
//                                            global.getMean(), global.getValueAtPercentile(99));
//
//                                    stats.reset();
                                    break;
                                default:
                                    throw new IllegalArgumentException("Illegal message type: " + msg.get("type"));
                            }
//                            System.out.println("I'm in fast-messages case out");
                            break;
//                        case "summary-markers":
//                            break;
                        default:
                            throw new IllegalStateException("Shouldn't be possible to get message on topic " + record.topic());
                    }
//                    System.out.println("I'm out of switch");
                }
//                System.out.println("I'm out of for");
            }
        }
    }
    
    public void initTimer() {
    	LOG.log(Level.INFO, "INIT TIMER");
        ConsumerRunnable runnable = new ConsumerRunnable();

         //use the handle if you need to cancel scheduler or something else
         final ScheduledFuture<?> runnableHandle = scheduler.schedule(runnable, 5, TimeUnit.SECONDS);
    }
    
    public List<TestEvent> getAndRemoveEvents() {
    	ArrayList<TestEvent> eventsCopy = new ArrayList<>(events);
    	events.clear();
    	return eventsCopy;
    }

}
