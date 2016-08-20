package si.fri.diploma.listeners;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.client.EventBean;

import si.fri.diploma.models.LatencyObject;

@ApplicationScoped
public class LatencyListener implements UpdateListener {
	
	public static final Logger LOG = Logger.getLogger(LatencyListener.class.getName());
	
	private static final Integer LIST_SIZE = 30;
	
	private static LinkedList<LatencyObject> avgLatencies = new LinkedList<LatencyObject>();
	
    public void update(EventBean[] newEvents, EventBean[] oldEvents)
    {
        if (newEvents == null)
        {
            return; // ignore old events for events leaving the window
        }

        for (EventBean theEvent : newEvents) {
	        if(theEvent != null) {
//        		if(theEvent.get("avgLatency") != null) {
//        			log.info("ID: " + theEvent.get("serialNum").toString());
//        			log.info("avgLatency=" + theEvent.get("avgLatency").toString());
//        		}
	        	if(theEvent.get("avgLatency") != null) {
	        		boolean canReadLatency = true;
	        		Double avgLatency = null;
	        		try {
		        		avgLatency = (Double)theEvent.get("avgLatency");
		        		avgLatency *= 1e-3;	//convert to seconds
	        		} catch(Exception e) {
	        			LOG.log(Level.WARNING, "Cannot cast the average latency to Double.");
	            		canReadLatency = false;
	            	}
	        		if(canReadLatency) {
	        			LatencyObject latency = new LatencyObject();
		        		latency.setTen(avgLatency);
		        		avgLatencies.addLast(latency);
		        		while (avgLatencies.size() > LIST_SIZE) {
		        			avgLatencies.removeFirst();
			        	}
	        		}
	        	}
	        }
        }
    }

    public LinkedList<LatencyObject> getAvgLatencies() {
    	return avgLatencies;
    }
    
}
