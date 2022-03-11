package messages;

import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.signal.DataHandler;
import eu.quanticol.moonlight.signal.EnumerationHandler;
import eu.quanticol.moonlight.signal.RecordHandler;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.Arrays;
import java.util.List;

public class OfficeMessage extends Message<MoonLightRecord>{

    RecordHandler factory;
    List<String> places;

    public OfficeMessage(){
        places = Arrays.asList("Hospital", "School", "MetroStop", "School", "MainSquare", "BusStop");
        factory = new RecordHandler(new EnumerationHandler<>(
                String.class, places.toArray(new String[0])), DataHandler.INTEGER, DataHandler.INTEGER);
    }

    @Override
    public void transformReceivedData(String topic, String message) {
        String[] topics = topic.split("/");
        id = Integer.parseInt(topics[topics.length-1]);
        //TODO: Change the way the time is set
        time = 1;

        JSONObject obj = (JSONObject) JSONValue.parse(message);
        convertJSONtoMoonlightRecord(obj);
    }

    public void convertJSONtoMoonlightRecord(JSONObject j){
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

            valueElement = factory.fromObjectArray(places.get(
                    Math.toIntExact(place)), Math.toIntExact(noise), Math.toIntExact(people));

        //TODO: make the try catch cleaner
        }catch (NullPointerException e){
            throw new UnsupportedOperationException("Invalid data format");
        }
    }
}
