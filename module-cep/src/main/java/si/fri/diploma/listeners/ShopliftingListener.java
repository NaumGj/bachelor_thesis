package si.fri.diploma.listeners;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.event.map.MapEventBean;

import si.fri.diploma.models.ShelfEvent;
import si.fri.diploma.models.ExitEvent;
import si.fri.diploma.models.StolenProductObject;

@ApplicationScoped
public class ShopliftingListener implements UpdateListener {
	
	public static final Logger LOG = Logger.getLogger(ShopliftingListener.class.getName());
	
	private static ArrayList<StolenProductObject> stolenProducts = new ArrayList<StolenProductObject>();
	
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	private final RemoveRunnable runnable = new RemoveRunnable();
	
	private final ScheduledFuture<?> resetStolenProducts = scheduler.schedule(runnable, 12, TimeUnit.HOURS);
	
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents == null) {
            return; // ignore old events for events leaving the window
        }

        for (EventBean theEvent : newEvents) {
        	StolenProductObject stolenProduct = new StolenProductObject();
        	stolenProduct.setProductId(((ShelfEvent)theEvent.get("s")).getProductId());
        	stolenProducts.add(stolenProduct);
        	
//        	LOG.log(Level.INFO, ((MapEventBean)theEvent).getProperties().toString());
//        	LOG.log(Level.INFO, ((ShelfEvent)theEvent.get("s")).getProductId());
//        	LOG.log(Level.INFO, ((ExitEvent)theEvent.get("e")).getProductId());
        	
        }
    }
    
    public ArrayList<StolenProductObject> getStolenProducts() {
    	return stolenProducts;
    }
    
    private class RemoveRunnable implements Runnable {
    	
    	public RemoveRunnable() {}

		public void run() {
    		LOG.log(Level.INFO, "Reseting stolen products!");
    		stolenProducts.clear();
		}
    }

}
