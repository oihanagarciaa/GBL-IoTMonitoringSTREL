package dataconverter;

import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.signal.DataHandler;
import eu.quanticol.moonlight.signal.EnumerationHandler;
import eu.quanticol.moonlight.signal.RecordHandler;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConverterStringMoonlightRecord {
    RecordHandler factory;
    List<MoonLightRecord> moonLightRecords;


    public List<MoonLightRecord> getMoonLightRecords(String s){
        JSONObject object = convertStringtoJSONObject(s);
        convertJSONtoMoonlightRecord(object);
        return moonLightRecords;
    }

    public JSONObject convertStringtoJSONObject(String s){
        Object obj= JSONValue.parse(s);

        return (JSONObject) obj;
    }

    public void convertJSONtoMoonlightRecord(JSONObject j){
        //TODO: update converter
        /*For the moment I suppose that the JSON file is like:
         {
            "id": 2
            "place": 3
            "noise": 40
            "people": 30
         }*/
        long id = (long) j.get("id");
        long noise = (long) j.get("noise");
        long people = (long) j.get("people");

        MoonLightRecord moonLightRecord = factory.fromObjectArray(places.get(Math.toIntExact(id)), Math.toIntExact(noise), Math.toIntExact(people));

        moonLightRecords.set(Math.toIntExact(id), moonLightRecord);
    }

    List<String> places;
    public List<MoonLightRecord> initDataConverter(int size){
        moonLightRecords = new ArrayList<>();
        //TODO: change the default values
        places = Arrays.asList("Hospital", "School", "MetroStop", "School", "MainSquare", "BusStop");
        factory = new RecordHandler(new EnumerationHandler<>(String.class, places.toArray(new String[0])), DataHandler.INTEGER, DataHandler.INTEGER);
        for (int i = 0; i < size; i++) {
            moonLightRecords.add(factory.fromObjectArray(places.get(i), 60, 25));
        }
        return moonLightRecords;
    }
}
