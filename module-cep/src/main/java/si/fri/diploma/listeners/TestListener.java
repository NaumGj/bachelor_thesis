package si.fri.diploma.listeners;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.client.EventBean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import si.fri.diploma.models.CountsObject;

@ApplicationScoped
public class TestListener implements UpdateListener {
	
	private static final Integer COUNTS_SIZE = 30;
	private static LinkedList<CountsObject> counts = new LinkedList<CountsObject>();
	
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents == null) {
            return; // ignore old events for events leaving the window
        }

        EventBean theEvent = newEvents[0];

//        log.info("count=" + theEvent.get("cnt").toString());
        if(theEvent != null) {
        	Integer cnt = (Integer)theEvent.get("cnt");
        	if (cnt != null) {
	        	CountsObject count = new CountsObject();
	        	count.setCounts(cnt);
	        	counts.addFirst(count);
	        	while (counts.size() > COUNTS_SIZE) {
	        		counts.removeLast();
	        	}
        	}
        }
//        counts.add(Integer.parseInt(theEvent.get("cnt").toString()));
//        log.info("COUNTS ARRAY: " + counts.toString());
//        log.info("COUNTS SIZE: " + counts.size());
    }

    private static final Log log = LogFactory.getLog(TestListener.class);
    
    public LinkedList<CountsObject> getCounts() {
//    	log.info("COUNTS ARRAY IN getCounts(): " + counts.toString());
//        log.info("COUNTS SIZE IN getCounts(): " + counts.size());
//    	ArrayList<Integer> countsCopy = new ArrayList<>(counts);
//    	return countsCopy;
    	return counts;
    }
}
