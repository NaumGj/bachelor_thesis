package si.fri.diploma.listeners;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.client.EventBean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import si.fri.diploma.models.LatencyObject;

@ApplicationScoped
public class LatencyListener implements UpdateListener {
	
//	private static ArrayList<Integer> avgLatencies = new ArrayList<Integer>();
	private static LatencyObject avgLatency = new LatencyObject();
	
    public void update(EventBean[] newEvents, EventBean[] oldEvents)
    {
        if (newEvents == null)
        {
            return; // ignore old events for events leaving the window
        }

        EventBean theEvent = newEvents[0];

        if(theEvent != null) {
//        	if(theEvent.get("avgLatency") != null) {
//        		log.info("ID: " + theEvent.get("serialNum").toString());
//        		log.info("avgLatency=" + theEvent.get("avgLatency").toString());
//        	}
        	try {
        		avgLatency.setFifteen((Double)theEvent.get("avgLatency"));
        	} catch(Exception e) {
        		log.warn("Cannot cast the average latency to Double.");
        	}
        }
//        avgLatencies.add(Integer.parseInt(theEvent.get("cnt").toString()));
//        log.info("COUNTS ARRAY: " + counts.toString());
//        log.info("COUNTS SIZE: " + counts.size());
    }

    private static final Log log = LogFactory.getLog(LatencyListener.class);
    
    public LatencyObject getLastAvgLatency() {
    	return avgLatency;
    }
    
}
