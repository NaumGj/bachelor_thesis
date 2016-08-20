package si.fri.diploma.statements;

import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.UpdateListener;

public class FireDetectionStatement {
    private EPStatement statement;

    public FireDetectionStatement(EPAdministrator admin) {
//    	String stmt = "select temperature.roomNumber as room, temperature.celsiusTemperature as temp," + 
//    				" smoke.obscuration as obscur" +
//					" from TemperatureEvent.win:time(20 sec) as temperature, SmokeEvent.win:time(25 sec) as smoke" +
//					" where temperature.roomNumber = smoke.roomNumber" +
//					" and temperature.celsiusTemperature > 40.0 and smoke.obscuration > 5.0";
    	String stmt1 = "insert into HighTemperature select * from TemperatureEvent as temperature" +
    				" where temperature.celsiusTemperature > 40.0";
    	statement = admin.createEPL(stmt1);
    	
    	String stmt2 = "insert into LotSmoke select * from SmokeEvent as smoke where smoke.obscuration > 5.0";
    	statement = admin.createEPL(stmt2);
    	
    	String stmt3 = "select temperature.roomNumber as room, temperature.celsiusTemperature as temp, smoke.obscuration as obscur" +
				" from HighTemperature.std:groupwin(roomNumber).win:time(25 sec) as temperature," + 
    			" LotSmoke.std:groupwin(roomNumber).win:time(25 sec) as smoke" +
				" where temperature.roomNumber = smoke.roomNumber";
    	statement = admin.createEPL(stmt3);
//    	String stmt1 = "insert into HighTemperature select * from TemperatureEvent as temperature" +
//				" where temperature.celsiusTemperature > 40.0";
//    	statement = admin.createEPL(stmt1);
//    	
//    	String stmt2 = "select temperature.roomNumber as room, temperature.celsiusTemperature as temp from HighTemperature as temperature";
//    	statement = admin.createEPL(stmt2);
    }

    public void addListener(UpdateListener listener) {
        statement.addListener(listener);
    }
}


