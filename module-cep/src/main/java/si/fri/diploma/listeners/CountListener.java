package si.fri.diploma.listeners;

import java.util.LinkedList;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.client.EventBean;

import si.fri.diploma.models.CountsObject;

@ApplicationScoped
public class CountListener implements UpdateListener {
	
	public static final Logger LOG = Logger.getLogger(CountListener.class.getName());
	
	private static final Integer LIST_SIZE = 30;
	private static LinkedList<CountsObject> counts = new LinkedList<CountsObject>();
	
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents == null) {
            return; // ignore old events for events leaving the window
        }

        for (EventBean theEvent : newEvents) {
//        	log.info("count=" + theEvent.get("cnt").toString());
	        if(theEvent != null) {
	        	Long cnt = (Long)theEvent.get("cnt");
	        	if (cnt != null) {
		        	CountsObject count = new CountsObject();
		        	count.setCounts(cnt);
		        	counts.addLast(count);
		        	while (counts.size() > LIST_SIZE) {
		        		counts.removeFirst();
		        	}
	        	}
	        }
        }
    }

    public LinkedList<CountsObject> getCounts() {
    	return counts;
    }
}
