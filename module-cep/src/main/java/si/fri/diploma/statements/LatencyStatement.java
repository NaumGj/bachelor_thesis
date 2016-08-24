package si.fri.diploma.statements;

import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.UpdateListener;

public class LatencyStatement {
    private EPStatement statement;

    public LatencyStatement(EPAdministrator admin) {
    	
//    	String ctx = "create context CtxSeconds initiated @now and pattern [every timer:interval(10)] terminated after 10 sec";
//    	statement = admin.createEPL(ctx);
//        String stmt = "select serialNum, avg(cast(timestampConsumed, double) - cast(timestamp, double)) as avgLatency from TestEvent.win:time_batch(10 sec)";

//        statement = admin.createEPL(stmt);
    	
    	String stmt = "context Ctx10Seconds select avg(timestampConsumed - timestamp) as avgLatency" +
					" from SimpleEvent output snapshot when terminated";
    	statement = admin.createEPL(stmt);
    }

    public void addListener(UpdateListener listener) {
        statement.addListener(listener);
    }
}
