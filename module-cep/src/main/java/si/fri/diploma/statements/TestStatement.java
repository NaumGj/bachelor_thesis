package si.fri.diploma.statements;

import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.UpdateListener;

public class TestStatement {
    private EPStatement statement;

    public TestStatement(EPAdministrator admin) {
        String stmt = "select k, count(*) as cnt from TestEvent.win:time_batch(10 sec)";

        statement = admin.createEPL(stmt);
    }

    public void addListener(UpdateListener listener)
    {
        statement.addListener(listener);
    }
}


