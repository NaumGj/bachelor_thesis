package si.fri.diploma.listeners;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

import si.fri.diploma.models.FireObject;

import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.client.EventBean;

@ApplicationScoped
public class FireDetectionListener implements UpdateListener {
	
	public static final Logger LOG = Logger.getLogger(FireDetectionListener.class.getName());
	
	private static ArrayList<FireObject> fires = new ArrayList<FireObject>();
	
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents == null) {
            return; // ignore old events for events leaving the window
        }

        for (EventBean theEvent : newEvents) {
        	FireObject fire = new FireObject();
        	fire.setRoomNumber((Integer)theEvent.get("room"));
        	fire.setCelsiusTemperature((Double)theEvent.get("temp"));
        	fire.setObscuration((Double)theEvent.get("obscur"));
        	fires.add(fire);
        	
        	RemoveRunnable runnable = new RemoveRunnable(fire);
        	scheduler.schedule(runnable, 30, TimeUnit.SECONDS);
//        	LOG.log(Level.INFO, "room=" + theEvent.get("room").toString());
//        	LOG.log(Level.INFO, "temperature=" + theEvent.get("temp").toString());
//        	LOG.log(Level.INFO, "obscuration=" + theEvent.get("obscur").toString());
        }
    }
    
    public ArrayList<FireObject> getFires() {
    	return fires;
    }
    
    private class RemoveRunnable implements Runnable {
    	
    	private FireObject fireToDelete;
    	
    	public RemoveRunnable(FireObject fire) {
    		fireToDelete = fire;
    	}

		public void run() {
    		LOG.log(Level.INFO, "Starting remove runnable!");
    		fires.remove(fireToDelete);
		}
    }

}
