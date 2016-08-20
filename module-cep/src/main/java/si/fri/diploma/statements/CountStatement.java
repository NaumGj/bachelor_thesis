package si.fri.diploma.statements;

import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.UpdateListener;

public class CountStatement {
    private EPStatement statement;

    public CountStatement(EPAdministrator admin) {
//        String stmt = "select count(*) as cnt from TestEvent.win:time(10 sec)";
//
//        statement = admin.createEPL(stmt);
        
        String ctx = "create context CtxSeconds initiated @now and pattern [every timer:interval(10)] terminated after 10 sec";
    	statement = admin.createEPL(ctx);
    	
    	String stmt = "context CtxSeconds select count(*) as cnt" +
					" from TestEvent output snapshot when terminated";
    	statement = admin.createEPL(stmt);
    }

    public void addListener(UpdateListener listener) {
        statement.addListener(listener);
    }
}


