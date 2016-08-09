package si.fri.diploma.listeners;

import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.client.EventBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestListener implements UpdateListener {
    public void update(EventBean[] newEvents, EventBean[] oldEvents)
    {
        if (newEvents == null)
        {
            return; // ignore old events for events leaving the window
        }

        EventBean theEvent = newEvents[0];

        log.info("k=" + theEvent.get("k").toString() +
                  " and count=" + theEvent.get("cnt").toString());
    }

    private static final Log log = LogFactory.getLog(TestListener.class);
}
