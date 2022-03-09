package data;

import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.signal.RecordHandler;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.Arrays;
import java.util.List;

//TODO: Needs changes
public class OfficeMoolightRecordParser {
    RecordHandler factory;
    List<String> places;

    public OfficeMoolightRecordParser(){
        places = Arrays.asList("Hospital", "School", "MetroStop", "School", "MainSquare", "BusStop");
    }

    public MoonLightRecord getAndConvertMoonLightRecords(String s){
        JSONObject object = convertStringtoJSONObject(s);
        return convertJSONtoMoonlightRecord(object);
    }

    public JSONObject convertStringtoJSONObject(String s){
        Object obj= JSONValue.parse(s);

        return (JSONObject) obj;
    }

    public MoonLightRecord convertJSONtoMoonlightRecord(JSONObject j){
        //TODO: update converter
        /*For the moment I suppose that the JSON file is like:
         {
            "place": 3
            "noise": 40
            "people": 30
         }*/
        try{
            long place = (long) j.get("place");
            long noise = (long) j.get("noise");
            long people = (long) j.get("people");

            MoonLightRecord moonLightRecord = factory.fromObjectArray(places.get(Math.toIntExact(place)), Math.toIntExact(noise), Math.toIntExact(people));
            return moonLightRecord;
        }catch (NullPointerException e){
            throw new UnsupportedOperationException("Invalid data format");
        }
    }

    //List<String> places;
    //public List<MoonLightRecord> initDataConverter(int size){
    //    moonLightRecords = new ArrayList<>();
    //    //TODO: change the default values
    //    factory = new RecordHandler(new EnumerationHandler<>(String.class, places.toArray(new String[0])), DataHandler.INTEGER, DataHandler.INTEGER);
    //    for (int i = 0; i < size; i++) {
    //        moonLightRecords.add(factory.fromObjectArray(places.get(i), 60, 25));
    //    }
    //    return moonLightRecords;
    //}
}
