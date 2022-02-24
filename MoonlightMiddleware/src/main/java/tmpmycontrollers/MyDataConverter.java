package tmpmycontrollers;

import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.signal.DataHandler;
import eu.quanticol.moonlight.signal.RecordHandler;
import eu.quanticol.moonlight.signal.online.Update;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.List;


public class MyDataConverter {

    RecordHandler factory;


    public MyDataConverter(){
        factory = new RecordHandler(DataHandler.REAL, DataHandler.REAL, DataHandler.REAL, DataHandler.REAL);
    }

    public MoonLightRecord convertJSONtoMoonlightRecord(JSONObject j){
        /*For the moment I suppose that the JSON file is like:
         {
            "temp": 12.4,
            "hum": 23.54,
            "CO2": 340.1,
            "TVOC": 5.4
         }
         */
        double temp = (double) j.get("temp");
        double hum = (double) j.get("hum");
        double co2 = (double) j.get("CO2");
        double tvoc = (double) j.get("TVOC");

        MoonLightRecord moonLightRecord = factory.fromObjectArray(temp, hum, co2, tvoc);

        return moonLightRecord;
    }

    public JSONObject convertStringtoJSONObject(String s){
        Object obj= JSONValue.parse(s);
        JSONObject jsonObject = (JSONObject) obj;
        return jsonObject;
    }

    /*temporal*/ double j = 0;
    public Update<Double, List<MoonLightRecord>>  createUpdate(List<MoonLightRecord> records){
        // I don't know how to specify the time
        return new Update<Double, List<MoonLightRecord>>(j, ++j, records);
    }
}
