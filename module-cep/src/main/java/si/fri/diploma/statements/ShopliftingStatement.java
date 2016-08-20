package si.fri.diploma.statements;

import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.UpdateListener;

public class ShopliftingStatement {
    private EPStatement statement;

    public ShopliftingStatement(EPAdministrator admin) {
    	String stmt = "select * from pattern [every s=ShelfEvent ->" +
    				" (e=ExitEvent(productId = s.productId)" +
    				" and not PaidEvent(productId = s.productId)) where timer:within(12 hours)]";
    	statement = admin.createEPL(stmt);
    }

    public void addListener(UpdateListener listener) {
        statement.addListener(listener);
    }
}


